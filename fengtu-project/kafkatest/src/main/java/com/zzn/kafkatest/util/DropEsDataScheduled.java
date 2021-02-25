package com.zzn.kafkatest.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class DropEsDataScheduled {
    public static final FastDateFormat ISO_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM");
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    private static final int days=14;
//    @Scheduled(cron="")
    public void dropEsDataBefore2Month(){
        String beforeDaysForIndex = getBeforeDaysForIndex(days);
        String indexName="device_warn_"+beforeDaysForIndex;
        try {
            GetAliasesRequest request = new GetAliasesRequest().indices("device_*");
            GetAliasesResponse aliasesResponse = restHighLevelClient.indices().getAlias(request, RequestOptions.DEFAULT);
            Map<String, Set<AliasMetaData>> map = aliasesResponse.getAliases();
            Set<String> indices = map.keySet();
            for (String key : indices) {
                System.out.println(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getBeforeDaysForIndex(int days){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -days);
        Date date = calendar.getTime();
        String format = ISO_DATE_FORMAT.format(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);  //美国是以周日为每周的第一天 现把周一设成第一天
        calendar.setTime(date);
        int i = calendar.get(Calendar.WEEK_OF_MONTH);
        return format+"-"+i;
    }
}
