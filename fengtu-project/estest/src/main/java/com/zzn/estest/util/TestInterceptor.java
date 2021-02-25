package com.zzn.estest.util;

import com.zzn.estest.dao.DeviceWarnMapper;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.*;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class TestInterceptor implements Interceptor {
    //    private static final Set<String> filter;
//    static {
//        filter=new HashSet<>(Arrays.asList("com.zzn.estest.dao.DeviceWarnMapper.getAllForEs",DeviceWarnMapper.class.getName()+".getAllForEs"
//        ,DeviceWarnMapper.class.getName()+".getEsResultById"));
//    }
    static Map<String, Integer> tableMap = new HashMap<String, Integer>() {
        {
            put("exp_car", 1);
            put("exp_device", 1);
            put("exp_driver", 1);
            put("risk_transport", 1);
        }
    };
    static String orgTableName = "ua_sys_org";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
//        Connection connection = (Connection) invocation.getArgs()[0];
        ParameterHandler parameterHandler = statementHandler.getParameterHandler();
        Object parameterObject = parameterHandler.getParameterObject();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        String id = mappedStatement.getId();
        Class<?> mapperClass = Class.forName(id.substring(0, id.lastIndexOf(".")));
//        Class<?> baseMapperClass = mapperClass.getSuperclass();
//        if(baseMapperClass!=null){
//            Type type=baseMapperClass.getGenericSuperclass();
//            //获取泛型，即实体类类型
//            ParameterizedType p=(ParameterizedType)type;
//            //获取实体类类型
//            Class entityClass=(Class) p.getActualTypeArguments()[0];
//            Field[] declaredFields = entityClass.getDeclaredFields();
//            for (Field declaredField : declaredFields) {
//                String name = declaredField.getName();
//                if(StringUtils.contains(name,"org")){
//                    System.out.println("实体类包含组织id："+name);
//                }
//            }
//        }


        OrgFilter filter = SpringContextUtil.getBean(OrgFilter.class);
        boolean needProcess = false;
        if (filter.containsSql(id)) {
            needProcess = true;
        } else {
            int i = id.lastIndexOf(".");
            String mapper = id.substring(0, i);
            needProcess = filter.containsMapper(mapper);
        }
        if (needProcess) {
            BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
            //解析sql
            String sql = boundSql.getSql();
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Select) {
                Select select = (Select) statement;
                SelectBody selectBody = select.getSelectBody();
                if (selectBody instanceof PlainSelect) {
                    PlainSelect plainSelect = (PlainSelect) selectBody;
                    FromItem fromItem = plainSelect.getFromItem();
                    if (fromItem instanceof Table) {
                        Table table = (Table) fromItem;
                        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
                        List<String> tableList = tablesNamesFinder.getTableList(select);
                        //join表
                        List<Join> joins = plainSelect.getJoins();

//判断表名是否已包含org表
                        String orgName = "";
                        String addSql = "";
                        String whereAlias = "";
                        if (tableList.contains(orgTableName)) {
                            if (orgTableName.equals(table.getName())) {
                                //有别名用别名，无别名用表名，防止字段冲突报错
                                orgName = table.getAlias() == null ? table.getName() : table.getAlias().getName();
                            } else {
                                if(joins!=null){
                                    //为join表，获取别名
                                    for (Join join : joins) {
                                        Table joinRightItem = (Table) join.getRightItem();
                                        if (orgTableName.equals(joinRightItem.getName())) {
                                            //join表一定有别名
                                            orgName = joinRightItem.getAlias().getName();
                                        } else {
                                            continue;
                                        }
                                    }
                                }
                            }
                        } else {
                            //判断是否有锚点表
                            if (tableMap.containsKey(table.getName())) {
                                //join中加入org表
                                orgName = table.getAlias() == null ? table.getName() : table.getAlias().getName();
                            } else {
                                if(joins!=null){
                                    for (Join join : joins) {
                                        Table joinRightItem = (Table) join.getRightItem();
                                        if (tableMap.containsKey(joinRightItem.getName())) {
                                            //join表一定有别名
                                            orgName = joinRightItem.getAlias().getName();
                                            break;
                                        } else {
                                            continue;
                                        }
                                    }
                                }
                            }
                            if(StringUtils.isNotEmpty(orgName)){
                                Join join = new Join();
                                join.setLeft(true);
                                Table orgTable = new Table(orgTableName);
                                orgTable.setAlias(new Alias("f_org"));
                                join.setRightItem(orgTable);
                                join.setOnExpression(CCJSqlParserUtil.parseCondExpression("f_org.id = " + orgName + ".org"));
                                joins.add(join);
                                plainSelect.setJoins(joins);
                                whereAlias = orgTable.getAlias().getName();
                            }
                        }
//                        LoginUser user = SecurityUtils.getUser();
                        if(StringUtils.isEmpty(whereAlias)){
                            whereAlias=orgName;
                        }
                        if (StringUtils.isNotBlank(whereAlias)){
                            addSql = whereAlias+".parent_ids LIKE '"+"[1]%' ";
                            if (plainSelect.getWhere() == null) {
                                plainSelect.setWhere(CCJSqlParserUtil.parseCondExpression(addSql));
                            } else {
                                plainSelect.setWhere(new AndExpression(plainSelect.getWhere(), CCJSqlParserUtil.parseCondExpression(addSql)));
                            }
                        }
                        String s = select.toString();
                        System.out.println(")))))))))))))):"+s);


                        Expression where = plainSelect.getWhere();
                        Alias alias = table.getAlias();
                        //生成字段名
                        EqualsTo equalsTo = new EqualsTo();
                        StringBuilder column = new StringBuilder();
                        column.append(table.getAlias().getName());
                        column.append(".");
                        column.append("tenant_id");
                        equalsTo.setLeftExpression(new Column(column.toString()));
                        equalsTo.setRightExpression(new StringValue("YZKF"));
                        if (where == null) {
                            plainSelect.setWhere(equalsTo);
                        } else {
                            AndExpression andExpression = new AndExpression(equalsTo, where);
                            plainSelect.setWhere(andExpression);
                        }
                    }
                }
            }
//        CCJSqlParserManager parserManager = new CCJSqlParserManager();
//        Select select = (Select) parserManager.parse(new StringReader(sql));
//        PlainSelect plain = (PlainSelect) select.getSelectBody();
//        List<SelectItem> selectitems = plain.getSelectItems();
//        List<String> str_items = new ArrayList<String>();
//        if (selectitems != null) {
//            for (int i = 0; i < selectitems.size(); i++) {
//                str_items.add(selectitems.get(i).toString());
//            }
//        }
            String newSql = statement.toString();

            metaObject.setValue("delegate.boundSql.sql", newSql);
        }
        Configuration configuration = mappedStatement.getConfiguration();


        return invocation.proceed();
    }
}
