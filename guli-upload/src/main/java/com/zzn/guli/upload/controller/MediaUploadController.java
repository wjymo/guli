package com.zzn.guli.upload.controller;

//import com.xuecheng.framework.domain.media.response.CheckChunkResult;
//import com.xuecheng.framework.model.response.ResponseResult;
//import com.xuecheng.manage_media.service.MediaUploadService;

import com.zzn.guli.common.response.CommonResponse;
import com.zzn.guli.common.response.ResponseUtil;
import com.zzn.guli.common.response.ResultCode;
import com.zzn.guli.upload.service.MediaUploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author john
 * @date 2020/1/1 - 13:13
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class MediaUploadController {
    @Autowired
    MediaUploadService mediaUploadService;
    //上传文件的根目录
    @Value("${xc-service-manage-media.upload-location}")
    String uploadPath;
//    //检查文件是否已经存在
//    @PostMapping("/register")
//    public CommonResponse register(@RequestParam("fileMd5") String fileMd5,
//                                   @RequestParam("fileName") String fileName,
//                                   @RequestParam("fileSize") Long fileSize,
//                                   @RequestParam("mimetype") String mimetype,
//                                   @RequestParam("fileExt") String fileExt) {
//        //   检查文件是否上传
//        //   1.   获取文件的路径
//        String filePath = getFilePath(fileMd5, fileExt);
//        File file = new File(filePath);
//
//        //   2.   查询数据库文件是否存在
//        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
//        //   文件存在直接返回
//        if (file.exists() && optional.isPresent()) {
//            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
//        }
//        //下面说明文件并未上传
//        boolean fileFold = createFileFold(fileMd5);
//        if (!fileFold) {
//            //上传文件目录创建失败
//            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_CREATEFOLDER_FAIL);
//        }
//        return ResponseUtil.success(ResultCode.SUCCESS);
//    }
//    private String getFilePath(String fileMd5, String fileExt) {
//        return getFileFolderPath(fileMd5) + fileMd5 + "." + fileExt;
//    }
//    //得到文件所在目录
//    private String getFileFolderPath(String fileMd5) {
//        return uploadPath + getFileFolderRelativePath(fileMd5);
//    }
//    //校验文件块
//    @PostMapping("/checkchunk")
//    public CheckChunkResult checkchunk(@RequestParam("fileMd5") String fileMd5,
//                                       @RequestParam("chunk") Integer chunk,
//                                       @RequestParam("chunkSize") Integer chunkSize) {
//        return mediaUploadService.checkChunk(fileMd5, chunk, chunkSize);
//    }

    //上传文件块
    @PostMapping("/uploadchunk")
    public CommonResponse uploadchunk(HttpServletRequest request, HttpServletResponse response, @RequestParam("files") MultipartFile[] files) {
        StandardMultipartHttpServletRequest multipartHttpServletRequest = null;
        if (request instanceof StandardMultipartHttpServletRequest) {
            multipartHttpServletRequest = (StandardMultipartHttpServletRequest) request;
        }
        Map<String, String[]> parameterMap = multipartHttpServletRequest.getParameterMap();
        String[] chunkArr = parameterMap.get("chunk");
        String[] chunksArr = parameterMap.get("chunks");
        String[] uids = parameterMap.get("uid");
        String uid = uids[0];
        Integer chunk = null;
        Integer chunks = null;
        if (!Objects.isNull(chunkArr) && !Objects.isNull(chunksArr)) {
            chunk = Integer.parseInt(chunkArr[0]);
            chunks = Integer.parseInt(chunksArr[0]);
        }
        MultipartFile file = files[0];
        String name = file.getOriginalFilename();
        String basePath = uploadPath + uid + "\\";
        File baseFile = new File(basePath);
        if (!baseFile.exists()) {
            baseFile.mkdir();
        }
        String path = basePath + ((chunk == null ? "" : (chunk + "-")) + name);
        //块文件
        File chunkfile = new File(path);
        if (chunkfile.exists()) {
            return ResponseUtil.success(null, "文件已存在", 200);
        }
        //上传的块文件
        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(chunkfile)) {
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("upload   chunk  file   fail:{}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        }
        return ResponseUtil.success(ResultCode.SUCCESS);
    }

    //合并文件块
    @PostMapping("/mergechunks")
    public CommonResponse mergechunks(@RequestParam("uid") String uid, @RequestParam("partSize") Integer partSize) {
        String basePath = uploadPath + uid + "\\";
        File baseFile = new File(basePath);
        if (!baseFile.exists()) {
            throw new RuntimeException("文件不存在");
        }
        File[] files = baseFile.listFiles();
        ByteBuffer[] buffers = new ByteBuffer[files.length];
        String name = null;
//        FileInputStream fileInputStream =null;
        try {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (i == 0) {
                    name = file.getName();
                    name = name.substring(name.indexOf("-") + 1);
                }
                buffers[i] = ByteBuffer.allocate(partSize);
                FileInputStream fileInputStream = new FileInputStream(file);
                FileChannel fileChannel = fileInputStream.getChannel();
                fileChannel.read(buffers[i]);
                buffers[i].flip();
                fileInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.error("文件合并失败");
        }finally {
//            if(fileInputStream!=null){
//                try {
//                    fileInputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        try (FileOutputStream outFile = new FileOutputStream(basePath + name);) {
            FileChannel fileChanneOut = outFile.getChannel();
            fileChanneOut.write(buffers);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.error("文件合并失败");
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            boolean delete = file.delete();
            System.out.println("文件："+file.getName()+" 被删除："+delete);
        }

        return ResponseUtil.success(ResultCode.SUCCESS);
    }


    private boolean createChunkFileFolder(String fileMd5) {
        //创建上传文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        if (!chunkFileFolder.exists()) {
            //创建文件夹
            return chunkFileFolder.mkdirs();
        }
        return true;
    }

    //   得到块文件所在目录
    private String getChunkFileFolderPath(String fileMd5) {
        return getFileFolderPath(fileMd5) + "/" + "chunks" + "/";
    }

    /**
     * 根据文件md5得到文件路径
     * 规则：
     * 一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件扩展名
     *
     * @param fileMd5 文件md5值
     * @param fileExt 文件扩展名
     * @return 文件路径
     */
    private String getFilePath(String fileMd5, String fileExt) {
        return getFileFolderPath(fileMd5) + fileMd5 + "." + fileExt;
    }

    //得到文件所在目录
    private String getFileFolderPath(String fileMd5) {
        return uploadPath + getFileFolderRelativePath(fileMd5);
    }

    //得到文件目录相对路径，路径中去掉根目录
    private String getFileFolderRelativePath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/"
                + fileMd5.substring(1, 2) + "/"
                + fileMd5 + "/";
    }


}