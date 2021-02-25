package com.zzn.estest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzn.estest.dao.DeviceWarnMapper;
import com.zzn.estest.entity.DeviceWarn;
import com.zzn.estest.vo.RiskDeDeviceWarnVo;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestMysql {
    @Autowired
    private DeviceWarnMapper deviceWarnMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testJdbcTemplate(){

        List<Map<String, Object>> mapList=jdbcTemplate.queryForList("SELECT  TABLE_NAME tableName,GROUP_CONCAT(COLUMN_NAME) cloumns  FROM  information_schema.COLUMNS  " +
                " where TABLE_SCHEMA='efsvmap' and  COLUMN_NAME in ('org','org_id')  " +
                " GROUP BY TABLE_NAME");
        Map<String,String> result=new HashMap<>();
        mapList.forEach(map->{
            String tableName = (String)map.get("tableName");
            String cloumns = (String)map.get("cloumns");
            result.put(tableName,cloumns);
        });

        System.out.println(1);
    }
    @Test
    public void testInsertDeviceWarn() throws ParseException {
        String s = "2021-01-01 12:12:12";
        Date date = DateUtils.parseDate(s, "yyyy-MM-dd HH:mm:ss");
        DeviceWarn deviceWarn=new DeviceWarn();
        deviceWarn.setOrg(1111l);
        deviceWarn.setAlarmType("bsd");
        deviceWarn.setAlarmTime(date);
        deviceWarn.setCreateDate(new Date());
        deviceWarn.setFromId("xx-2");

        Integer integer = null;
        try {
            integer = deviceWarnMapper.insertByDisTest(deviceWarn);
        } catch (Exception e) {
            if(e instanceof  org.springframework.dao.DuplicateKeyException){
                System.out.println(1);
            }
            e.printStackTrace();
        }
        System.out.println(integer+":"+deviceWarn.getId());
    }

    @Test
    public void testInterceptor(){
//        List<RiskDeDeviceWarnVo> allForEs = deviceWarnMapper.getAllForEs("2020-12-01 12:00:00", "2020-12-01 12:50:00");
//        System.out.println(allForEs.size());
//        System.out.println("**************************************");
//        allForEs.forEach(riskDeDeviceWarnVo -> {
////            System.out.println(riskDeDeviceWarnVo.getWarnId()+" ,");
//        });
        Class<DeviceWarnMapper> deviceWarnMapperClass = DeviceWarnMapper.class;
        String name = deviceWarnMapperClass.getName();
        RiskDeDeviceWarnVo esResultById = deviceWarnMapper.getEsResultById(456520l);
        System.out.println(1);
    }

    @Test
    public void testParseSql() throws JSQLParserException {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader("select name from user where id =1"));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        List<SelectItem> selectitems = plain.getSelectItems();
        List<String> str_items = new ArrayList<String>();
        if (selectitems != null) {
            for (int i = 0; i < selectitems.size(); i++) {
                str_items.add(selectitems.get(i).toString());
            }
        }
        System.out.println(1);
    }
}
