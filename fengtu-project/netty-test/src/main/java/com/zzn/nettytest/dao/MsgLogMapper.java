package com.zzn.nettytest.dao;

import com.zzn.nettytest.bean.MsgLog;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface MsgLogMapper {

    @Insert("insert into msg_log (content,update_time) values (#{content},#{updateTime}  )")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    Integer insertOne(MsgLog msgLog);

    @Update("update msg_log set status=#{status},update_time=#{updateDate}  where id=#{id}")
    Integer updateStatus(@Param("id")Long id,@Param("status") Integer status, @Param("updateDate")Date updateDate);

    @Update("update msg_log set status=#{status},retries=retries+1,update_time=#{updateDate}  where id=#{id}")
    Integer updateStatusAndAddRetries(@Param("id")Long id, @Param("status") Integer status, @Param("updateDate")Date updateDate);

    @Select("select  * from msg_log " +
            "where status =${@com.zzn.nettytest.constant.MsgStatus@UN_SUCCESS} and update_time>#{doNothingDateTime} " +
            " and retries<=3" +
            " union all " +
            "select  * from msg_log " +
            "where status =${@com.zzn.nettytest.constant.MsgStatus@FAIL} and update_time>#{failDateTime} " +
            " and retries<=3")
    List<MsgLog> getFailAndUnSuccessList(@Param("doNothingDateTime")String doNothingDateTime,@Param("failDateTime")String failDateTime);

}
