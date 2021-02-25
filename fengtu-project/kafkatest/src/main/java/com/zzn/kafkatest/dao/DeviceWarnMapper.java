package com.zzn.kafkatest.dao;

import com.zzn.kafkatest.entity.DeviceWarn;
import com.zzn.kafkatest.vo.RiskDeDeviceWarnVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface DeviceWarnMapper {

//    List<RiskDeDeviceWarnVo>

    @Insert("INSERT INTO `de_device_warn`" +
            "( `imei`, `pai_code`, `alarm_time`, `alarm_type`, `alarm_sub_type`, `file_type`," +
            " `alarm_location`, `lat`, `lng`, `speed`, `from_id`, `tenant_id`, `AUDIT_RESULT`, " +
            "`RECICE_TIME`, `SUBMIT_TIME`, `EMP_CODE`, `reason`, `TASK_STATUS`," +
            " `send_flag`, `audit_parent`, `audit_sub`, `origin_file_name`, `driver_id`, `driver_name`," +
            " `recent_risk`, `risk_level`, `fPhotos`, `bPhotos`, `temperature`, `humidity`," +
            " `speedX`, `speedY`, `speedZ`, `is_push`, `push_time`, `create_date`, `exist_flag`, `address`," +
            " `brief_address`, `comm_alarm_type`, `comm_subtype`, `org`, `alarm_level`) " +
            "VALUES " +
            "( #{imei}, #{paiCode}, #{alarmTime}, #{alarmType}, " +
            "#{alarmSubType}, #{fileType}, " +
            "#{alarmLocation}, #{lat}, #{lng}, #{speed}," +
            " #{fromId}, #{tenantId}, #{auditResult}, #{reciveTime}," +
            " #{submitTime}," +
            " #{empCode}, #{reason}, #{taskStatus}, #{sendFlag}, " +
            "#{auditParent}, " +
            "#{auditSub}, #{originFileName}, #{driverId}, #{driverName}," +
            " #{recentRisk}, #{riskLevel}, #{fPhotos}, #{bPhotos}, " +
            "#{temperature}," +
            " #{humidity}, #{speedX}, #{speedY}, #{speedZ}, #{isPush}," +
            " #{pushTime},#{createDate}, #{existFlag}, #{address}," +
            " #{briefAddress}, #{commAlarmType}, #{commSubtype}," +
            " #{org}, #{alarmLevel})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    Integer insertOne(DeviceWarn deviceWarn);

    @Update("update de_device_warn set pai_code=#{paiCode} where id=#{id}")
    Integer updateById(@Param("id") Long id,@Param("paiCode")String paiCode);

    @Delete("delete from de_device_warn  where id=#{id}")
    Integer delById(@Param("id") Long id);

    RiskDeDeviceWarnVo getEsResultById(@Param("id") Long deviceWarnId);


    @Select("SELECT g.id as warnId, " +
            "        g.pai_code as carplate, " +
            "        org.name as orgName, " +
            "        g.alarm_time as alarmTime, " +
            "        g.comm_alarm_type as alarmType, " +
            "        g.comm_subtype as alarmSubType, " +
            "        g.alarm_location as alarmLocationStr, " +
            "        CONCAT( g.lng,',', g.lat ) alarmLocation2, " +
            "        g.speed, " +
            "        g.imei, " +
            "        g.exist_flag as existFlag, " +
            "        g.fphotos, " +
            "        g.bPhotos, " +
            "        g.origin_file_name as originFileName, " +
            "        g.lng, " +
            "        g.lat, " +
            "        g.address, " +
            "        g.brief_address as briefAddress, " +
            "        g.alarm_level as alarmLevel, " +
            "  g.TASK_STATUS taskStatus, " +
            "        g.tenant_id tenantId, " +
            "        g.AUDIT_RESULT auditResult, " +
            "  g.org "+
            "        FROM " +
            "        de_device_warn g " +
            "        LEFT join exp_car car on g.pai_code = car.car_no " +
            "        LEFT JOIN ua_sys_org org ON car.org = org.id " +
            " where alarm_time>=#{startTime} and alarm_time<=#{endTime}")
    List<RiskDeDeviceWarnVo> getAllForEs(@Param("startTime")String startTime,@Param("endTime")String endTime);
}
