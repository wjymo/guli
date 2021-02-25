package com.zzn.estest.entity;

import lombok.Data;

import java.util.Date;


@Data
public class DeviceWarn  {


    private Long id;
    private String imei;
    /**
     *'车牌号'
     */
    private String paiCode;
    /**
     *''告警发生时间''
     */
    private Date alarmTime;
    /**
     *''告警大类：0-疲劳行为、1-三急行为、2-安全驾驶、3-分心''
     */
    private String alarmType;
    /**
     *'告警子类\r\n大类为 0-疲劳行为\r\n1-疲劳；2-打哈欠\r\n\r\n大类为 1-三急行为\r\n1-急加速；2-急减速；3-急左转；4-急右转\r\n\r\n大类为 2-安全驾驶\r\n1-头盔报警；2-逆行报警；3-占道报警；4-超速报警\r\n\r\n大类为3-分心\r\n1-打电话；2-吸烟；3-左顾右盼\r\n'
     */
    private String alarmSubType;
    /**
     *'文件类型：0-无文件 1-图片  2- mp4 '
     */
    private String fileType;
    /**
     *'预警发生经纬度'
     */
    private String alarmLocation;
    /**
     *'纬度'
     */
    private Double lat;
    /**
     *''经度''
     */
    private Double lng;
    /**
     *''车速''
     */
    private String speed;
    /**
     *'同步数据的起始ID'
     */
    private String fromId;
    /**
     */
    private String tenantId;
    /**
     * '审核结果(0,自动审批；1，正确；2，错误；3，无法识别)'
     */
    private Integer auditResult;

    /**
     * '领取时间'
     */
    private Date reciveTime;
    /**
     *'提交时间（第一次审核时间）
     */
    private Date submitTime;
    /**
     *'处理人工号'
     */
    private String empCode;
    /**
     *''审核原因''
     */
    private String reason;
    /**
     * '任务状态：0未处理，1正在处理，2完成'
     */
    private Integer taskStatus;
    /**
     *'发送状态：0未发送，1已发送'
     */
    private Integer sendFlag;
    private Integer auditParent;
    private Integer auditSub;
    private String originFileName;
    /**
     * 司机id
     */
    private Long driverId;
    /**
     * 司机名称
     */
    private String driverName;

    private Double recentRisk;

    private Integer riskLevel;

    private String temperature;
    private String humidity;
    private String speedX;
    private String speedY;
    private String speedZ;

    private String fPhotos;
    private String bPhotos;

    private Integer isPush;
    private Date pushTime;
    private Date createDate;
    private Integer existFlag;
    private String address;
    private String briefAddress;
    private String commAlarmType;
    private String commSubtype;
    private Integer alarmLevel;
    private Long org;

}
