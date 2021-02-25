package com.zzn.kafkatest.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zzn.kafkatest.dao.DeviceWarnMapper;
import com.zzn.kafkatest.entity.DeviceWarn;
import com.zzn.kafkatest.query.RiskDeDeviceWarnQuery;
import com.zzn.kafkatest.schedule.KafkaConsumerScheduled;
import com.zzn.kafkatest.util.CanalScheduled;
import com.zzn.kafkatest.util.DropEsDataScheduled;
import com.zzn.kafkatest.vo.RiskDeDeviceWarnVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Transactional(propagation = Propagation.SUPPORTS)
@Service
@Slf4j
public class DeviceWarnService {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    public static final FastDateFormat ISO_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM");
    public static final FastDateFormat ISO_DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private DeviceWarnMapper deviceWarnMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Transactional
    public Integer addDeviceWran(DeviceWarn deviceWarn) {
        Integer integer = deviceWarnMapper.insertOne(deviceWarn);
        return integer;
    }

    @Transactional
    public Integer updateDeviceWran(Long id, String paiCode) {
        Integer integer = deviceWarnMapper.updateById(id, paiCode);
        return integer;
    }

    @Transactional
    public Integer delDeviceWran(Long id) {
        Integer integer = deviceWarnMapper.delById(id);
        return integer;
    }

