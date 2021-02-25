package com.zzn.estest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 01383508123
 * @since 2019-12-06
 */
@Data
@Accessors(chain = true)
public class ExpCar  {


    /**
     * ID
     */
    private Long id;
    
    private Date createDate;
    
    private Long createBy;
    
    private Date updateDate;
    
    private Long updateBy;
    
    private String remarks;
    
    private Integer delFlag;
    /**
     * 车型
     */
    
    private String carType;

    /**
     * 号牌类型 01
     */
    
    private String noType;
    /**
     * 跟车司机
     */
    
    private String driver;
    /**
     * 载重
     */
    
    private String load;

    
    private String loadUnit;
    /**
     * 车牌号
     */
    
    private String carNo;
    /**
     * 检验有效期
     */
    
    private String validDate;
    /**
     * 承运商
     */
    
    private String company;
    /**
     * 车管电话
     */
    
    private String carPhone;
    /**
     * 品牌型号
     */
    
    private String brand;
    /**
     * 绑定设备
     */
    
    private String device;
    /**
     * 车辆状态 1：待安排 2：已安排
     */
    
    private Long status;
    /**
     * 油耗
     */
    
    private String carFuleConsume;
    /**
     * 运输类型
     */
    
    private String carTft;
    /**
     * 容积
     */
    
    private String carVolume;
    /**
     * 车辆规格
     */
    
    private String carSpec;
    /**
     * 组织机构
     */
    
    private Long org;

    /**
     * 组织机构名称
     */
    
    private String orgName;

    
    private Long crudExpCarGroupId;

    
    private String carGroupName;

    
    private String tenantId;

    
    private String powerType;

    
    private String axeNum;

    
    private String carWeight;

    
    private String priceStrategy;

    
    private String emissionStandard;

    
    private String carHeight;

    
    private String plateColor;

    
    private String carLength;

    
    private String carWidth;

    
    private String workTime;

    
    private String routeLine;

    
    private String crudSchRouteLineId;


    
    private String dataScope;

    
    private String imei;

    
    private Long deviceId;

    
    private List<Long> crudExpDriver;

    
    private String crudExpDriverName;


    private String drivingLicenseValid;

    private String drivingLicensePicture;

    
    private String drivingLicensePictureUrl;
    /**
     * 车辆商业险id
     */
    
    private Long busInsuranceId;
    /**
     * 车辆商业险有效期始
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    
    private Date busInsuranceBeginTime;
    /**
     * 车辆商业险有效期止
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    
    private Date busInsuranceEndTime;
    /**
     * 车辆商业险范围区间
     */
    
    private String busInsuranceRange;
    /**
     * 车辆商业险图片路径
     */
    
    private String busInsuranceImgPath;
    /**
     * 车辆交强险id
     */
    
    private Long forceInsuranceId;
    /**
     * 车辆交强险有效期始
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    
    private Date forceInsuranceBeginTime;
    /**
     * 车辆交强险有效期止
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    
    private Date forceInsuranceEndTime;
    /**
     * 车辆交强险范围区间
     */
    
    private String forceInsuranceRange;
    /**
     * 车辆交强险图片路径
     */
    
    private String forceInsuranceImgPath;

}
