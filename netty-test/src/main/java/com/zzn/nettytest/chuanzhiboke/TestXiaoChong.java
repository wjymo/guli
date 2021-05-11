package com.zzn.nettytest.chuanzhiboke;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Slf4j
public class TestXiaoChong {
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    //配置您申请的appKey和openId
    public static final String APP_KEY ="slRdhk5x800w";
    public static final String OPEN_ID ="YZt5NI9MsB9wEE80";

    //将map型转为请求参数型
    public static String urlEncode(Map<String,Object> params) {

        if(params==null){return "";};

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,Object> i : params.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String r=sb.toString();
        if(r.endsWith("&")){
            r = r.substring(0,r.length()-1);
        }
        return r;
    }

    /**
     *
     * @param requestUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return 请求结果
     * @throws Exception
     */
    public static String requestContent(String requestUrl, Map<String,Object> params, String method) throws Exception {

        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {

            //组装请求链接
            StringBuffer sb = new StringBuffer();

            if(method!=null&&method.equalsIgnoreCase("get")){
                requestUrl = requestUrl+"?"+urlEncode(params);
            }

            //默认get
            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if(method!=null&&method.equalsIgnoreCase("post")){
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
            }

            //参数配置
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();

            if (params!= null && method.equalsIgnoreCase("post")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlEncode(params));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //读取数据
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }


    public static void main(String[] args) throws Exception{

        String domain="http://api.xiaocongjisuan.com/";
        String servlet="data/skydriverdata/get";
        String method="get";

        String requestUrl=domain+servlet;
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("appKey",APP_KEY);
        params.put("openId",OPEN_ID);

        //变动部分
        params.put("q","图论精讲");
        params.put("currentPage",1);
        params.put("pageSize",24);
//        params.put("pageSize",24);
//        params.put("pageSize",24);


//        String result=requestContent(requestUrl,params,method);
//        System.out.println(result);


//        Path write = Files.write(Paths.get("E:\\demo.txt"),
//                IntStream.rangeClosed(1, 10).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toList()),
//                UTF_8, CREATE, TRUNCATE_EXISTING);
        LongAdder longAdder=new LongAdder();
        IntStream.rangeClosed(1,1000000).forEach(i->{
            try {
                Files.lines(Paths.get("E:\\demo.txt")).forEach(line->longAdder.increment());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        log.info("total,{}",longAdder.longValue());
        Thread.sleep(200000);
    }
}
