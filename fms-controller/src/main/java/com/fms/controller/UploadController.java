package com.fms.controller;

import com.alibaba.fastjson.JSONArray;
import com.anniweiya.fastdfs.FastDFSTemplate;
import com.anniweiya.fastdfs.FastDfsInfo;
import com.anniweiya.fastdfs.exception.FastDFSException;
import com.fms.domain.filemanage.FileParser;
import com.fms.domain.filemanage.FileParserJar;
import com.fms.domain.filemanage.FileType;
import com.fms.domain.filemanage.upload.Chunk;
import com.fms.domain.filemanage.upload.FileInfo;
import com.fms.service.filemanage.*;
import com.fms.utils.Ftp;
import com.fms.utils.FtpUtil;
import com.fms.utils.SFTPUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.handu.apollo.utils.CollectionUtil;
import com.handu.apollo.utils.ExtUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@RestController
@CrossOrigin
public class UploadController {
    //    @Value("${prop.upload-folder}")

    @Autowired
    private FastDFSTemplate fastDFSTemplate;
    @Autowired
    private DirectoryService directoryService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ChunkService chunkService;
    @Autowired
    private FileTypeService fileTypeService;
    @Autowired
    private FileParserService fileParserService;
    @Autowired
    private Environment env;
    @Autowired
    private FileParserJarService fileParserJarService;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @RequestMapping(value = "/uploadFromFtpFile", method = RequestMethod.POST)
    public void uploadFromFtpFile(@RequestParam String ipAddr, @RequestParam int port,@RequestParam String userName,@RequestParam String pwd,@RequestParam String path,HttpServletResponse response) {
        Runnable runnable = new Runnable() {
            public void run() {
                Ftp ftp=new Ftp();
                ftp.setIpAddr(ipAddr);
                ftp.setPort(port);
                ftp.setUserName(userName);
                ftp.setPwd(pwd);
                ftp.setPath(path);
                ftp.setDirectoryId(1L);
                //下载ftp到本地临时目录
                String directory = ftp.getPath();

                String tempFold = env.getProperty("file.tmpPath") + "/ftpFile/" + UUID.randomUUID().toString().replaceAll("-", "");

                Path dirPath = Paths.get(tempFold);
                if (!Files.exists(dirPath)) {
                    try {
                        Files.createDirectories(dirPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (21 == ftp.getPort())
                {
                    try {
                        FtpUtil.connectFtp(ftp);
                        FtpUtil.startDown(ftp, tempFold, directory);
                        FtpUtil.handleFile(ftp,directory);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {

                    try {
                        JSONArray arr=FtpUtil.handleFile(ftp,directory);
                        for(int i=0;i<arr.size();i++){
                            kafkaTemplate.send("operation_3rd3",arr.getJSONObject(i).toJSONString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SFTPUtils sf = SFTPUtils.getInstance(ftp);
                    Vector<ChannelSftp.LsEntry> files = null;        //查看文件列表
                    try {
                        files = sf.listFiles(directory);
                        if (files != null && files.size() > 0)
                        {
                            for (ChannelSftp.LsEntry lsEntry : files)
                            {
                                String fileName = lsEntry.getFilename();
                                if(!fileName.equals(".") && !fileName.equals("..")) {
                                    if (!lsEntry.getAttrs().isDir()) {
                                        File download = sf.download(directory + "/" + lsEntry.getFilename(), tempFold + "/" + fileName);
                                        try {
                                            FileInputStream fis = null;
                                            ByteArrayOutputStream bos = null;
                                            byte[] buffer = null;
                                            fis = new FileInputStream(download.getAbsolutePath());
                                            bos = new ByteArrayOutputStream();

                                            byte[] b = new byte[1024];

                                            int n;

                                            while ((n = fis.read(b)) != -1) {
                                                bos.write(b, 0, n);
                                            }

                                            buffer = bos.toByteArray();
                                            String suffix = fileName.toLowerCase().endsWith("tar.gz") ? "tar.gz" : fileName.indexOf(".") == -1 ? "" :fileName.substring(fileName.lastIndexOf(".") + 1);
                                            FastDfsInfo info = fastDFSTemplate.upload(buffer, suffix);
                                            if (info != null) {
                                                Long dirId = ftp.getDirectoryId();
//                                String relativePath = fileInfo.getWebkitRelativePath();


                                                //test
//                                if (!Strings.isNullOrEmpty(relativePath)) {
//                                }
                                                Date currentDate = new Date();
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                                String relativePath = sdf.format(currentDate);
                                                dirId = directoryService.createRelativePath(dirId, relativePath.split("/"));
                                                saveFile(info,fileName,suffix,dirId,null);

                                                // 清除文件夹
                                                File tempFile = new File(tempFold);
                                                if (tempFile.isDirectory() && tempFile.exists()) {
                                                    tempFile.delete();
                                                }

                                            }
                                        } catch (FastDFSException e) {
                                            e.printStackTrace();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (SftpException e) {
                        e.printStackTrace();
                    }
                    sf.disconnect();
                }

            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.SECONDS);
    }


    @RequestMapping(value = "/uploadFileToLocal", method = RequestMethod.POST)
    public void uploadFileToLocal(@RequestParam String ipAddr, @RequestParam int port,@RequestParam String userName,@RequestParam String pwd,@RequestParam String path,HttpServletResponse response) {
        Runnable runnable = new Runnable() {
            public void run() {
                Ftp ftp=new Ftp();
                ftp.setIpAddr(ipAddr);
                ftp.setPort(port);
                ftp.setUserName(userName);
                ftp.setPwd(pwd);
                ftp.setPath(path);
                ftp.setDirectoryId(1L);
                //下载ftp到本地临时目录
                String directory = ftp.getPath();

                String tempFold = env.getProperty("file.tmpPath") + "/ftpFile/" + UUID.randomUUID().toString().replaceAll("-", "");

                Path dirPath = Paths.get(tempFold);
                if (!Files.exists(dirPath)) {
                    try {
                        Files.createDirectories(dirPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (21 == ftp.getPort())
                {
                    try {
                        FtpUtil.connectFtp(ftp);
                        FtpUtil.startDown(ftp, tempFold, directory);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    SFTPUtils sf = SFTPUtils.getInstance(ftp);
                    Vector<ChannelSftp.LsEntry> files = null;        //查看文件列表
                    try {
                        files = sf.listFiles(directory);
                        if (files != null && files.size() > 0)
                        {
                            for (ChannelSftp.LsEntry lsEntry : files)
                            {
                                String fileName = lsEntry.getFilename();
                                if(!fileName.equals(".") && !fileName.equals("..")) {
                                    if (!lsEntry.getAttrs().isDir()) {
                                        File download = sf.download(directory + "/" + lsEntry.getFilename(), tempFold + "/" + fileName);
                                        try {
                                            FileInputStream fis = null;
                                            ByteArrayOutputStream bos = null;
                                            byte[] buffer = null;
                                            fis = new FileInputStream(download.getAbsolutePath());
                                            bos = new ByteArrayOutputStream();

                                            byte[] b = new byte[1024];

                                            int n;

                                            while ((n = fis.read(b)) != -1) {
                                                bos.write(b, 0, n);
                                            }

                                            buffer = bos.toByteArray();
                                            String suffix = fileName.toLowerCase().endsWith("tar.gz") ? "tar.gz" : fileName.indexOf(".") == -1 ? "" :fileName.substring(fileName.lastIndexOf(".") + 1);
                                            FastDfsInfo info = fastDFSTemplate.upload(buffer, suffix);
                                            if (info != null) {
                                                Long dirId = ftp.getDirectoryId();
//                                String relativePath = fileInfo.getWebkitRelativePath();


                                                //test
//                                if (!Strings.isNullOrEmpty(relativePath)) {
//                                }
                                                Date currentDate = new Date();
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                                String relativePath = sdf.format(currentDate);
                                                dirId = directoryService.createRelativePath(dirId, relativePath.split("/"));
                                                saveFile(info,fileName,suffix,dirId,null);

                                                // 清除文件夹
                                                File tempFile = new File(tempFold);
                                                if (tempFile.isDirectory() && tempFile.exists()) {
                                                    tempFile.delete();
                                                }

                                            }
                                        } catch (FastDFSException e) {
                                            e.printStackTrace();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (SftpException e) {
                        e.printStackTrace();
                    }
                    sf.disconnect();
                }

            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.SECONDS);
    }


    /**
     * 上传分片文件
     * @param chunk
     * @param response
     * @return
     */
    @PostMapping("/chunk")
    public String uploadChunk(Chunk chunk, HttpServletResponse response) {
        MultipartFile file = chunk.getFile();
        String fileTmp = env.getProperty("file.tmpPath") + "/";
//        log.debug("file originName: {}, chunkNumber: {}", file.getOriginalFilename(), chunk.getChunkNumber());

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(generatePath(fileTmp, chunk));
            //文件写入指定路径
            Files.write(path, bytes);
//            log.debug("文件 {} 写入成功, uuid:{}", chunk.getFilename(), chunk.getIdentifier());
//            chunkService.saveChunk(chunk);

            chunkService.add(chunk);
            return "文件上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return "后端异常...";
        }
    }

    /**
     * 服务端检测并创建分片文件存储目录
     * @param path
     * @param chunk
     * @return
     * @throws IOException
     */
    private String generatePath(String path, Chunk chunk) throws IOException {
        String dir = path + chunk.getIdentifier();
        Path dirPath = Paths.get(dir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        String fileName = chunk.getTotalChunks().intValue() == 1 ? chunk.getFilename() : (chunk.getFilename() + "-" + chunk.getChunkNumber());
        Path filePath = Paths.get(dirPath + "/" + fileName);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        return filePath.toString();
    }

    /**
     * 检测分片文件是否已经存在若存在在直接跳过，若不存在在上传
     * @param chunk
     * @param response
     * @return
     */
    @GetMapping("/chunk")
    public Object checkChunk(Chunk chunk, HttpServletResponse response) {

        Chunk oChunk = chunkService.query(chunk);
        if (oChunk==null) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }else{
            String fileTmp = env.getProperty("file.tmpPath") + "/";
            String dir = fileTmp + chunk.getIdentifier();
            Path dirPath = Paths.get(dir);
            String fileName = chunk.getTotalChunks().intValue() == 1 ? chunk.getFilename() : (chunk.getFilename() + "-" + oChunk.getChunkNumber());
            Path filePath = Paths.get(dirPath + "/" + fileName);
            if (!Files.exists(filePath)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            }
        }
        return chunk;
    }

    /**
     * 文件合并
     * @param fileInfo
     * @return
     */
    @PostMapping("/mergeFile")
    public String mergeFile(FileInfo fileInfo) {
        String uploadFolder = env.getProperty("file.tmpPath");
        String fileName = fileInfo.getFilename();
        String path = uploadFolder + "/" + fileInfo.getIdentifier() + "/" + fileInfo.getFilename();
        String folder = uploadFolder + "/" + fileInfo.getIdentifier();
        merge(path, folder);
        fileInfo.setLocation(path);
//        fileInfoService.addFileInfo(fileInfo);

        try {
            FileInputStream fis = null;
            ByteArrayOutputStream bos = null;
            byte[] buffer = null;
            fis = new FileInputStream(fileInfo.getLocation());
            bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int n;

            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            buffer = bos.toByteArray();
            String suffix = fileName.toLowerCase().endsWith("tar.gz") ? "tar.gz" : fileName.indexOf(".") == -1 ? "" :fileName.substring(fileName.lastIndexOf(".") + 1);
            FastDfsInfo info = fastDFSTemplate.upload(buffer, suffix);
            if (info != null) {
                Long dirId = fileInfo.getDirectoryId();

                Date currentDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                String relativePath = sdf.format(currentDate) + "/" + fileInfo.getWebkitRelativePath();

                if (!Strings.isNullOrEmpty(relativePath)) {
                    relativePath = relativePath.substring(0, relativePath.lastIndexOf("/"));
                    dirId = directoryService.createRelativePath(dirId, relativePath.split("/"));
                }
                saveFile(info,fileName,suffix,dirId,fileInfo.getIdentifier());

                chunkService.delete(fileInfo.getIdentifier());
                // 清除文件夹
                File tempFile = new File(path);
                if (tempFile.isDirectory() && tempFile.exists()) {
                    tempFile.delete();
                }

            }
        } catch (FastDFSException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "合并成功";
    }


    /**
     * 文件合并
     * @param fileInfo
     * @return
     */
    @PostMapping("/mergeFileForJar")
    public String mergeFileForJar(FileInfo fileInfo) {

        String fileTmp = env.getProperty("parser.path") + "/";

        String name = fileInfo.getFilename();

        if(!name.endsWith(".jar")){
            return "非jar文件不作处理";
        }

        try
        {
            String uploadFolder = env.getProperty("file.tmpPath");

            String path = uploadFolder + "/" + fileInfo.getIdentifier() + "/" + fileInfo.getFilename();
            String folder = uploadFolder + "/" + fileInfo.getIdentifier();
            merge(path, folder);
            fileInfo.setLocation(path);
            FileInputStream fis = null;
            ByteArrayOutputStream bos = null;
            byte[] bytes = null;
            fis = new FileInputStream(fileInfo.getLocation());
            bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int n;

            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            bytes = bos.toByteArray();

            Path pathPath = Paths.get(fileTmp+name);
            Files.write(pathPath, bytes);

            FileParserJar fileParserJar = new FileParserJar();
            fileParserJar.setName(name);
            Map<String,Object> params = new HashMap<String,Object>() ;
            params.put("name",name);
            List<FileParserJar> fileParserJarList = fileParserJarService.query(params);
            if(!CollectionUtil.isNotEmpty(fileParserJarList)){
                fileParserJar.setPath(pathPath.toString());
                fileParserJarService.add(fileParserJar);
            }
            chunkService.delete(fileInfo.getIdentifier());
            // 清除文件夹
            File tempFile = new File(path);
            if (tempFile.isDirectory() && tempFile.exists()) {
                tempFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "后端异常...";
        }


        return "合并成功";
    }

    /**
     * 保存文件
     * @param info
     * @param fileName
     * @param suffix
     * @param dirId
     * @param fileMd5
     */
    public void saveFile(FastDfsInfo info,String fileName,String suffix,Long dirId,String fileMd5){

        com.fms.domain.filemanage.File file = new com.fms.domain.filemanage.File();
        file.setId(System.currentTimeMillis());
        file.setName(fileName);
        file.setRealPath(info.getPath());
        file.setGroups(info.getGroup());
        file.setType(suffix);
        file.setDirectoryId(dirId);
        file.setFileMd5(fileMd5);
        file.setIsParser(0);
        file.setIsExport(0);

        String fileSuffix = "";
        if(fileName.lastIndexOf(".")>0){
            fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1);
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("fileSuffix", "<"+fileSuffix+">");
        List<FileType> fileTypeList =  fileTypeService.getListBySuffix(params);

        if(fileTypeList.size()>0){
            file.setClassId(fileTypeList.get(0).getId());
            file.setClassName(fileTypeList.get(0).getName());
            file.setFatherClassName(fileTypeList.get(0).getType());

        }


        if(fileTypeList==null||fileTypeList.size()==0){
            file.setClassType("其他");
        }else{
            Map<String,Object> fileParserIds = new HashMap<String,Object>();
            int tCount = 0;
            for(FileType ftype:fileTypeList){
                String fileParseId = ftype.getFileParserIds();
                if(!Strings.isNullOrEmpty(fileParseId)){
                    String [] tFileParserIds = fileParseId.split(",");
                    for(String tFP:tFileParserIds){
                        if(!fileParserIds.containsKey(tFP)){
                            fileParserIds.put(tFP,tFP);
                            tCount++;
                        }
                    }
                }
            }
            if(tCount>1){
                file.setClassType("待分类");
            }else if(tCount==1){
                file.setClassType("预分类");
            }else{
                file.setClassType("其他");
            }

            String recommendParserId = "";
//            List<FileParser> fPs = new ArrayList<FileParser>();
            Set<String> filePK =  fileParserIds.keySet();
            if(filePK.size()==1) {
                Iterator<String> it = filePK.iterator();
                while(it.hasNext()){
                    recommendParserId=it.next();
                    if(!Strings.isNullOrEmpty(recommendParserId)){
                        file.setRecommendParserId(Long.valueOf(recommendParserId));
                        FileParser fileParser = fileParserService.get(file.getRecommendParserId());
                        if(fileParser!=null){
                            file.setRecommendParserName(fileParser.getName());
                            continue;
                        }

                    }
                }
            }



        }
//        if(fileTypeList==null||fileTypeList.size()==0){
//            file.setClassType("其他");
//        }else if(fileTypeList.size()>1){
//            file.setClassType("待分类");
//        }else{
//            file.setClassId(fileTypeList.get(0).getId());
//            file.setClassName(fileTypeList.get(0).getName());
//            file.setClassType("预分类");
//            String recommendParserIds = fileTypeList.get(0).getFileParserIds();
//            String recommendParserId = "";
//            if(!Strings.isNullOrEmpty(recommendParserIds)){
//                if(recommendParserIds.indexOf(",")>0){
//                    recommendParserId = recommendParserIds.substring(0,recommendParserIds.indexOf(","));
//                }else{
//                    recommendParserId = recommendParserIds;
//                }
//            }
//
//            if(!Strings.isNullOrEmpty(recommendParserId)){
//                file.setRecommendParserId(Long.valueOf(recommendParserId));
//                FileParser fileParser = fileParserService.get(file.getRecommendParserId());
//                file.setRecommendParserName(fileParser.getName());
//            }
//
//
//        }

        fileService.add(file);
    }

    /**
     * 分片文件合并
     * @param targetFile
     * @param folder
     */
    public static void merge(String targetFile, String folder) {
        try {
            if (!Files.exists(Paths.get(targetFile))) {
                Path p = Files.createFile(Paths.get(targetFile));

                Files.list(Paths.get(folder))
                        .filter(path -> path.getFileName().toString().contains("-") && !p.getFileName().equals(path.getFileName()))
                        .sorted((o1, o2) -> {
                            String p1 = o1.getFileName().toString();
                            String p2 = o2.getFileName().toString();
                            int i1 = p1.lastIndexOf("-");
                            int i2 = p2.lastIndexOf("-");
                            return Integer.valueOf(p2.substring(i2)).compareTo(Integer.valueOf(p1.substring(i1)));
                        })
                        .forEach(path -> {
                            try {
                                //以追加的形式写入文件
                                Files.write(Paths.get(targetFile), Files.readAllBytes(path), StandardOpenOption.APPEND);
                                //合并后删除该块
                                Files.delete(path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/uploadJars", method = RequestMethod.POST)
    public String singleFileUpload( MultipartFile file,HttpServletResponse response) {

        String fileTmp = env.getProperty("parser.path") + "/";

        String name = file.getOriginalFilename();

        if(!name.endsWith(".jar")){
            return "非jar文件不作处理";
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(fileTmp+name);
            Files.write(path, bytes);

            FileParserJar fileParserJar = new FileParserJar();
            fileParserJar.setName(name);
            Map<String,Object> params = new HashMap<String,Object>() ;
            params.put("name",name);
            List<FileParserJar> fileParserJarList = fileParserJarService.query(params);
            if(!CollectionUtil.isNotEmpty(fileParserJarList)){
                fileParserJar.setPath(path.toString());
                fileParserJarService.add(fileParserJar);
            }
            return "文件上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return "后端异常...";
        }


    }

    @RequestMapping(value = "/uploadFromFtp", method = RequestMethod.POST)
    public Object singleFileUpload(Ftp ftp, HttpServletResponse response) {


        //下载ftp到本地临时目录
        String directory = ftp.getPath();

        String tempFold = env.getProperty("file.tmpPath") + "/ftpFile/" + UUID.randomUUID().toString().replaceAll("-", "");

        Path dirPath = Paths.get(tempFold);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (21 == ftp.getPort())
        {
            try {
                FtpUtil.connectFtp(ftp);
                FtpUtil.startDown(ftp, tempFold, directory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            SFTPUtils sf = SFTPUtils.getInstance(ftp);
            Vector<ChannelSftp.LsEntry> files = null;        //查看文件列表
            try {
                files = sf.listFiles(directory);
                if (files != null && files.size() > 0)
                {
                    for (ChannelSftp.LsEntry lsEntry : files)
                    {
                        String fileName = lsEntry.getFilename();
                        if(!fileName.equals(".") && !fileName.equals("..")) {
                            if (!lsEntry.getAttrs().isDir()) {
                                File download = sf.download(directory + "/" + lsEntry.getFilename(), tempFold + "/" + fileName);
                                try {
                                    FileInputStream fis = null;
                                    ByteArrayOutputStream bos = null;
                                    byte[] buffer = null;
                                    fis = new FileInputStream(download.getAbsolutePath());
                                    bos = new ByteArrayOutputStream();

                                    byte[] b = new byte[1024];

                                    int n;

                                    while ((n = fis.read(b)) != -1) {
                                        bos.write(b, 0, n);
                                    }

                                    buffer = bos.toByteArray();
                                    String suffix = fileName.toLowerCase().endsWith("tar.gz") ? "tar.gz" : fileName.indexOf(".") == -1 ? "" :fileName.substring(fileName.lastIndexOf(".") + 1);
                                    FastDfsInfo info = fastDFSTemplate.upload(buffer, suffix);
                                    if (info != null) {
                                        Long dirId = ftp.getDirectoryId();
//                                String relativePath = fileInfo.getWebkitRelativePath();


                                        //test
//                                if (!Strings.isNullOrEmpty(relativePath)) {
//                                }
                                        Date currentDate = new Date();
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                        String relativePath = sdf.format(currentDate);
                                        dirId = directoryService.createRelativePath(dirId, relativePath.split("/"));
                                        saveFile(info,fileName,suffix,dirId,null);

                                        // 清除文件夹
                                        File tempFile = new File(tempFold);
                                        if (tempFile.isDirectory() && tempFile.exists()) {
                                            tempFile.delete();
                                        }

                                    }
                                } catch (FastDFSException e) {
                                    e.printStackTrace();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (SftpException e) {
                e.printStackTrace();
            }
            sf.disconnect();
        }
        return ExtUtil.success("文件修改成功！");
    }


}