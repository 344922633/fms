package com.fms.controller.filemanage;

import com.anniweiya.fastdfs.FastDFSTemplate;
import com.anniweiya.fastdfs.FastDfsInfo;
import com.anniweiya.fastdfs.exception.FastDFSException;
import com.fms.service.filemanage.DirectoryService;
import com.fms.service.filemanage.FileService;
import com.fms.utils.ParamUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.handu.apollo.utils.ExtUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

@RestController
@Deprecated
public class ChunkedUploadController {
    private String serverPath = "/Users/fencho/Documents/tmp/";
    @Autowired
    private FastDFSTemplate fastDFSTemplate;

    @Autowired
    private FileService fileService;
    @Autowired
    private DirectoryService directoryService;

    @RequestMapping("chunkUpload")
    public Object chunkUpload(HttpServletRequest request) {

        // 1.创建DiskFileItemFactory对象，配置缓存用
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

        // 2. 创建 ServletFileUpload对象
        ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

        // 3. 设置文件名称编码
        servletFileUpload.setHeaderEncoding("utf-8");

        // 4. 开始解析文件
        // 文件md5获取的字符串
        String fileMd5 = null;
        // 文件的索引
        String chunk = null;

        String fileName = null;
        try {
            List<FileItem> items = servletFileUpload.parseRequest(request);
            for (FileItem fileItem : items) {

                if (fileItem.isFormField()) { // >> 普通数据
                    String fieldName = fileItem.getFieldName();
                    if ("info".equals(fieldName)) {
                        String info = fileItem.getString("utf-8");
                        System.out.println("info:" + info);
                    }
                    if ("fileMd5".equals(fieldName)) {
                        fileMd5 = fileItem.getString("utf-8");
                        System.out.println("fileMd5:" + fileMd5);
                    }
                    if ("chunk".equals(fieldName)) {
                        chunk = fileItem.getString("utf-8");
                        System.out.println("chunk:" + chunk);
                    }
                    if ("name".equals(fieldName)) {
                        fileName = fileItem.getString("utf-8");
                    }
                } else { // >> 文件
					/*// 1. 获取文件名称
					String name = fileItem.getName();
					// 2. 获取文件的实际内容
					InputStream is = fileItem.getInputStream();

					// 3. 保存文件
					FileUtils.copyInputStreamToFile(is, new File(serverPath + "/" + name));*/

                    // 如果文件夹没有创建文件夹
                    File dir = new File(serverPath + "/" + fileMd5);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    // 保存文件
                    File chunkFile = null;
                    if (chunk == null) {
                        chunkFile = new File(serverPath + "/" + fileMd5 + "/" + fileName);
                    } else {
                        chunkFile = new File(serverPath + "/" + fileMd5 + "/" + chunk);
                    }
                    FileUtils.copyInputStreamToFile(fileItem.getInputStream(), chunkFile);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ExtUtil.success("分片上传成功！");
    }


    @RequestMapping("mergeChunks")
    public Object mergeChunks(HttpServletRequest request) {
        // 获得需要合并的目录
        String fileMd5 = ParamUtil.get(request, "fileMd5", null);
        String fileName = ParamUtil.get(request, "fileName", null);

        File outputFile = new File(serverPath + "/" + fileMd5 + "/" + fileName);
        if (!outputFile.exists()) {
            // 读取目录所有文件
            File f = new File(serverPath + "/" + fileMd5);
            File[] fileArray = f.listFiles(new FileFilter() {

                // 排除目录，只要文件
                @Override
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        return false;
                    }
                    return true;
                }
            });

            // 转成集合，便于排序
            List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
            // 从小到大排序
            Collections.sort(fileList, new Comparator<File>() {

                @Override
                public int compare(File o1, File o2) {
                    if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                        return -1;
                    }
                    return 1;
                }

            });

            // 新建保存文件
//        File outputFile = new File(serverPath + "/" + UUID.randomUUID().toString() + ".zip");


            // 创建文件
            try {
                outputFile.createNewFile();

                // 输出流
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                FileChannel outChannel = fileOutputStream.getChannel();
                // 合并
                FileChannel inChannel;
                for (File file : fileList) {
                    inChannel = new FileInputStream(file).getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    inChannel.close();

                    // 删除分片
                    file.delete();
                }
                // 关闭流
                fileOutputStream.close();
                outChannel.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileInputStream fis = null;
            ByteArrayOutputStream bos = null;
            byte[] buffer = null;
            fis = new FileInputStream(outputFile);
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
                Long dirId = ParamUtil.get(request, "directoryId", 0L);
                String relativePath = ParamUtil.get(request, "relativePath", null);
                if (!Strings.isNullOrEmpty(relativePath)) {
                    relativePath = relativePath.substring(0, relativePath.indexOf("/"));
                    dirId = directoryService.createRelativePath(dirId, relativePath.split(relativePath));
                }
                com.fms.domain.filemanage.File file = new com.fms.domain.filemanage.File();
                file.setId(System.currentTimeMillis());
                file.setName(fileName);
                file.setRealPath(info.getPath());
                file.setGroups(info.getGroup());
                file.setType(suffix);
                file.setDirectoryId(dirId);
                fileService.add(file);
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                // 清除文件夹
                File tempFile = new File(serverPath + "/" + fileMd5);
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


        return ExtUtil.success("文件合并成功！");
    }

    @RequestMapping("checkChunk")
    public Object checkChunk(HttpServletRequest request, HttpServletResponse response) {
        // 校验文件是否已经上传并返回结果给前端

        // 文件唯一表示
        String fileMd5 = request.getParameter("fileMd5");
        // 当前分块下标
        String chunk = request.getParameter("chunk");
        // 当前分块大小
        String chunkSize = request.getParameter("chunkSize");

        // 找到分块文件
        File checkFile = new File(serverPath + "/" + fileMd5 + "/" + chunk);

        // 检查文件是否存在，且大小一致
        response.setContentType("text/html;charset=utf-8");
        Map<String, Object> params = Maps.newHashMap();
        if (checkFile.exists() && checkFile.length() == Integer.parseInt((chunkSize))) {
            params.put("ifExist", 1);
//            response.getWriter().write("{\"ifExist\":1}");

        } else {
            params.put("ifExist", 0);
//            response.getWriter().write("{\"ifExist\":0}");
        }
        return params;
    }
}