    public Map<String, Object> getByQuery(RiskDeDeviceWarnQuery query) throws IOException, ExecutionException, InterruptedException {
        Date date = new Date();
        String format = CanalScheduled.ISO_DATE_FORMAT.format(date);
        int week = CanalScheduled.getWeek(date);
//        String yearMonthWeek = format + "-" + week;
        String yearMonthWeek = format ;
        String indexName = "device_warn_" + yearMonthWeek;

        CompletableFuture<Map<String, Object>>[] completableFutures = new CompletableFuture[2];
        CompletableFuture<Map<String, Object>> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                return getInEs(query, indexName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        completableFutures[0] = future1;

        String startTime = query.getStartTime();
        String endTime = query.getEndTime();
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        try {
            Date start = fastDateFormat.parse(startTime);
            String startFormat = CanalScheduled.ISO_DATE_FORMAT.format(start);
            int startWeek = CanalScheduled.getWeek(start);
            String startYearMonthWeek = startFormat + "-" + startWeek;

            Date end = fastDateFormat.parse(endTime);
            String endFormat = CanalScheduled.ISO_DATE_FORMAT.format(end);
            int endWeek = CanalScheduled.getWeek(end);
            String endYearMonthWeek = endFormat + "-" + endWeek;
            if (!StringUtils.equals(startYearMonthWeek, endYearMonthWeek)) {
                if (!StringUtils.equals(startYearMonthWeek, yearMonthWeek) && StringUtils.equals(endYearMonthWeek, yearMonthWeek)) {
                    //搜索的开始时间不等于当前时间，但等于结束时间等于当前时间，说明还需要之前的一个索引进行查询
                    //只有这一种可能
                    String beforeDaysForIndex = DropEsDataScheduled.getBeforeDaysForIndex(7);
//                    CompletableFuture<Map<String, Object>> future2 = CompletableFuture.supplyAsync(() -> {
//                        try {
//                            return getInEs(query, beforeDaysForIndex);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    });
//                    completableFutures[1]=future2;
                } else {
//                    throw new RuntimeException("出鬼了！");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (completableFutures[1] == null) {
            CompletableFuture<Map<String, Object>> completableFuture = completableFutures[0];
            Map<String, Object> map = completableFuture.get();
            return map;
        }else {
            CompletableFuture.allOf(completableFutures).join();
            for (CompletableFuture<Map<String, Object>> completableFuture : completableFutures) {
                Map<String, Object> map = completableFuture.get();
            }
        }



        return null;
    }

    public Map<String, Object> getByQueryInMonth(RiskDeDeviceWarnQuery query) throws IOException, ExecutionException, InterruptedException {
        Date date = new Date();
        String format = CanalScheduled.ISO_DATE_FORMAT.format(date);
        String yearMonth = format ;
        String indexName = "device_warn_" + yearMonth;

        CompletableFuture<Map<String, Object>>[] completableFutures = new CompletableFuture[2];
        CompletableFuture<Map<String, Object>> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                return getInEs(query, indexName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        completableFutures[0] = future1;

        String startTime = query.getStartTime();
        String endTime = query.getEndTime();
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        try {
            Date start = fastDateFormat.parse(startTime);
            String startFormat = CanalScheduled.ISO_DATE_FORMAT.format(start);
            String startYearMonth = startFormat;

            Date end = fastDateFormat.parse(endTime);
            String endFormat = CanalScheduled.ISO_DATE_FORMAT.format(end);
            String endYearMonth = endFormat;
            if (!StringUtils.equals(startYearMonth, endYearMonth)) {
                if (!StringUtils.equals(startYearMonth, yearMonth) && StringUtils.equals(endYearMonth, yearMonth)) {
                    //搜索的开始时间不等于当前时间，但等于结束时间等于当前时间，说明还需要之前的一个索引进行查询
                    //只有这一种可能
                    String beforeIndex = startYearMonth;
                    CompletableFuture<Map<String, Object>> future2 = CompletableFuture.supplyAsync(() -> {
                        try {
                            return getInEs(query, beforeIndex);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
                    completableFutures[1]=future2;
                } else {
                    throw new RuntimeException("！");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (completableFutures[1] == null) {
            CompletableFuture<Map<String, Object>> completableFuture = completableFutures[0];
            Map<String, Object> map = completableFuture.get();
            return map;
        }else {
            CompletableFuture.allOf(completableFutures).join();
            for (CompletableFuture<Map<String, Object>> completableFuture : completableFutures) {
                Map<String, Object> map = completableFuture.get();
            }
        }



        return null;
    }

    Map<String, Object> getInEs(RiskDeDeviceWarnQuery query, String indexName) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("alarmTime")
                .gte(query.getStartTime()).lte(query.getEndTime());
        if (StringUtils.isNotEmpty(query.getAlarmType())) {
            TermQueryBuilder termQueryBuilder1 = QueryBuilders.termQuery("alarmType", query.getAlarmType());
            boolBuilder.filter(termQueryBuilder1);
        }
        if (StringUtils.isNotEmpty(query.getAlarmSubType())) {
            TermQueryBuilder termQueryBuilder2 = QueryBuilders.termQuery("alarmSubType", query.getAlarmSubType());
            boolBuilder.filter(termQueryBuilder2);
        }
        if (StringUtils.isNotEmpty(query.getOrg())) {
            TermsQueryBuilder termsQueryBuilder3 = QueryBuilders.termsQuery("org", query.getOrg());
            boolBuilder.filter(termsQueryBuilder3);
        }
        if (StringUtils.isNotEmpty(query.getAlarmLevel())) {
            TermQueryBuilder termQueryBuilder4 = QueryBuilders.termQuery("alarmLevel", query.getAlarmLevel());
            boolBuilder.filter(termQueryBuilder4);
        }
        TermsQueryBuilder termsQueryBuilder5 = QueryBuilders.termsQuery("taskStatus", "0", "1", "2", "3", "4", "5");
        if(StringUtils.isNotEmpty(query.getTenantId())){
            TermQueryBuilder termQueryBuilder6 = QueryBuilders.termQuery("tenantId", query.getTenantId());
            boolBuilder.filter(termQueryBuilder6);
        }

        boolBuilder.filter(rangeQueryBuilder)
                .filter(termsQueryBuilder5);
        boolBuilder.must(QueryBuilders.matchQuery("carplate", query.getCarplate()));
        boolBuilder.mustNot(QueryBuilders.termQuery("auditResult", 2));
        sourceBuilder.query(boolBuilder);
        sourceBuilder.sort(SortBuilders.fieldSort("_id").order(SortOrder.DESC));

        sourceBuilder.from((query.getPageNo() - 1) * query.getPageSize()).size(query.getPageSize());
        searchRequest.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        long totalHits = response.getHits().getTotalHits();
        SearchHit[] hits = response.getHits().getHits();
        List<JSONObject> collect = Arrays.asList(hits).stream().map(hit -> {
            String sourceAsString = hit.getSourceAsString();
//            RiskDeDeviceWarnVo riskDeDeviceWarnVo = JSON.parseObject(sourceAsString, RiskDeDeviceWarnVo.class);
            return JSON.parseObject(sourceAsString);
        }).collect(Collectors.toList());
        List<RiskDeDeviceWarnVo> collect1 = Arrays.asList(hits).stream().map(hit -> {
            String sourceAsString = hit.getSourceAsString();
            RiskDeDeviceWarnVo riskDeDeviceWarnVo = JSON.parseObject(sourceAsString, RiskDeDeviceWarnVo.class);
            return riskDeDeviceWarnVo;
        }).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("pageNo", query.getPageNo());
        map.put("pageSize", query.getPageSize());
        map.put("total", totalHits);
        map.put("hits", collect);
        return map;
    }


    @Transactional
    public void processData(ConsumerRecord<Object, String> record){
//        int i = 10 / 0;
        System.out.printf("partition=%d,offset=%d, key=%s, value=%s%n",
                record.partition(), record.offset(), record.key(), record.value());
        String value = record.value();
        DeviceWarn deviceWarn = JSON.parseObject(value, DeviceWarn.class);
        //插入时有from_id作为唯一建，可保证消费端幂等性
        Integer integer = deviceWarnMapper.insertOne(deviceWarn);
        if(integer!=1){
            throw new RuntimeException("插入mysql失败!");
        }

        //插入mysql成功后，再连表查询告警信息，然后再录入es
        deviceWarn.setAlarmLocation(null);
        try {
            deviceWarn.setAlarmTime(ISO_DATE_TIME_FORMAT.parse(ISO_DATE_TIME_FORMAT.format(deviceWarn.getAlarmTime())));
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(),e.getCause());
        }
        deviceWarn.setAlarmTime(null);

        Date date = new Date();
        String format = KafkaConsumerScheduled.FAST_DATE_MONTH_FORMAT.format(date);
        int week = KafkaConsumerScheduled.getWeek(date);
        String indexName = "device_warn_" + format + "-" + week;
        IndexRequest indexRequest = new IndexRequest(indexName, "_doc");
        indexRequest.id(String.valueOf(deviceWarn.getId()));
        indexRequest.source(JSON.toJSONString(deviceWarn), XContentType.JSON);
        IndexResponse response = null;
        try {
            response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(),e.getCause());
        }
        if(response!=null){
            RestStatus restStatus = response.status();
            int status = restStatus.getStatus();
            String name = restStatus.name();
            if (!Objects.equals(RestStatus.CREATED.getStatus(), status) && !Objects.equals(RestStatus.OK.getStatus(), status)) {
                RestStatus[] values = RestStatus.values();
                for (RestStatus item : values) {
                    if (Objects.equals(status, item.getStatus())) {
                        System.out.println(item.name());
                    }
                }
            }
            String s = response.toString();
            log.info("es新增的状态:{},{} ,详细信息：{}",name, status,s);
        }
    }
}
