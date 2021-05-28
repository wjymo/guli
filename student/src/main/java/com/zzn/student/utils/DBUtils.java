package com.zzn.student.utils;

import com.zzn.student.entity.Student;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class DBUtils {

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties properties = new Properties();
        InputStream fis = Class.forName(DBUtils.class.getName()).getResourceAsStream("/db.properties");
        properties.load(fis);
        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    /**
     * 增删改的通用方法
     */
    public static boolean executeUpdate(String sql, Object... args) {
        PreparedStatement ps = null;
        Connection connection =null;

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            int i = ps.executeUpdate();
            if (i > 0) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close(connection, ps, null);
        }
        return false;
    }

    /**
     * 增删改的通用方法
     */
    public static boolean executeUpdateStudent(String sql, Student student,boolean autoId) {
        PreparedStatement ps = null;
        Connection connection =null;

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            Field[] declaredFields = Student.class.getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                Field declaredField = declaredFields[i];
                String fieldName = declaredField.getName();
                if(autoId){
                    if(Objects.equals(fieldName,"sid")){
                        continue;
                    }
                    declaredField.setAccessible(true);
                    ps.setObject(i, declaredField.get(student));
                }else {
                    declaredField.setAccessible(true);
                    ps.setObject(i+1, declaredField.get(student));
                }

            }
            int i = ps.executeUpdate();
            if (i > 0) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            close(connection, ps, null);
        }
        return false;
    }


    // c查询的通用方法
    public static List<Student> executeQuery(String sql, Object... args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            /* 有可能有参数 */
            for (int i=0;i<args.length;i++){
                ps.setObject(i+1,args[i]);
            }
            /*执行*/
            resultSet = ps.executeQuery();
            /*需要将所有数据都存放到 List中    每一行 用一个 map存放*/
//            List<Map<String,Object>> list = new ArrayList<>();
            List<Student> studentList = new ArrayList<>();
            /*获取本次查询结果集有多少列*/
            int count = resultSet.getMetaData().getColumnCount();

            while(resultSet.next()){
//                Map<String, Object> map = new HashMap<>();//一行数据 用一个map 接收
                Student student=new Student();
                Field[] declaredFields = Student.class.getDeclaredFields();
                for(int i=0;i<count;i++){
                    String name = resultSet.getMetaData().getColumnLabel(i+1);
//                    map.put(name,resultSet.getObject(name));
                    for (Field declaredField : declaredFields) {
                        String fieldName = declaredField.getName();
                        if(Objects.equals(fieldName,name)){
                            declaredField.setAccessible(true);
//                            Class<?> type = declaredField.getType();
                            declaredField.set(student,resultSet.getObject(name));
                        }
                    }
//                    if(Objects.equals("name",name)){
//                        student.setName((String) resultSet.getObject(name));
//                    }

                }
                /*将每行的map存放到 List中*/
//                list.add(map);
                studentList.add(student);
            }
            return studentList;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            close(conn,ps,resultSet);
        }
        return null;
    }

//    public static boolean executeBatch(String sql, Object... objs) {
//        int n = 0;
//        PreparedStatement preparedStatement = null;
//        Connection connection =null;
//
//        try {
//            connection = getConnection();
//            // 那么对于每一条insert语句，都会产生一条log写入磁盘
//            connection.setAutoCommit(false);
//            preparedStatement = connection.prepareStatement(sql);
//            for (int i = 0; i < objs.length; i++) {
//                preparedStatement.setObject(i + 1, objs[i]);
//
//                // 1w条记录插入一次
//                if (i % 10000 == 0){
//                    preparedStatement.executeBatch();
//                    connection.commit();
//                }
//            }
//
//
//            // 最后插入不足1w条的数据
//            int[] executeBatch = preparedStatement.executeBatch();
//            connection.commit();
//            //更新条数
//            n= executeBatch.length;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            close(connection,preparedStatement,resultSet);
//        }
//        return n > 0 ? true : false;
//    }




    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        List<Student> students = executeQuery("select * from student where sid=?", 1);
//        boolean b = executeUpdate("insert into student (name,age,address) values(?,?,?)", "骁儿", 28, "内蒙大杨树");
        Student student=new Student();
        student.setName("王佩");
        student.setAge(39);
        student.setAddress("江夏五里界");
        boolean b = executeUpdateStudent("insert into student (name,age,address) values(?,?,?)", student,true);
        System.out.println(1);
    }



    //关闭的通用方法
    private static void close(Connection conn,PreparedStatement st,ResultSet set){
        try {
            if(set!=null){
                set.close();
            }
            if(st!=null){
                st.close();
            }
            if(conn != null){
                conn.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
