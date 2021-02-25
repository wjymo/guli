package com.zzn.kafkatest.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class RiskDeDeviceWarnVo {

	private Long warnId;
	
	private String carplate;
	//	
	private String imei;
	
	private String orgName;
	
	private String alarmTime;
//	
	private String alarmType;
	
	private String alarmTypeName;
//	
	private Integer alarmSubType;
	
	private String alarmSubTypeName;

	private String alarmLocationStr;
	private JSONObject alarmLocation;
	private String alarmLocation2;

	private Double speed;
	
	private String driverName;
	
	private String driverPhone;
	private String address;
	
	private String briefAddress;

	private String recentRisk;
	private String deviceType;
	private String deviceModel;
	private boolean existFlag;
	private String originFileName;
	private String fphotos;
	private String bPhotos;
	private Integer riskLevel ;
	private Integer alarmLevel ;
	
	private String alarmLevelName ;
	private Double lng;
	private Double lat;

	private Integer org;
	private String tenantId ;
	private Integer taskStatus;
	private Integer auditResult;

	private String code;
}
