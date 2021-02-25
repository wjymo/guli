package com.zzn.estest;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.ByteString;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static com.zzn.estest.canal.CanalClient.CONNECTOR;

@Slf4j
@ActiveProfiles(profiles = "test")
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCanal {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private DeviceWarnMapper deviceWarnMapper;

    @Value("${wjy.database.name}")
    private String databaseName;

    //    private final static String databaseName="wjyefsvmap";
//    private final static String databaseName="efsvmap";
    public static final FastDateFormat ISO_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM");

    @Test
    public void testCanalSync() {
        for (int i = 0; i < 3; i++) {
            int batchSize = 1000;
            Message message = CONNECTOR.getWithoutAck(batchSize); // 获取指定数量的数据
            long batchId = message.getId();
            List<CanalEntry.Entry> entries = message.getEntries();
            if (batchId != -1 && !CollectionUtils.isEmpty(entries)) {
                try {
                    for (CanalEntry.Entry entry : entries) {
                        if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                            //解析处理
                            String logFileName = entry.getHeader().getLogfileName();
                            long offset = entry.getHeader().getLogfileOffset();
                            CanalEntry.EventType eventType = entry.getHeader().getEventType();
                            String database = entry.getHeader().getSchemaName();
                            String table = entry.getHeader().getTableName();
                            if (StringUtils.equals(database, databaseName) && StringUtils.equals(table, "de_device_warn")) {
                                ByteString storeValue = entry.getStoreValue();
                                CanalEntry.RowChange change = null;
                                try {
                                    change = CanalEntry.RowChange.parseFrom(storeValue);
                                } catch (InvalidProtocolBufferException e) {
                                    e.printStackTrace();
                                    return;
                                }
                                for (CanalEntry.RowData rowData : change.getRowDatasList()) {
                                    List<CanalEntry.Column> columns = null;
                                    if (eventType == CanalEntry.EventType.INSERT || eventType == CanalEntry.EventType.UPDATE) {
                                        columns = rowData.getAfterColumnsList();
                                    } else if (eventType == CanalEntry.EventType.DELETE) {
                                        columns = rowData.getBeforeColumnsList();
                                    }
                                    if (!CollectionUtils.isEmpty(columns)) {
                                        String primaryKey = "id";
                                        CanalEntry.Column idColumn = columns.stream().filter(column -> column.getIsKey()
                                                && primaryKey.equals(column.getName())).findFirst().orElse(null);
                                        if (idColumn == null) {
                                            System.out.println("没有新增或修改的数据,eventType为：" + eventType.name());
                                            return;
                                        }
                                        String idColumnValue = idColumn.getValue();
                                        try {
                                            log.info("当前正在处理数据库：{}，表：{}，操作类型为：{}，binlog：{}，position为：{}",
                                                    database, table, eventType.name(), logFileName, offset);
                                            indexESByEventType(eventType, idColumnValue, database, table);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            throw e;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CONNECTOR.rollback(batchId);
                    return;
                }
                int i1 = 10 / 2;
                CONNECTOR.ack(batchId);
            }
        }
    }

    private void indexESByEventType(CanalEntry.EventType eventType, String id, String database, String table) throws IOException {
        if (!StringUtils.equals(databaseName, database)) {
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
            }
            String s = response.toString();
            log.info("新增的状态:{},{} ,详细信息：{}", name, status, s);
        } else if (eventType == CanalEntry.EventType.DELETE) {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, "_doc", id);
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            RestStatus restStatus = deleteResponse.status();
            int status = restStatus.getStatus();
            String name = restStatus.name();
            String s = deleteResponse.toString();
            log.info("删除的状态:{},{} ,详细信息：{}", name, status, s);
        } else {
//            log.info("当前binlog的event的状态是：{}", eventType.name());
        }
    }

    public int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);  //美国是以周日为每周的第一天 现把周一设成第一天
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

}
