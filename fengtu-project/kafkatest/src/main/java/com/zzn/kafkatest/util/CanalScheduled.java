package com.zzn.kafkatest.util;

import com.alibaba.fastjson.JSON;
import com.zzn.kafkatest.dao.DeviceWarnMapper;
import com.zzn.kafkatest.vo.RiskDeDeviceWarnVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;


@Component
@Slf4j
public class CanalScheduled {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private DeviceWarnMapper deviceWarnMapper;

    public LongAdder longAdder = new LongAdder();
    public static final FastDateFormat ISO_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM");








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



    public static int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);  //美国是以周日为每周的第一天 现把周一设成第一天
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }
}
