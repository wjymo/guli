package com.zzn.estest.canal;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.zzn.estest.dao.DeviceWarnMapper;
import com.zzn.estest.vo.RiskDeDeviceWarnVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static com.zzn.estest.canal.CanalClient.CONNECTOR;

@Component
@Slf4j
public class CanalScheduled {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private DeviceWarnMapper deviceWarnMapper;

    public LongAdder longAdder = new LongAdder();
    public static final FastDateFormat ISO_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM");

//    @Scheduled(fixedDelay = 10 * 1000)
    public void getBatch() {
        int batchSize = 1000;
        Message message = null; // 获取指定数量的数据
        try {
            message = CONNECTOR.getWithoutAck(batchSize,10l, TimeUnit.SECONDS);
        } catch (CanalClientException e) {
            log.error("出现异常：{}",e.getMessage(),e);
            return;
        }
        long batchId = message.getId();
        List<CanalEntry.Entry> entries = message.getEntries();
        if (batchId != -1 && !CollectionUtils.isEmpty(entries)) {
            try {
                for (CanalEntry.Entry entry : entries) {
                    if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                        //解析处理
                        publishCanalEvent(entry);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                CONNECTOR.rollback(batchId);
                return;
            }
            System.out.println(1);
            CONNECTOR.ack(batchId);
        } else {

        }

    }


    private void publishCanalEvent(CanalEntry.Entry entry) throws IOException {
        String logFileName = entry.getHeader().getLogfileName();
        long offset = entry.getHeader().getLogfileOffset();
        log.info("当前正在处理binlog：{}，position为：{}",logFileName,offset);
        CanalEntry.EventType eventType = entry.getHeader().getEventType();
//        if (CanalEntry.EventType.DELETE == eventType) {
//            System.out.println("删除数据，直接返回");
//            return;
//        }
        String database = entry.getHeader().getSchemaName();
        String table = entry.getHeader().getTableName();
        if (StringUtils.equals(database, "wjyefsvmap") && StringUtils.equals(table, "de_device_warn")) {
            CanalEntry.RowChange change = null;
            try {
                change = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                return;
            }
            for (CanalEntry.RowData rowData : change.getRowDatasList()) {
                List<CanalEntry.Column> columns =null;
                if(eventType == CanalEntry.EventType.INSERT || eventType == CanalEntry.EventType.UPDATE){
                    columns=rowData.getAfterColumnsList();
                }else if (eventType == CanalEntry.EventType.DELETE){
                    columns=rowData.getBeforeColumnsList();
                }
                if(!CollectionUtils.isEmpty(columns)){
                    String primaryKey = "id";
                    CanalEntry.Column idColumn = columns.stream().filter(column -> column.getIsKey()
                            && primaryKey.equals(column.getName())).findFirst().orElse(null);
                    String idColumnValue = idColumn.getValue();
                    if (idColumn == null) {
                        System.out.println("没有新增或修改的数据,eventType为：" + eventType.name());
                        return;
                    }
                    Map<String, Object> dataMap = parseColumnsToMap(columns);
                    try {
//                    indexES(dataMap, database, table);
//                        indexESByEventType(eventType,idColumnValue,database,table);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
            }
        }
    }

    Map<String, Object> parseColumnsToMap(List<CanalEntry.Column> columns) {
        Map<String, Object> jsonMap = new HashMap<>();
        columns.forEach(column -> {
            if (column == null) {
                return;
            }
            jsonMap.put(column.getName(), column.getValue());
        });
        return jsonMap;
    }

    private void indexES(Map<String, Object> dataMap, String database, String table) throws IOException {
        if (!StringUtils.equals("wjyefsvmap", database)) {
            return;
        }
        RiskDeDeviceWarnVo deviceWarnVo = null;
        if (StringUtils.equals("de_device_warn", table)) {
            deviceWarnVo = deviceWarnMapper.getEsResultById(Long.parseLong(String.valueOf(dataMap.get("id"))));
        } else {
            return;
        }

        if (Objects.nonNull(deviceWarnVo)) {
            Date date = new Date();
            String format = ISO_DATE_FORMAT.format(date);
            int week = getWeek(date);
            String indexName = "device_warn_" + format + "-" + week;
//            int i = 10 / 0;
            IndexRequest indexRequest = new IndexRequest(indexName, "_doc");
            indexRequest.id(String.valueOf(deviceWarnVo.getWarnId()));
            indexRequest.source(JSON.toJSONString(deviceWarnVo), XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            RestStatus restStatus = response.status();
            int status = restStatus.getStatus();
            if (!Objects.equals(RestStatus.CREATED.getStatus(), status) && !Objects.equals(RestStatus.OK.getStatus(), status)) {
                //新增失败
                //throw new RuntimeException("处理失败");
                RestStatus[] values = RestStatus.values();
                for (RestStatus value : values) {
                    if (Objects.equals(status, value.getStatus())) {
                        System.out.println(value.name());
                    }
                }
            } else {
                longAdder.increment();
            }
            String s = response.toString();
            log.info("新增的状态:{}", s);
        }
    }


    private void indexESByEventType(CanalEntry.EventType eventType, String id, String database, String table) throws IOException {
        if (!StringUtils.equals("wjyefsvmap", database)) {
            return;
        }
        RiskDeDeviceWarnVo deviceWarnVo = null;
        if (StringUtils.equals("de_device_warn", table) && eventType != CanalEntry.EventType.DELETE) {
            deviceWarnVo = deviceWarnMapper.getEsResultById(Long.parseLong(id));
        }

        Date date = new Date();
        String format = ISO_DATE_FORMAT.format(date);
        int week = getWeek(date);
        String indexName = "device_warn_" + format + "-" + week;
        if (Objects.nonNull(deviceWarnVo)) {
//            int i = 10 / 0;
            IndexRequest indexRequest = new IndexRequest(indexName, "_doc");
            indexRequest.id(id);
            indexRequest.source(JSON.toJSONString(deviceWarnVo), XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            RestStatus restStatus = response.status();
            int status = restStatus.getStatus();
            String name = restStatus.name();
            if (!Objects.equals(RestStatus.CREATED.getStatus(), status) && !Objects.equals(RestStatus.OK.getStatus(), status)) {
                //新增失败
                //throw new RuntimeException("处理失败");
                RestStatus[] values = RestStatus.values();
                for (RestStatus value : values) {
                    if (Objects.equals(status, value.getStatus())) {
                        System.out.println(value.name());
                    }
                }
            } else {
                longAdder.increment();
            }
            String s = response.toString();
            log.info("新增的状态:{},{} ,详细信息：{}",name, status,s);
        } else if (eventType == CanalEntry.EventType.DELETE) {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, "_doc", id);
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            RestStatus restStatus = deleteResponse.status();
            int status = restStatus.getStatus();
            String name = restStatus.name();
            String s = deleteResponse.toString();
            log.info("删除的状态:{},{} ,详细信息：{}", name,status,s);
            longAdder.decrement();
        } else {
            log.info("当前binlog的event的状态是：{}", eventType.name());
        }
    }

    private void indexESByEventTypeAndCloumnMap(CanalEntry.EventType eventType, Map<String, Object> dataMap, String database, String table) throws IOException {
        if (!StringUtils.equals("wjyefsvmap", database)) {
            return;
        }
        RiskDeDeviceWarnVo deviceWarnVo = null;
        if (StringUtils.equals("de_device_warn", table) && eventType != CanalEntry.EventType.DELETE) {
            deviceWarnVo=new RiskDeDeviceWarnVo();
            Field[] declaredFields = RiskDeDeviceWarnVo.class.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                String fieldName = declaredField.getName();
                declaredField.setAccessible(true);
                Object o = dataMap.get(fieldName);
                try {
                    declaredField.set(deviceWarnVo,o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        Date date = new Date();
        String format = ISO_DATE_FORMAT.format(date);
        int week = getWeek(date);
        String indexName = "device_warn_" + format + "-" + week;
        if (Objects.nonNull(deviceWarnVo)) {
//            int i = 10 / 0;
            IndexRequest indexRequest = new IndexRequest(indexName, "_doc");
            indexRequest.id(String.valueOf(deviceWarnVo.getWarnId()));
            indexRequest.source(JSON.toJSONString(deviceWarnVo), XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            RestStatus restStatus = response.status();
            int status = restStatus.getStatus();
            String name = restStatus.name();
            if (!Objects.equals(RestStatus.CREATED.getStatus(), status) && !Objects.equals(RestStatus.OK.getStatus(), status)) {
                //新增失败
                //throw new RuntimeException("处理失败");
                RestStatus[] values = RestStatus.values();
                for (RestStatus value : values) {
                    if (Objects.equals(status, value.getStatus())) {
                        System.out.println(value.name());
                    }
                }
            } else {
                longAdder.increment();
            }
            String s = response.toString();
            log.info("新增的状态:{},{} ,详细信息：{}",name, status,s);
        } else if (eventType == CanalEntry.EventType.DELETE) {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, "_doc", String.valueOf(deviceWarnVo.getWarnId()));
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            RestStatus restStatus = deleteResponse.status();
            int status = restStatus.getStatus();
            String name = restStatus.name();
            String s = deleteResponse.toString();
            log.info("删除的状态:{},{} ,详细信息：{}", name,status,s);
            longAdder.decrement();
        } else {
            log.info("当前binlog的event的状态是：{}", eventType.name());
        }
    }


    public static int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);  //美国是以周日为每周的第一天 现把周一设成第一天
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }
}
