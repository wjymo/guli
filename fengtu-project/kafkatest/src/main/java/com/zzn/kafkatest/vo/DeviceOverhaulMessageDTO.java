package com.zzn.kafkatest.vo;

import com.zzn.kafkatest.util.ExcelField;
import lombok.Data;

import java.util.Date;

@Data
public class DeviceOverhaulMessageDTO {

    @ExcelField(title="* 车牌号", align=2, sort=1)
    private String carplate;

    @ExcelField(title="* 设备ID", align=2, sort=2)
    private String imei;

    @ExcelField(title="驾驶人姓名", align=2, sort=3)
    private String driverName;

    @ExcelField(title="驾驶人电话", align=2, sort=4)
    private String driverPhone;

    @ExcelField(title="* 所属机构", align=2, sort=5)
    private String orgName;

    @ExcelField(title="* 报修电话", align=2, sort=6)
    private String repairPhone;

    @ExcelField(title="* 故障描述", align=2, sort=7)
    private String faultDesc;


    private Long id;
    private String orgId;
    private Integer readStatus;
    private String readStatusStr;
    private Date createDate;
    private Integer releaseStatus;
}
