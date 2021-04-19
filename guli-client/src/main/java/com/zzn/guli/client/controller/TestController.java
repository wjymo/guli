package com.zzn.guli.client.controller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client/test")
public class TestController {
    @GetMapping("/success")
    public String test(){
        return "success";
    }

    @GetMapping("/error")
    public String error(@RequestParam(value = "i",required = false,defaultValue = "1")Integer i){
        int j = 10 / i;
        return "error";
    }

    private final static String BASE_PATH= "D:\\temp";
    @PostMapping("/upload")
    public String upload( HttpServletRequest request, HttpServletResponse response){
        HttpServletRequest servletRequest =null;
        StandardMultipartHttpServletRequest multipartHttpServletRequest=null;
        if(request instanceof StandardMultipartHttpServletRequest ){
            multipartHttpServletRequest = (StandardMultipartHttpServletRequest) request;
            servletRequest = ((StandardMultipartHttpServletRequest) request).getRequest();
        }
        Map<String, String[]> parameterMap = multipartHttpServletRequest.getParameterMap();
        String[] chunksArr = parameterMap.get("chunks");
        String[] chunkArr = parameterMap.get("chunk");
//        MultipartFile file = multipartHttpServletRequest.getFile();
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setRepository(new File(BASE_PATH));
        factory.setSizeThreshold(4*1024*1024);
        ServletFileUpload upload = new ServletFileUpload(factory);
        String chunks =null;
        String chunk=null;
        String fileName=null;
        try {
            List<FileItem> list = upload.parseRequest(servletRequest==null?request:servletRequest);
            for (FileItem fileItem : list) {
                boolean isFormField = fileItem.isFormField();
                if(isFormField){
                    String fieldName = fileItem.getFieldName();
                    if(StringUtils.equals("chunks",fieldName)){
                        chunks=fileItem.getString("chunks");
                    }else if(StringUtils.equals("chunk",fieldName)){
                        chunk = fileItem.getString("chunk");
                    }else if(StringUtils.equals("name",fieldName)){
                        fileName = fileItem.getString("name");
                    }
                }
            }
            if(chunk!=null&&fileName!=null){
                String tmpFileName=chunk+"-"+fileName;
                for (FileItem fileItem : list) {
                    boolean isFormField = fileItem.isFormField();
                    if(!isFormField){
                        InputStream inputStream = fileItem.getInputStream();
                        FileUtils.copyInputStreamToFile(inputStream,new File(BASE_PATH,tmpFileName));
                    }
                }
            }

        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "upload success";
    }
}
