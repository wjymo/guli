package com.zzn.kafkatest.query;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zzn.kafkatest.util.JsonDateDeSerialize;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RiskDeDeviceWarnQuery {
    private String indexName;
//    @JsonDeserialize(using = JsonDateDeSerialize.class)
    private String startTime;
//    @JsonDeserialize(using = JsonDateDeSerialize.class)
    private String endTime;
    private String org;

    private String carplate;
    private String alarmType;
    private String alarmSubType;
    private String alarmLevel;
    private String driverName;
    private String driverPhone;
    private String imei;
    private String tenantId;
    private List<String> orgList;
    private List<String> alarmCodeList;
    //运输企业名称
    private String transName;

    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private Integer offset;

    private String beginDate;
    private String endDate;

    public int getOffset(){
        if(pageNo != null && pageSize != null){
            return (pageNo-1)*pageSize;
        }
        return offset==null?0:offset;
    }

}
