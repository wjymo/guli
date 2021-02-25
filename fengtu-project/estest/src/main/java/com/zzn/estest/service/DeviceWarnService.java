package com.zzn.estest.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zzn.estest.canal.CanalScheduled;
import com.zzn.estest.dao.DeviceWarnMapper;
import com.zzn.estest.entity.DeviceWarn;
import com.zzn.estest.query.RiskDeDeviceWarnQuery;
import com.zzn.estest.util.DropEsDataScheduled;
import com.zzn.estest.util.QueryDslStrTemplate;
import com.zzn.estest.vo.RiskDeDeviceWarnVo;
import com.zzn.estest.vo.RiskDeDeviceWarnVo2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.frameworkset.elasticsearch.template.ESTemplateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.Min;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Transactional(propagation = Propagation.SUPPORTS)
@Service
@Slf4j
public class DeviceWarnService {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);


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
        CompletableFuture<Map<String, Object>>[] completableFutures = new CompletableFuture[2];
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        long startTimeMillis = System.currentTimeMillis();
        String startTime = query.getStartTime();
        String endTime = query.getEndTime();
        try {
            Date start = fastDateFormat.parse(startTime);
            String startFormat = CanalScheduled.ISO_DATE_FORMAT.format(start);
            int startWeek = CanalScheduled.getWeek(start);
            String startYearMonthWeek = startFormat + "-" + startWeek;
//        String yearMonthWeek = format ;
            String indexName = "device_warn_" + startYearMonthWeek;

            CompletableFuture<Map<String, Object>> future1 = getFuture(query, indexName);
            completableFutures[0] = future1;


//            Date start = fastDateFormat.parse(startTime);


            Date end = fastDateFormat.parse(endTime);
            String endFormat = CanalScheduled.ISO_DATE_FORMAT.format(end);
            int endWeek = CanalScheduled.getWeek(end);
            String endYearMonthWeek = endFormat + "-" + endWeek;
            if (!StringUtils.equals(startYearMonthWeek, endYearMonthWeek)) {
                CompletableFuture<Map<String, Object>> future2 = getFuture(query, "device_warn_" + endYearMonthWeek);
                completableFutures[1] = future2;
//                if (!StringUtils.equals(startYearMonthWeek, yearMonthWeek) && StringUtils.equals(endYearMonthWeek, yearMonthWeek)) {
//                    //搜索的开始时间不等于当前时间，但等于结束时间等于当前时间，说明还需要之前的一个索引进行查询
//                    //只有这一种可能
//                    String beforeDaysForIndex = DropEsDataScheduled.getBeforeDaysForIndex(7);
//                    CompletableFuture<Map<String, Object>> future2 = getFuture(query, "device_warn_" + beforeDaysForIndex);
//                    completableFutures[1] = future2;
//                } else {
////                    throw new RuntimeException("！");
//                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (completableFutures[1] == null) {
            CompletableFuture<Map<String, Object>> completableFuture = completableFutures[0];
            Map<String, Object> map = completableFuture.get();
            return map;
        } else {
            Map<String, Object> allMap = new HashMap<>();
            long allTotal = 0;
//            List<JSONObject> allJsonObjects=new ArrayList<>();
            List<RiskDeDeviceWarnVo2> allRiskDeDeviceWarnVo2s = new ArrayList<>();
            CompletableFuture.allOf(completableFutures).join();
            for (int i = 0; i < completableFutures.length; i++) {
                CompletableFuture<Map<String, Object>> completableFuture = completableFutures[i];
                Map<String, Object> map = completableFuture.get();
                if (map != null && !map.isEmpty()) {
//                    List<JSONObject> jsonObjects = (List<JSONObject>)map.get("hits");
//                    allJsonObjects.addAll(jsonObjects);
                    List<RiskDeDeviceWarnVo2> riskDeDeviceWarnVo2s = (List<RiskDeDeviceWarnVo2>) map.get("hits");
                    allRiskDeDeviceWarnVo2s.addAll(riskDeDeviceWarnVo2s);
                    long total = (long) map.get("total");
                    long tookItem = (long) map.get("took");
                    allMap.put("took" + i, tookItem);
                    allTotal += total;
                }
            }
            Integer pageNo = query.getPageNo();
            Integer pageSize = query.getPageSize();
            allMap.put("pageNo", pageNo);
            allMap.put("pageSize", pageSize);
            allMap.put("total", allTotal);
//            tracks.sort(Comparator.comparing(obj -> ((JSONObject) JSON.toJSON(obj)).getLong("time")));
//            allJsonObjects=allJsonObjects.stream().sorted(Comparator.comparing(o->
//                    ((JSONObject)o).getLong("warnId")).reversed())
//                    .skip((pageNo-1)*pageSize).limit(pageSize).collect(Collectors.toList());
            allRiskDeDeviceWarnVo2s = allRiskDeDeviceWarnVo2s.stream().sorted(Comparator.comparing(o ->
                    ((RiskDeDeviceWarnVo2) o).getWarnId()).reversed())
                    .skip((pageNo - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
            allMap.put("hits", allRiskDeDeviceWarnVo2s);
            long end = System.currentTimeMillis();
            allMap.put("totalTook", (end - startTimeMillis));
            return allMap;
        }

    }

    private CompletableFuture<Map<String, Object>> getFuture(RiskDeDeviceWarnQuery query, String indexName) {
        CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
            try {
//                Map<String, Object> map = getInEs(query, indexName);
                Map<String, Object> map = getInEsConstant(query, indexName);
                return map;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        return future;
    }

    public Map<String, Object> getByQueryInMonth(RiskDeDeviceWarnQuery query) throws IOException, ExecutionException, InterruptedException {
        Date date = new Date();
        String format = CanalScheduled.ISO_DATE_FORMAT.format(date);
        String yearMonth = format;
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
                    completableFutures[1] = future2;
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
        } else {
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
//        ConstantScoreQueryBuilder constantScoreQueryBuilder=QueryBuilders.constantScoreQuery();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        if (StringUtils.isNotEmpty(query.getAlarmType())) {
            TermQueryBuilder termQueryBuilder1 = QueryBuilders.termQuery("alarmType", query.getAlarmType());
            boolBuilder.filter(termQueryBuilder1);
        }
        if (StringUtils.isNotEmpty(query.getAlarmSubType())) {
            TermQueryBuilder termQueryBuilder2 = QueryBuilders.termQuery("alarmSubType", query.getAlarmSubType());
            boolBuilder.filter(termQueryBuilder2);
        }
        if (StringUtils.isNotEmpty(query.getAlarmLevel())) {
            TermQueryBuilder termQueryBuilder4 = QueryBuilders.termQuery("alarmLevel", query.getAlarmLevel());
            boolBuilder.filter(termQueryBuilder4);
        }
//        if (Objects.nonNull(query.getOrg())) {
//            TermsQueryBuilder termsQueryBuilder3 = QueryBuilders.termsQuery("org", (Integer) query.getOrg());
//            boolBuilder.filter(termsQueryBuilder3);
//        }


        if (StringUtils.isNotEmpty(query.getTenantId())) {
            TermQueryBuilder termQueryBuilder6 = QueryBuilders.termQuery("tenantId", query.getTenantId());
            boolBuilder.filter(termQueryBuilder6);
        }

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("alarmTime")
                .gte(query.getStartTime()).lte(query.getEndTime());
        TermsQueryBuilder termsQueryBuilder5 = QueryBuilders.termsQuery("taskStatus", "2", "3");
        boolBuilder.filter(rangeQueryBuilder)
                .filter(termsQueryBuilder5);
        if (StringUtils.isNotEmpty(query.getCarplate())) {
            boolBuilder.must(QueryBuilders.matchQuery("carplate", query.getCarplate()));
        }
        boolBuilder.mustNot(QueryBuilders.termQuery("auditResult", 2));
        sourceBuilder.query(boolBuilder);

        sourceBuilder.sort(SortBuilders.fieldSort("alarmTime").order(SortOrder.DESC));

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
        List<RiskDeDeviceWarnVo2> collect2 = Arrays.asList(hits).stream().map(hit -> {
            String sourceAsString = hit.getSourceAsString();
            RiskDeDeviceWarnVo2 riskDeDeviceWarnVo = JSON.parseObject(sourceAsString, RiskDeDeviceWarnVo2.class);
            return riskDeDeviceWarnVo;
        }).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("pageNo", query.getPageNo());
        map.put("pageSize", query.getPageSize());
        map.put("total", totalHits);
        map.put("hits", collect);
        map.put("hits2", collect2);
        return map;
    }


    public Map<String, Object> getInEsConstant(RiskDeDeviceWarnQuery query, String indexName) {
        long start = System.currentTimeMillis();
        String s = JSON.toJSONString(query);
        Map parms = JSON.parseObject(s, Map.class);
//        Map<String,String> parms=new HashMap<>();
//        parms.put("title",title);
        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmappers/deviceWarn.xml");
//        ESDatas<Map> getByQuery = clientUtil
//                .searchList(indexName + "/_search", "getByQuery", parms, Map.class);
        ESDatas<RiskDeDeviceWarnVo2> response = clientUtil
                .searchList(indexName + "/_search", "getByQueryNoAudit", parms, RiskDeDeviceWarnVo2.class);
        List<RiskDeDeviceWarnVo2> datas = response.getDatas();
        long end = System.currentTimeMillis();
        long totalSize = response.getTotalSize();
        long took = response.getRestResponse().getTook();
        Map<String, Object> map = new HashMap<>();
        map.put("pageNo", query.getPageNo());
        map.put("pageSize", query.getPageSize());
        map.put("total", totalSize);
        map.put("totalTook", (end - start));
        map.put("took", took);
        map.put("hits", datas);
        return map;
    }

    public Map<String, Object> getInEsConstant2(RiskDeDeviceWarnQuery deDeviceWarnQuery, String indexName) throws IOException {
        Integer pageNo = deDeviceWarnQuery.getPageNo();
        Integer pageSize = deDeviceWarnQuery.getPageSize();
        Request request = new Request("GET", "/" + indexName + "/_search");
        JSONObject jsonObject = JSON.parseObject(QueryDslStrTemplate.DEVICE_WARN_BY_CONSTANT);
        jsonObject.put("from", (pageNo - 1) * pageSize);
        jsonObject.put("size", pageSize);
        JSONObject query = jsonObject.getJSONObject("query");
        JSONObject constantScore = query.getJSONObject("constant_score");
        JSONObject filter = constantScore.getJSONObject("filter");
        JSONObject bool = filter.getJSONObject("bool");
        JSONArray must = bool.getJSONArray("must");
        JSONObject range = new JSONObject();
        JSONObject rangeAlarmTime = new JSONObject();
        JSONObject fromAndTo = new JSONObject();
        fromAndTo.put("from", deDeviceWarnQuery.getStartTime());
        fromAndTo.put("to", deDeviceWarnQuery.getEndTime());
        range.put("range", rangeAlarmTime);
        rangeAlarmTime.put("alarmTime", fromAndTo);
//        must.add("range",rangeAlarmTime);


        request.setJsonEntity(JSON.toJSONString(jsonObject));
        Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
        String responseStr = EntityUtils.toString(response.getEntity());
        System.out.println(responseStr);
        JSONObject result = JSONObject.parseObject(responseStr);
        JSONObject hits = result.getJSONObject("hits");
        Long total = hits.getLong("total");
        JSONArray jsonArr = hits.getJSONArray("hits");
        Map<String, Object> map = new HashMap<>();
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);
        map.put("total", total);
        map.put("hits", jsonArr);
        return map;
    }

    @XxlJob("demo1")
    public ReturnT<String> execute(String param) {
        try {
            TimeUnit.MILLISECONDS.sleep(1500);
//            for (int i = 0; i < 10000000; i++) {
//            while (!Thread.currentThread().isInterrupted()){
//                System.out.println(8);
//                System.out.println();
//            }
//            }
            System.out.println("庚庚在哪弄的：" + param);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
            return ReturnT.FAIL;
        }
        ReturnT<String> returnT = new ReturnT<>(ReturnT.FAIL_CODE, "庚庚mm");
        returnT.setContent("爱小芬");
        return returnT;
    }

    public static void main(String[] args) throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        System.out.println("Local HostAddress:" + addr.getHostAddress());
        String hostname = addr.getHostName();
        System.out.println("Local host name: " + hostname);
    }

    @XxlJob("demo2")
    public ReturnT<String> execute2(String param) {
        List<Future<String>> futureList = new ArrayList<>();
        Callable<String> task1 = () -> "胡尧";
        Callable<String> task2 = () -> {
            /*if(StringUtils.equals(param,"0")){
                TimeUnit.MILLISECONDS.sleep(3100);
            }else {
                int i = 10 / 0;
            }*/
            while (!Thread.currentThread().isInterrupted()) {
                int i = 10 / 5;
                int i1 = 10 / 9;
                System.out.println(i + i1);
            }
            return "骁儿";
        };
        Callable<String> task3 = () -> "王佩";
        Future<String> future1 = EXECUTOR.submit(task1);
        Future<String> future2 = EXECUTOR.submit(task2);
        Future<String> future3 = EXECUTOR.submit(task3);
        futureList.add(future1);
        futureList.add(future2);
        futureList.add(future3);

        List<String> names = new ArrayList<>();
        for (int i = 0; i < futureList.size(); i++) {
            Future<String> future = futureList.get(i);
            try {
                String name = future.get(2, TimeUnit.SECONDS);
                names.add(name);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
//                return ReturnT.FAIL;
            } catch (ExecutionException e) {
                log.error(e.getMessage(), e);
//                return ReturnT.FAIL;
            } catch (TimeoutException e) {
                log.error(e.getMessage(), e);
                future.cancel(true);

            }
        }
//        return  new ReturnT<>(Arrays.toString(names.toArray()));
        return new ReturnT<>(ReturnT.FAIL_CODE, "庚庚");
    }
}
