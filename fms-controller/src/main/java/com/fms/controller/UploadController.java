package com.fms.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anniweiya.fastdfs.FastDFSTemplate;
import com.anniweiya.fastdfs.FastDfsInfo;
import com.anniweiya.fastdfs.exception.FastDFSException;
import com.caeit.parser.excel.ExcelParser;
import com.caeit.parser.json.JsonParser;
import com.caeit.parser.sql.SqlParser;
import com.caeit.parser.xml.XmlParser;
import com.fms.domain.columnSet.ColumnInfo;
import com.fms.domain.columnSet.TableInfo;
import com.fms.domain.filemanage.FileParser;
import com.fms.domain.filemanage.FileParserJar;
import com.fms.domain.filemanage.FileType;
import com.fms.domain.filemanage.upload.Chunk;
import com.fms.domain.filemanage.upload.FileInfo;
import com.fms.service.filemanage.*;
import com.fms.utils.Ftp;
import com.fms.utils.FtpUtil;
import com.fms.utils.JSONUtils;
import com.fms.utils.SFTPUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.handu.apollo.utils.CollectionUtil;
import com.handu.apollo.utils.ExtUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
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
    public void uploadFromFtpFile(@RequestParam String ipAddr, @RequestParam int port, @RequestParam String userName, @RequestParam String pwd, @RequestParam String path, HttpServletResponse response) {
        Runnable runnable = new Runnable() {

            public void run() {
        /*        // 测试全字段匹配
                try {
                    File testFile = new File("D:\\zw_kzsx_sb.xls");

                    handleFile(kafkaTemplate, testFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                Ftp ftp = new Ftp();
                ftp.setIpAddr(ipAddr);
                ftp.setPort(port);
                ftp.setUserName(userName);
                ftp.setPwd(pwd);
                ftp.setPath(path);
                ftp.setDirectoryId(0L);
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

                if (21 == ftp.getPort()) {
                    try {
                        FtpUtil.connectFtp(ftp);
                        FtpUtil.startDown(ftp, tempFold, directory);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    SFTPUtils sf = SFTPUtils.getInstance(ftp);
                    Vector<ChannelSftp.LsEntry> files = null;        //查看文件列表
                    try {
                        Long directoryId = ftp.getDirectoryId();

                        files = sf.listFiles(directory);
                        if (files != null && files.size() > 0) {
                            for (ChannelSftp.LsEntry lsEntry : files) {
                                /*Date currentDate = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");*/

                                //String relativePath = sdf.format(currentDate);
                                String relativePath = ipAddr;
                                Long dirId = directoryService.createRelativePath(directoryId, relativePath.split("/"));

                                String fileName = lsEntry.getFilename();
                                Map<String, Object> params = new HashMap<>();
                                params.put("name", fileName);
                                params.put("directoryId", dirId);

                                // 去重
                                int count = fileService.queryCount(params);
                                if (count > 0) {
                                    continue;
                                }
                                if (!fileName.equals(".") && !fileName.equals("..")) {
                                    if (!lsEntry.getAttrs().isDir()) {
                                        File download = sf.download(directory + "/" + lsEntry.getFilename(), tempFold + "/" + fileName);
//                                        File download = new File("d:\\zw_kzsx_sb.xml");
//                                        File download = new File("d:\\zw_kzsx_sb.xls");

                                        handleFile(download);

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
                                            String suffix = fileName.toLowerCase().endsWith("tar.gz") ? "tar.gz" : fileName.indexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
                                            FastDfsInfo info = fastDFSTemplate.upload(buffer, suffix);
                                            if (info != null) {

//                                String relativePath = fileInfo.getWebkitRelativePath();


                                                //test
//                                if (!Strings.isNullOrEmpty(relativePath)) {
//                                }

                                                saveFile(info, fileName, suffix, dirId, null);

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
//                                        kafkaTemplate.send("operation_3rd3", jsonStr);
                                    }
                                }
                            }
                        }
                    } catch (SftpException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sf.disconnect();
                }

            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 0, 1800, TimeUnit.SECONDS);
    }


    @RequestMapping(value = "/uploadFileToLocal", method = RequestMethod.POST)
    public void uploadFileToLocal(@RequestParam String ipAddr, @RequestParam int port, @RequestParam String userName, @RequestParam String pwd, @RequestParam String path, HttpServletResponse response) {
        Runnable runnable = new Runnable() {
            public void run() {
                Ftp ftp = new Ftp();
                ftp.setIpAddr(ipAddr);
                ftp.setPort(port);
                ftp.setUserName(userName);
                ftp.setPwd(pwd);
                ftp.setPath(path);
                ftp.setDirectoryId(0L);
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

                if (21 == ftp.getPort()) {
                    try {
                        FtpUtil.connectFtp(ftp);
                        FtpUtil.startDown(ftp, tempFold, directory);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    SFTPUtils sf = SFTPUtils.getInstance(ftp);
                    Vector<ChannelSftp.LsEntry> files = null;        //查看文件列表
                    try {
                        Long directoryId = ftp.getDirectoryId();

                        files = sf.listFiles(directory);
                        if (files != null && files.size() > 0) {
                            for (ChannelSftp.LsEntry lsEntry : files) {
                                /*Date currentDate = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");*/

                                //String relativePath = sdf.format(currentDate);
                                String relativePath = ipAddr;
                                Long dirId = directoryService.createRelativePath(directoryId, relativePath.split("/"));

                                String fileName = lsEntry.getFilename();
                                Map<String, Object> params = new HashMap<>();
                                params.put("name", fileName);
                                params.put("directoryId", dirId);

                                // 去重
                                int count = fileService.queryCount(params);
                                if (count > 0) {
                                    continue;
                                }
                                if (!fileName.equals(".") && !fileName.equals("..")) {
                                    if (!lsEntry.getAttrs().isDir()) {
                                        File download = sf.download(directory + "/" + lsEntry.getFilename(), tempFold + "/" + fileName);
//                                        File download = new File("d:\\zw_kzsx_sb.xml");
//                                        File download = new File("d:\\zw_kzsx_sb.xls");


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
                                            String suffix = fileName.toLowerCase().endsWith("tar.gz") ? "tar.gz" : fileName.indexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
                                            FastDfsInfo info = fastDFSTemplate.upload(buffer, suffix);
                                            if (info != null) {

//                                String relativePath = fileInfo.getWebkitRelativePath();


                                                //test
//                                if (!Strings.isNullOrEmpty(relativePath)) {
//                                }

                                                saveFile(info, fileName, suffix, dirId, null);

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
//                                        kafkaTemplate.send("operation_3rd3", jsonStr);
                                    }
                                }
                            }
                        }
                    } catch (SftpException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sf.disconnect();
                }

            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 0, 1800, TimeUnit.SECONDS);
    }


    /**
     * 上传分片文件
     *
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
     *
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
     *
     * @param chunk
     * @param response
     * @return
     */
    @GetMapping("/chunk")
    public Object checkChunk(Chunk chunk, HttpServletResponse response) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("fileMd5", chunk.getIdentifier());
        List<com.fms.domain.filemanage.File> files = fileService.query(params);
        if (CollectionUtil.isNotEmpty(files)) {
            return chunk;
        }
        Chunk oChunk = chunkService.query(chunk);
        if (oChunk == null) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
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
     *
     * @param fileInfo
     * @return
     */
    @PostMapping("/mergeFile")
    public String mergeFile(FileInfo fileInfo) {
        String uploadFolder = env.getProperty("file.tmpPath");
        String fileName = fileInfo.getFilename();
        String suffix = fileName.toLowerCase().endsWith("tar.gz") ? "tar.gz" : fileName.indexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = uploadFolder + "/" + fileInfo.getIdentifier() + "/" + fileInfo.getFilename();
        Map<String, Object> params = Maps.newHashMap();
        params.put("fileMd5", fileInfo.getIdentifier());
        List<com.fms.domain.filemanage.File> files = fileService.query(params);
        if (CollectionUtil.isNotEmpty(files)) {
            com.fms.domain.filemanage.File file = files.get(0);
            Long dirId = fileInfo.getDirectoryId();

            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String relativePath = sdf.format(currentDate) + "/" + fileInfo.getWebkitRelativePath();

            if (!Strings.isNullOrEmpty(relativePath)) {
                relativePath = relativePath.substring(0, relativePath.lastIndexOf("/"));
                dirId = directoryService.createRelativePath(dirId, relativePath.split("/"));
            }
            FastDfsInfo info = new FastDfsInfo(file.getGroups(), file.getRealPath());

            saveFile(info, fileName, suffix, dirId, fileInfo.getIdentifier());
            return "合并成功";
        }

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
                saveFile(info, fileName, suffix, dirId, fileInfo.getIdentifier());

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
     *
     * @param fileInfo
     * @return
     */
    @PostMapping("/mergeFileForJar")
    public String mergeFileForJar(FileInfo fileInfo) {

        String fileTmp = env.getProperty("parser.path") + "/";

        String name = fileInfo.getFilename();

        if (!name.endsWith(".jar")) {
            return "非jar文件不作处理";
        }

        try {
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

            Path pathPath = Paths.get(fileTmp + name);
            Files.write(pathPath, bytes);

            FileParserJar fileParserJar = new FileParserJar();
            fileParserJar.setName(name);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("name", name);
            List<FileParserJar> fileParserJarList = fileParserJarService.query(params);
            if (!CollectionUtil.isNotEmpty(fileParserJarList)) {
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
     *
     * @param info
     * @param fileName
     * @param suffix
     * @param dirId
     * @param fileMd5
     */
    public void saveFile(FastDfsInfo info, String fileName, String suffix, Long dirId, String fileMd5) {

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
        if (fileName.lastIndexOf(".") > 0) {
            fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("fileSuffix", "<" + fileSuffix + ">");
        List<FileType> fileTypeList = fileTypeService.getListBySuffix(params);

        if (fileTypeList.size() > 0) {
            file.setClassId(fileTypeList.get(0).getId());
            file.setClassName(fileTypeList.get(0).getName());
            file.setFatherClassName(fileTypeList.get(0).getType());

        }


        if (fileTypeList == null || fileTypeList.size() == 0) {
            file.setClassType("其他");
        } else {
            Map<String, Object> fileParserIds = new HashMap<String, Object>();
            int tCount = 0;
            for (FileType ftype : fileTypeList) {
                String fileParseId = ftype.getFileParserIds();
                if (!Strings.isNullOrEmpty(fileParseId)) {
                    String[] tFileParserIds = fileParseId.split(",");
                    for (String tFP : tFileParserIds) {
                        if (!fileParserIds.containsKey(tFP)) {
                            fileParserIds.put(tFP, tFP);
                            tCount++;
                        }
                    }
                }
            }
            if (tCount > 1) {
                file.setClassType("待分类");
            } else if (tCount == 1) {
                file.setClassType("预分类");
            } else {
                file.setClassType("其他");
            }

            String recommendParserId = "";
//            List<FileParser> fPs = new ArrayList<FileParser>();
            Set<String> filePK = fileParserIds.keySet();
            if (filePK.size() == 1) {
                Iterator<String> it = filePK.iterator();
                while (it.hasNext()) {
                    recommendParserId = it.next();
                    if (!Strings.isNullOrEmpty(recommendParserId)) {
                        file.setRecommendParserId(Long.valueOf(recommendParserId));
                        FileParser fileParser = fileParserService.get(file.getRecommendParserId());
                        if (fileParser != null) {
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
     *
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
    public String singleFileUpload(MultipartFile file, HttpServletResponse response) {

        String fileTmp = env.getProperty("parser.path") + "/";

        String name = file.getOriginalFilename();

        if (!name.endsWith(".jar")) {
            return "非jar文件不作处理";
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(fileTmp + name);
            Files.write(path, bytes);

            FileParserJar fileParserJar = new FileParserJar();
            fileParserJar.setName(name);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("name", name);
            List<FileParserJar> fileParserJarList = fileParserJarService.query(params);
            if (!CollectionUtil.isNotEmpty(fileParserJarList)) {
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

        if (21 == ftp.getPort()) {
            try {
                FtpUtil.connectFtp(ftp);
                FtpUtil.startDown(ftp, tempFold, directory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            SFTPUtils sf = SFTPUtils.getInstance(ftp);
            Vector<ChannelSftp.LsEntry> files = null;        //查看文件列表
            try {
                files = sf.listFiles(directory);
                if (files != null && files.size() > 0) {
                    for (ChannelSftp.LsEntry lsEntry : files) {
                        String fileName = lsEntry.getFilename();
                        if (!fileName.equals(".") && !fileName.equals("..")) {
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
                                    String suffix = fileName.toLowerCase().endsWith("tar.gz") ? "tar.gz" : fileName.indexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
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
                                        saveFile(info, fileName, suffix, dirId, null);

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

    /**
     * 进行全字段匹配并发送kafka
     *
     * @param file
     * @throws Exception
     */
    public void handleFile(File file) throws Exception {
        try {

            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            //JSONArray kafkaArray = new JSONArray();


            if (suffix.equals("xml")) {
                Map<String, String> map = new XmlParser().parseXml(file);
                String outPut = map.get("jsonBottomLevel");
                System.out.println("xml:"+outPut);
                getKafka(outPut);
/*
                JSONObject outPutJson = JSONObject.parseObject(outPut);
//                fileParserService.parseData(json2List())
                Iterator<String> it = outPutJson.keySet().iterator();

                if (it.hasNext()) {
                    String key = it.next();// entitys entity
                    JSONArray array = outPutJson.getJSONArray(key);//就是匹配字段的源数据

                    String tableName = getTable(array.toJSONString());


                    // JSONArray array = JSONArray.parseArray(tableStr);
                            *//*    String schema="";
                                if(jsonObject.containsKey("schema")){
                                    schema= jsonObject.getString("schema");
                                }
                                String table="";
                                if(jsonObject.containsKey("table")){
                                    table= jsonObject.getString("table");
                                }*//*


                    for (int i = 0; i < array.size(); i++) {
                        JSONObject obj = new JSONObject();
                        JSONArray data = new JSONArray();

                        JSONObject obj1 = new JSONObject();
                        obj1.put("operationType", "INSERT");
                        obj1.put("objectCode", "dxbm");
                        obj.put("operationSource", "XX_PLATFORM");
                     *//*       obj1.put("schema", schema);
                        obj1.put("table", "zw_kzsx_sb");
*//*
                        JSONArray columns = new JSONArray();

                        JSONObject jsonObject = array.getJSONObject(i);
                        Iterator<String> colIt = jsonObject.keySet().iterator();
                        while (colIt.hasNext()) {

                            String jsonKey = colIt.next();
                            if (jsonKey.equals("schema")) {
                                obj1.put("schema", jsonObject.get(jsonKey).toString());
                                continue;
                            }
                            if (jsonKey.equals("table")) {
                                obj1.put("table", tableName);
                                continue;
                            }
                            if (jsonKey.equals("dxbm")) {
                                obj1.put("objectCodeValue", jsonObject.get(jsonKey).toString());
                            }

                            JSONObject jsonCol = new JSONObject();
                            jsonCol.put("name", jsonKey.toLowerCase());
                            jsonCol.put("value", jsonObject.get(jsonKey));
                            columns.add(jsonCol);

                        }
                        obj1.put("columns", columns);
                        data.add(obj1);
                        obj.put("data", data);
                        System.out.println(obj.toJSONString());
                        kafkaTemplate.send("operation_3rd1", obj.toJSONString());
                    }

                }*/
            } else if (suffix.equals("json")) {

                Map<String, String> map = new JsonParser().parseJson(file);
                String outPut = map.get("jsonBottomLevel");
                System.out.println("json:"+outPut);

                getKafka(outPut);

            } else if (suffix.equals("sql")) {

                Map<String, String> map = new SqlParser().parseSql(file);
                String outPut = map.get("jsonBottomLevel");
                System.out.println("sql:"+outPut);

                getKafka(outPut);

            } else if (suffix.equals("xls") || suffix.equals("xlsx")) {
                Map<String, String> map = new ExcelParser().parseExcel(file, true); //true 表示是否行排列，false表示列排列，目前仅支持行排列即可
                String outPut = map.get("jsonBottomLevel");
                System.out.println("xls:"+outPut);
                getKafka(outPut);

        /*        JSONObject outPutJson = JSONObject.parseObject(outPut);
                Iterator<String> it = outPutJson.keySet().iterator();
                if (it.hasNext()) {
                    String key = it.next();// entitys entity
                    JSONArray array = outPutJson.getJSONArray(key);

                    String tableName = getTable(array.toJSONString());
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject obj = new JSONObject();
                        JSONArray data = new JSONArray();

                        JSONObject obj1 = new JSONObject();
                        obj1.put("operationType", "INSERT");
                        obj1.put("objectCode", "dxbm");
                        obj.put("operationSource", "XX_PLATFORM");
                                     *//*       obj1.put("schema", schema);
                                        obj1.put("table", "zw_kzsx_sb");
                *//*
                        JSONArray columns = new JSONArray();

                        JSONObject jsonObject = array.getJSONObject(i);
                        Iterator<String> colIt = jsonObject.keySet().iterator();
                        while (colIt.hasNext()) {

                            String jsonKey = colIt.next();
                            if (jsonKey.equals("schema")) {
                                obj1.put("schema", jsonObject.get(jsonKey).toString());
                                continue;
                            }
                            if (jsonKey.equals("table")) {
                                obj1.put("table", tableName);
                                continue;
                            }
                            if (jsonKey.equals("dxbm")) {
                                obj1.put("objectCodeValue", jsonObject.get(jsonKey).toString());
                            }

                            JSONObject jsonCol = new JSONObject();
                            jsonCol.put("name", jsonKey.toLowerCase());
                            jsonCol.put("value", jsonObject.get(jsonKey));
                            columns.add(jsonCol);

                        }
                        obj1.put("columns", columns);
                        data.add(obj1);
                        obj.put("data", data);
                        System.out.println(obj.toJSONString());
                        kafkaTemplate.send("operation_3rd1", obj.toJSONString());
                    }
                }*/
            }
        } catch (Exception e) {
        }
    }


    /**
     * @param str 解析后 key为jsonBottomLevel 的value值
     * @return
     */
    public String getTable(String str) {

        List<Map<String, Object>> json = new ArrayList<>();
        try {

            JSONArray arrayList = JSONArray.parseArray(str);

            //获取返回数据中jsonBottomLevel每个tab对应的数据
//            for (int i = 0; i < arrayList.size(); i++) {
//                JSONObject jsonObject = arrayList.getJSONObject(i);
//
//                Iterator<String> it = jsonObject.keySet().iterator();
//                if (it.hasNext()) {
//                    // 获得key
//                    String key = it.next();
//                    String value = jsonObject.getString(key);
            json.addAll(JSONUtils.jsonToObject(arrayList.toJSONString(), List.class, Map.class));

//                }

//            }
            //json=JSONUtils.jsonToObject(data.get("jsonBottomLevel"),List.class,Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> result = fileParserService.parseData(json);
        System.out.println(result.get("table_name").toString());
        return result.get("table_name").toString();
    }

    @RequestMapping(value = "/sendToFtp", method = RequestMethod.POST)
    public Object sendToFtp(HttpServletRequest request, HttpServletResponse response) {
        String ipAddr = request.getParameter("ipAddr");
        String port = request.getParameter("port");
        String userName = request.getParameter("userName");
        String pwd = request.getParameter("pwd");
        String path = request.getParameter("path");
        Ftp ftp = new Ftp();
        ftp.setUserName(userName);
        ftp.setPwd(pwd);
        ftp.setPort(Integer.parseInt(port));
        ftp.setPath(path);
        ftp.setIpAddr(ipAddr);

        // 获取admin下的所有文件   directoryId = 1
        Map<String, Object> params = new HashMap<String, Object>();
        List<Long> dIds = new ArrayList<Long>();
        dIds.add(Long.valueOf(1));
        params.put("ids", dIds);//
        //List<File> fileList = fileService.getFileListByDirectoryIds(params);
        List<com.fms.domain.filemanage.File> fileList = fileService.getAllFIles();// 写个查询所有的方法

        try {
            String filePath = "";
            String dir = env.getProperty("file.tmpPath");// /home/huiju
            int i = 1;

            SFTPUtils sf = SFTPUtils.getInstance(ftp);// 获取ftp连接

            for (com.fms.domain.filemanage.File file : fileList) {
                boolean isUpload = true;// 判断是否上报

                File fileLocal = null;
                if (file != null) {
                    try {
                        String fileName = file.getName();

                        // 指定存放位置(有需求可以自定义)
                        filePath = dir + File.separatorChar + fileName;
                        fileLocal = new File(filePath);
                        // 校验文件夹目录是否存在，不存在就创建一个目录
                        if (!fileLocal.getParentFile().exists()) {
                            fileLocal.getParentFile().mkdirs();
                        }
                        OutputStream out = new FileOutputStream(fileLocal);

                        byte[] buf = fastDFSTemplate.loadFile(file.getGroups(), file.getRealPath());

                        if(buf != null){
                            out.write(buf);
                            fileService.updateFileIsReportById(file.getId(),1);
                            System.out.println("第" + i + "个文件下载成功！--" + fileName);
                        }else{
                            isUpload = false;
                            System.out.println("第" + i + "个文件不存在，下载失败！--" + fileName);
                        }
                        i++;

                        out.flush();
                        out.close();


                    } catch (FastDFSException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //String filePath = "http://"+env.getProperty("fastdfs.nginxAddress")+"/"+file.getGroups()+"/"+file.getRealPath();
                System.out.println(filePath);
                if(isUpload){
                    if (21 == ftp.getPort()) {
                        try {
                            FtpUtil.connectFtp(ftp);

                            FtpUtil.uploadFTP(filePath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if(sf == null || ! SFTPUtils.isSFTPConnect(sf) ){// 判断SFTP是否连接中
                            sf = SFTPUtils.getInstance(ftp);// 断开或者是去连接null时，重新连接
                        }

                        sf.upload(path, filePath);
                    }
                }

                // 删除临时文件
                if(fileLocal != null){
                    fileLocal.delete();
                }
            }
            if(sf!=null){
                sf.disconnect();// 断开连接
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return ExtUtil.success("文件上传成功！");
    }


    private void getKafka(String outPut) {
        JSONObject outPutJson = JSONObject.parseObject(outPut);

        //
        String key = "";
        for (String keys : outPutJson.keySet()) {
            key = keys;
            break;
        }
        JSONArray array = outPutJson.getJSONArray(key);//就是匹配字段的源数据

        String tableName = getTable(array.toJSONString());// =======================================================================
        //String tableName = "sd_ddd_xxx";


        JSONObject rootObj = new JSONObject();
        rootObj.put("operationSource", "XX_PLATFORM");

        JSONArray infoArr = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            JSONObject infoObj = new JSONObject();
            infoObj.put("operationType", "INSERT");

            infoObj.put("schema", "renzhi_1208");//库名

            JSONObject columnPublic = new JSONObject();

            JSONArray columnArr = new JSONArray();

            infoObj.put("table", tableName);//表名

            JSONObject colJson = array.getJSONObject(i);

            String dxbm = "";
            if(colJson.containsKey("DXBM")){
                dxbm = colJson.getString("DXBM");
            }else if(colJson.containsKey("dxbm")){
                dxbm = colJson.getString("dxbm");
            }

            for (String colKey : colJson.keySet()) {
                if(! colKey.equals("TableSchema")){
                    JSONObject columnJson = new JSONObject();

                    columnJson.put("name", colKey.toLowerCase());// 获取
                    columnJson.put("value", colJson.getString(colKey));

                    columnArr.add(columnJson);
                }
            }

            columnArr.add(columnPublic);

            infoObj.put("objectCode", "dxbm");
            infoObj.put("objectCodeValue", dxbm);//
            infoObj.put("columns", columnArr);

            infoArr.add(infoObj);

            rootObj.put("data", infoArr);
        }
        System.out.println("kafka消息格式：\n" + rootObj);

        kafkaTemplate.send("operation_3rd1", rootObj.toJSONString());

    }


}