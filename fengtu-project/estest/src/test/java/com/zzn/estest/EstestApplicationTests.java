package com.zzn.estest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Collections2;
import com.zzn.estest.canal.CanalScheduled;
import com.zzn.estest.dao.DeviceWarnMapper;
import com.zzn.estest.query.RiskDeDeviceWarnQuery;
import com.zzn.estest.service.DeviceWarnService;
import com.zzn.estest.vo.RiskDeDeviceWarnVo;
import com.zzn.estest.vo.RiskDeDeviceWarnVo2;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringJUnit4ClassRunner.class)
class EstestApplicationTests {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private DeviceWarnMapper deviceWarnMapper;
    @Autowired
    private DeviceWarnService deviceWarnService;

    @Test
    public void contextLoads() throws IOException {
        String json = FileUtils.readFileToString(
                new File("D:\\项目资料\\智慧车辆安全监管平台\\1223\\告警存储方案\\deviceWarnResult.json")
                , "utf-8");
        List<RiskDeDeviceWarnVo> riskDeDeviceWarnVos = JSON.parseArray(json, RiskDeDeviceWarnVo.class);
        riskDeDeviceWarnVos = riskDeDeviceWarnVos.stream().map(riskDeDeviceWarnVo -> {
            String alarmLocationStr = riskDeDeviceWarnVo.getAlarmLocationStr();
            BigDecimal[] logLat = getLogLat(alarmLocationStr);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lon", logLat[0]);
            jsonObject.put("lat", logLat[1]);
            riskDeDeviceWarnVo.setAlarmLocation(jsonObject);
            riskDeDeviceWarnVo.setOrg(RandomUtils.nextBoolean() ? 3474 : 3365);
            riskDeDeviceWarnVo.setTenantId("yzfkcs01");
            riskDeDeviceWarnVo.setTaskStatus(2);
            riskDeDeviceWarnVo.setAuditResult(0);
            return riskDeDeviceWarnVo;
        }).collect(Collectors.toList());
        BulkRequest bulkRequest = new BulkRequest();
        for (RiskDeDeviceWarnVo riskDeDeviceWarnVo : riskDeDeviceWarnVos) {
            IndexRequest indexRequest = new IndexRequest("device_warn", "_doc")
//                    .id(String.valueOf(riskDeDeviceWarnVo.getWarnId()))
                    .source(JSON.toJSONString(riskDeDeviceWarnVo), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(bulkResponse));
        System.out.println(1);

    }

    @Test
    public void testBboss() throws ParseException {
        RiskDeDeviceWarnQuery query = new RiskDeDeviceWarnQuery();
        String beginStr = "2021-01-18 16:53:02";
        String endStr = "2021-01-19 16:53:02";
        query.setBeginDate(beginStr);
        query.setEndDate(endStr);
//        query.setOrgList(Arrays.asList("3473","3471"));
        query.setOffset((query.getPageSize() - 1) * query.getPageNo());
        Map<String, Object> inEsConstant = deviceWarnService.getInEsConstant(query, "/device_warn_2021-01-4/_search");
        System.out.println(1);
    }

    @Test
    public void testUpdate(){
        BulkRequest bulkRequest = new BulkRequest();

        RiskDeDeviceWarnVo2 deviceWarnVo = new RiskDeDeviceWarnVo2();
        deviceWarnVo.setCarplate("胡尧车");
        deviceWarnVo.setOriginFileName("xx");

        UpdateRequest updateRequest = new UpdateRequest("device_warn_2020-12-5", "_doc",
                String.valueOf(509078))
                .doc(JSON.toJSONString(deviceWarnVo), XContentType.JSON);
        bulkRequest.add(updateRequest);
        BulkResponse bulkResponse = null;
        try {
            bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("同步es失败！");
        }
        System.out.println(1);
    }

    @Test
    public void testIndex(){
        BulkRequest bulkRequest = new BulkRequest();

        RiskDeDeviceWarnVo2 deviceWarnVo = new RiskDeDeviceWarnVo2();
        deviceWarnVo.setCarplate("胡尧车2");
        deviceWarnVo.setOriginFileName("xx2");

        IndexRequest indexRequest = new IndexRequest("device_warn_2020-12-5", "_doc",
                String.valueOf(509078))
                .source(JSON.toJSONString(deviceWarnVo), XContentType.JSON);
        bulkRequest.add(indexRequest);
        BulkResponse bulkResponse = null;
        try {
            bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("同步es失败！");
        }
        System.out.println(1);
    }

    @Test
    public void testIndexRequest() throws IOException {
        RiskDeDeviceWarnVo2 deviceWarnVo = new RiskDeDeviceWarnVo2();
        deviceWarnVo.setWarnId(2l);
        deviceWarnVo.setAddress("八宝山火葬2");
        deviceWarnVo.setExistFlag(2);

        IndexRequest indexRequest = new IndexRequest("device_warn_2020-12-6", "_doc");
        indexRequest.id(String.valueOf(deviceWarnVo.getWarnId()));
        indexRequest.source(JSON.toJSONString(deviceWarnVo), XContentType.JSON);
        //当人为终止任务时，若执行到这里，会抛出异常，在外层方法中结束此次定时任务
        IndexResponse response = null;
        try {
            response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            RestStatus restStatus = response.status();
            int status = restStatus.getStatus();
            String name = restStatus.name();
            System.out.println(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public BigDecimal[] getLogLat(String poiStr) {
        String substring = poiStr.substring(poiStr.indexOf("(") + 1, poiStr.indexOf(")"));
        String[] s = substring.split(" ");
        BigDecimal[] bigDecimals = new BigDecimal[2];
        bigDecimals[0] = new BigDecimal(s[0]);
        bigDecimals[1] = new BigDecimal(s[1]);
        return bigDecimals;
    }


    @Test
    public void testBlukImport() throws IOException, ParseException {
        String startTime = "2021-01-01 00:00:00";
        String endTime = "2021-01-01 00:00:05";
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        startTime = fastDateFormat.format(fastDateFormat.parse(startTime));
        endTime = fastDateFormat.format(fastDateFormat.parse(endTime));
        List<RiskDeDeviceWarnVo> allForEs = deviceWarnMapper.getAllForEs(startTime, endTime);
        BulkRequest bulkRequest = new BulkRequest();
        allForEs.forEach(riskDeDeviceWarnVo -> {
            String alarmLocationStr = riskDeDeviceWarnVo.getAlarmLocationStr();
            BigDecimal[] logLat = getLogLat(alarmLocationStr);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lon", logLat[0]);
            jsonObject.put("lat", logLat[1]);
            riskDeDeviceWarnVo.setAlarmLocation(jsonObject);
            riskDeDeviceWarnVo.setAlarmLocationStr(null);
//            String code = riskDeDeviceWarnVo.getAlarmType() + "-" + riskDeDeviceWarnVo.getAlarmSubType();
//            riskDeDeviceWarnVo.setCode(code);
            IndexRequest indexRequest = new IndexRequest("device_warn", "_doc")
                    .id(String.valueOf(riskDeDeviceWarnVo.getWarnId()))
                    .source(JSON.toJSONString(riskDeDeviceWarnVo), XContentType.JSON);
            bulkRequest.add(indexRequest);
        });
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.toString());
        System.out.println(1);
    }

    @Test
    public void testBatchUpdate() throws ParseException, IOException {
        String startTime = "2021-01-01 00:00:00";
        String endTime = "2021-01-01 00:00:05";
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        startTime = fastDateFormat.format(fastDateFormat.parse(startTime));
        endTime = fastDateFormat.format(fastDateFormat.parse(endTime));
//        List<RiskDeDeviceWarnVo> allForEs = deviceWarnMapper.getAllForEs(startTime,endTime);
        RiskDeDeviceWarnVo deDeviceWarnVo1 = new RiskDeDeviceWarnVo().setWarnId(456682l).setCarplate("粤B00000").setExistFlag(true);
        RiskDeDeviceWarnVo deDeviceWarnVo2 = new RiskDeDeviceWarnVo().setWarnId(456698l).setCarplate("粤B11111").setExistFlag(true);
        RiskDeDeviceWarnVo deDeviceWarnVo4 = new RiskDeDeviceWarnVo().setWarnId(456698l).setCarplate("粤B11111").setExistFlag(true);
        List<RiskDeDeviceWarnVo> allForEs = Arrays.asList(deDeviceWarnVo1, deDeviceWarnVo2);
        BulkRequest bulkRequest = new BulkRequest();
        allForEs.forEach(riskDeDeviceWarnVo -> {
            String alarmLocationStr = riskDeDeviceWarnVo.getAlarmLocationStr();
            if (StringUtils.isNotEmpty(alarmLocationStr)) {
                BigDecimal[] logLat = getLogLat(alarmLocationStr);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lon", logLat[0]);
                jsonObject.put("lat", logLat[1]);
                riskDeDeviceWarnVo.setAlarmLocation(jsonObject);
                riskDeDeviceWarnVo.setAlarmLocationStr(null);
            }
//            String code = riskDeDeviceWarnVo.getAlarmType() + "-" + riskDeDeviceWarnVo.getAlarmSubType();
//            riskDeDeviceWarnVo.setCode(code);
            UpdateRequest updateRequest = new UpdateRequest("device_warn",
                    "_doc", String.valueOf(riskDeDeviceWarnVo.getWarnId()))
                    .doc(JSON.toJSONString(riskDeDeviceWarnVo), XContentType.JSON);
            bulkRequest.add(updateRequest);
        });
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.toString());
        System.out.println(1);
    }

    @Test
    public void testBatchUpdateManyIndex() throws  IOException {
        RiskDeDeviceWarnVo deDeviceWarnVo1 = new RiskDeDeviceWarnVo().setWarnId(456682l).setCarplate("粤B99999").setExistFlag(true);
        RiskDeDeviceWarnVo deDeviceWarnVo2 = new RiskDeDeviceWarnVo().setWarnId(509077l).setCarplate("粤123");
        BulkRequest bulkRequest = new BulkRequest();

        UpdateRequest updateRequest1 = new UpdateRequest("device_warn",
                "_doc", String.valueOf(deDeviceWarnVo1.getWarnId()))
                .doc(JSON.toJSONString(deDeviceWarnVo1), XContentType.JSON);
        bulkRequest.add(updateRequest1);
        UpdateRequest updateRequest2 = new UpdateRequest("device_warn_2020-12-5",
                "_doc", String.valueOf(deDeviceWarnVo2.getWarnId()))
                .doc(JSON.toJSONString(deDeviceWarnVo2), XContentType.JSON);
        bulkRequest.add(updateRequest2);

        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.toString());
        System.out.println(1);
    }

    /**
     * 定时删除过期索引
     */
    @Test
    public void testGetIndies() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date date = calendar.getTime();
        String format = CanalScheduled.ISO_DATE_FORMAT.format(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);  //美国是以周日为每周的第一天 现把周一设成第一天
//        calendar.setTime(date);
        int i = calendar.get(Calendar.WEEK_OF_MONTH);
        String needDelIndexSuffix = format + "-" + i;

        LocalDate needDelIndexSuffixDate = LocalDate.parse(needDelIndexSuffix, dateTimeFormatter);
        String indexPrefix = "device_warn_";
        try {
            GetAliasesRequest getAliasesRequest = new GetAliasesRequest().indices(indexPrefix + "*");
            GetAliasesResponse aliasesResponse = restHighLevelClient.indices().getAlias(getAliasesRequest, RequestOptions.DEFAULT);
            Map<String, Set<AliasMetaData>> map = aliasesResponse.getAliases();
            Set<String> indices = map.keySet();
            List<String> needDelIndies = indices.stream().filter(index -> {
                String weekDateStr = index.substring(indexPrefix.length());
                System.out.println(needDelIndexSuffix + " : " + weekDateStr);
                LocalDate weekDate = LocalDate.parse(weekDateStr, dateTimeFormatter);
                return weekDate.isBefore(needDelIndexSuffixDate);
            }).collect(Collectors.toList());
            String needDelIndiesStr = indices.stream().filter(index -> {
                String weekDateStr = index.substring(indexPrefix.length());
                LocalDate weekDate = LocalDate.parse(weekDateStr, dateTimeFormatter);
                return weekDate.isBefore(needDelIndexSuffixDate);
            }).collect(Collectors.joining(","));
            System.out.println("*********************************");
            System.out.println(Arrays.toString(needDelIndies.toArray()));
            Request request = new Request("DELETE", "/" + needDelIndiesStr);
            Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
            HttpEntity entity = response.getEntity();
            String s = EntityUtils.toString(entity);
            JSONObject jsonObject = JSON.parseObject(s);
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGetIndies4month() {
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        Date date = calendar.getTime();
        String format = CanalScheduled.ISO_DATE_FORMAT.format(date);

        String needDelIndexSuffix = format;
        String indexPrefix = "device_warn_";

        try {
            GetAliasesRequest getAliasesRequest = new GetAliasesRequest().indices(indexPrefix + "*");
            GetAliasesResponse aliasesResponse = restHighLevelClient.indices().getAlias(getAliasesRequest, RequestOptions.DEFAULT);
            Map<String, Set<AliasMetaData>> map = aliasesResponse.getAliases();
            Set<String> indices = map.keySet();
            List<String> needDelIndies = indices.stream().filter(index -> {
                String weekDateStr = index.substring(indexPrefix.length());
                System.out.println(needDelIndexSuffix + " : " + weekDateStr);
                Date weekDate = null;
                try {
                    weekDate = fastDateFormat.parse(weekDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return weekDate.before(date);
            }).collect(Collectors.toList());
            String needDelIndiesStr = indices.stream().filter(index -> {
                String weekDateStr = index.substring(indexPrefix.length());
                Date weekDate = null;
                try {
                    weekDate = fastDateFormat.parse(weekDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return weekDate.before(date);
            }).collect(Collectors.joining(","));
            System.out.println("*********************************");
            System.out.println(Arrays.toString(needDelIndies.toArray()));
            if (StringUtils.isNotEmpty(needDelIndiesStr)) {
                Request request = new Request("DELETE", "/" + needDelIndiesStr);
                Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
                HttpEntity entity = response.getEntity();
                String s = EntityUtils.toString(entity);
                JSONObject jsonObject = JSON.parseObject(s);
                System.out.println(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testOrginFile() {

    }
}
