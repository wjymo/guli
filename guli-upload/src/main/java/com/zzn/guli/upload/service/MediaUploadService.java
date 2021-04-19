package com.zzn.guli.upload.service;

//import com.xuecheng.framework.domain.media.MediaFile;
//import com.xuecheng.framework.domain.media.response.CheckChunkResult;
//import com.xuecheng.framework.domain.media.response.MediaCode;
//import com.xuecheng.framework.exception.ExceptionCast;
//import com.xuecheng.framework.model.response.CommonCode;
//import com.xuecheng.framework.model.response.ResponseResult;
//import com.xuecheng.manage_media.dao.MediaFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @author john
 * @date 2020/1/1   -   12:07
 */
@Service
@Slf4j
public class MediaUploadService {
//    @Autowired
//    MediaFileRepository mediaFileRepository;
//
//    //上传文件的根目录
//    @Value("${xc-service-manage-media.upload-location}")
//    String uploadPath;
//
//    /**
//     * 根据文件md5得到文件路径
//     * 规则：
//     * 一级目录：md5的第一个字符
//     * 二级目录：md5的第二个字符
//     * 三级目录：md5
//     * 文件名：md5+文件扩展名
//     *
//     * @param fileMd5 文件md5值
//     * @param fileExt 文件扩展名
//     * @return 文件路径
//     */
//    private String getFilePath(String fileMd5, String fileExt) {
//        return getFileFolderPath(fileMd5) + fileMd5 + "." + fileExt;
//    }
//
//    //得到文件目录相对路径，路径中去掉根目录
//    private String getFileFolderRelativePath(String fileMd5) {
//        return fileMd5.substring(0, 1) + "/"
//                + fileMd5.substring(1, 2) + "/"
//                + fileMd5 + "/";
//    }
//
//    //得到文件所在目录
//    private String getFileFolderPath(String fileMd5) {
//        return uploadPath + getFileFolderRelativePath(fileMd5);
//    }
//
//    //创建文件目录
//    private boolean createFileFold(String fileMd5) {
//        //创建上传文件，目录
//        String fileFolderPath = getFileFolderPath(fileMd5);
//        File fileFolder = new File(fileFolderPath);
//        if (!fileFolder.exists()) {
//            //创建文件夹
//            return fileFolder.mkdirs();
//        }
//        return true;
//    }
//
//    //检查待上传文件是否存在
//    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
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
//        return new ResponseResult(CommonCode.SUCCESS);
//    }
//
//    //   得到块文件所在目录
//    private String getChunkFileFolderPath(String fileMd5) {
//        return getFileFolderPath(fileMd5) + "/" + "chunks" + "/";
//    }
//
//    //检查块文件
//    public CheckChunkResult checkChunk(String fileMd5, Integer chunk, Integer chunkSize) {
//        //得到块文件所在路径
//        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
//        //块文件的文件名称以1,2,3..序号命名，没有扩展名
//        File chunkFile = new File(chunkFileFolderPath + chunk);
//        if (chunkFile.exists()) {
//            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK, true);
//        } else {
//            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK, false);
//        }
//    }
//
//    private boolean createChunkFileFolder(String fileMd5) {
//        //创建上传文件目录
//        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
//        File chunkFileFolder = new File(chunkFileFolderPath);
//        if (!chunkFileFolder.exists()) {
//            //创建文件夹
//            return chunkFileFolder.mkdirs();
//        }
//        return true;
//    }
//
//    //块文件上传
//    public ResponseResult uploadChunk(MultipartFile file, String fileMd5, Integer chunk) {
//        if (file == null) {
//            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_ISNULL);
//        }
//        //创建块文件目录
//        boolean fileFold = createChunkFileFolder(fileMd5);
//        //块文件
//        File chunkfile = new File(getChunkFileFolderPath(fileMd5) + chunk);
//        //上传的块文件
//        InputStream inputStream = null;
//        FileOutputStream outputStream = null;
//        try {
//            inputStream = file.getInputStream();
//            outputStream = new FileOutputStream(chunkfile);
//            IOUtils.copy(inputStream, outputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("upload         chunk         file         fail:{}", e.getMessage());
//            ExceptionCast.cast(MediaCode.CHUNK_FILE_UPLOAD_FAIL);
//        } finally {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                outputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return new ResponseResult(CommonCode.SUCCESS);
//    }
//
//    //合并块文件
//    public ResponseResult mergeChunks(String fileMd5, String fileName, Long fileSize, String
//            mimeType, String fileExt) {
//        //获取块文件的路径
//        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
//        File chunkFileFolder = new File(chunkFileFolderPath);
//        if (!chunkFileFolder.exists()) {
//            chunkFileFolder.mkdirs();
//        }
//        //合并文件路径
//        File mergeFile = new File(getFilePath(fileMd5, fileExt));
//        //创建合并文件
//        //合并文件存在先删除再创建
//        if (mergeFile.exists()) {
//            mergeFile.delete();
//        }
//        boolean newFile = false;
//        try {
//            newFile = mergeFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.error("mergechunks..create mergeFile fail:{}", e.getMessage());
//        }
//        if (!newFile) {
//            ExceptionCast.cast(MediaCode.MERGE_FILE_CREATEFAIL);
//        }
//        //获取块文件，此列表是已经排好序的列表
//        List<File> chunkFiles = getChunkFiles(chunkFileFolder);
//        //合并文件
//        mergeFile = mergeFile(mergeFile, chunkFiles);
//        if (mergeFile == null) {
//            ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
//        }
//        //校验文件
//        boolean checkResult = this.checkFileMd5(mergeFile, fileMd5);
//        if (!checkResult) {
//            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
//        }
//        //将文件信息保存到数据库
//        MediaFile mediaFile = new MediaFile();
//        mediaFile.setFileId(fileMd5);
//        mediaFile.setFileName(fileMd5 + "." + fileExt);
//        mediaFile.setFileOriginalName(fileName);
//        //文件路径保存相对路径
//        mediaFile.setFilePath(getFileFolderRelativePath(fileMd5));
//        mediaFile.setFileSize(fileSize);
//        mediaFile.setUploadTime(new Date());
//        mediaFile.setMimeType(mimeType);
//        mediaFile.setFileType(fileExt);
//        //状态为上传成功
//        mediaFile.setFileStatus("301002");
//        MediaFile save = mediaFileRepository.save(mediaFile);
//        return new ResponseResult(CommonCode.SUCCESS);
//    }
//
//    //校验文件的md5值
//    private boolean checkFileMd5(File mergeFile, String md5) {
//        if (mergeFile == null || StringUtils.isEmpty(md5)) {
//            return false;
//        }
//        //进行md5校验
//        FileInputStream mergeFileInputstream = null;
//        try {
//            mergeFileInputstream = new FileInputStream(mergeFile);
//            //得到文件的md5
//            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileInputstream);
//            //比较md5
//            if (md5.equalsIgnoreCase(mergeFileMd5)) {
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("checkFileMd5 error,file is:{},md5 is: {} ", mergeFile.getAbsoluteFile(), md5);
//        } finally {
//            try {
//                mergeFileInputstream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }
//
//    //获取所有块文件
//    private List<File> getChunkFiles(File chunkFileFolder) {
//        //获取路径下的所有块文件
//        File[] chunkFiles = chunkFileFolder.listFiles();
//        //将文件数组转成list，并排序
//        List<File> chunkFileList = new ArrayList<File>();
//        chunkFileList.addAll(Arrays.asList(chunkFiles));
//        //排序
//        Collections.sort(chunkFileList, new Comparator<File>() {
//            @Override
//            public int compare(File o1, File o2) {
//                if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
//                    return 1;
//                }
//                return -1;
//            }
//        });
//        return chunkFileList;
//    }
//
//    //合并文件
//    private File mergeFile(File mergeFile, List<File> chunkFiles) {
//        try {
//            //创建写文件对象
//            RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
//            //遍历分块文件开始合并
//            //读取文件缓冲区
//            byte[] b = new byte[1024];
//            for (File chunkFile : chunkFiles) {
//                RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
//                int len = -1;
//                //读取分块文件
//                while ((len = raf_read.read(b)) != -1) {
//                    //向合并文件中写数据
//                    raf_write.write(b, 0, len);
//                }
//                raf_read.close();
//            }
//            raf_write.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("merge file error:{}", e.getMessage());
//            return null;
//        }
//        return mergeFile;
//    }
}