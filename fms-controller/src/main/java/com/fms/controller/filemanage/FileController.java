package com.fms.controller.filemanage;

import com.anniweiya.fastdfs.FastDFSTemplate;
import com.anniweiya.fastdfs.exception.FastDFSException;
import com.fms.domain.filemanage.File;
import com.fms.domain.filemanage.FileParser;
import com.fms.domain.filemanage.FileType;
import com.fms.service.filemanage.FileParserService;
import com.fms.service.filemanage.FileService;
import com.fms.service.filemanage.FileTypeService;
import com.fms.utils.ParamUtil;
import com.google.common.base.Strings;
import com.handu.apollo.base.Page;
import com.handu.apollo.utils.ExtUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.tools.jconsole.HTMLPane;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class FileController {
    @Autowired
    private FileService fileService;
    @Autowired
    private FastDFSTemplate fastDFSTemplate;
    @Autowired
    private FileTypeService fileTypeService;
    @Autowired
    private FileParserService fileParserService;

    /**
     * 分页条件获取文件列表
     * @param params
     * @param request
     * @return
     */
    @RequestMapping("pageFiles")
    public Object pageFiles(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Page page = ParamUtil.getPager(request);
        return fileService.page(params, page);
    }
    @RequestMapping("pageFilesByIsParser")
    public Object pageFilesByIsParser(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Page page = ParamUtil.getPager(request);
        return fileService.pageFileByIsParserAndRemoveRepeat(params, page);
    }

    /**
     * 条件获取文件列表
     * @param params
     * @return
     */
    @RequestMapping("getFileList")
    public Object getFileList(@RequestParam Map<String, Object> params) {
        return fileService.query(params);
    }

    /**
     * 删除文件
     * @param params
     * @return
     */
    @RequestMapping("deleteFile")
    public Object deleteFile( @RequestParam Map<String, Object> params) {
       String[] ids = null;
        if(params.containsKey("ids")){
           String sids = (String)params.get("ids");
           if(!Strings.isNullOrEmpty(sids)){
               sids=sids.substring(1);
           }
           ids = sids.split(",");
       }

        for(String stid : ids){
            Long id = Long.parseLong(stid);
            File file = fileService.get(id);
            if (file == null) {
                return ExtUtil.failure("文件不存在！");
            }
            try {
                fastDFSTemplate.deleteFile(file.getGroups(), file.getRealPath());
                fileService.delete(id);
            } catch (FastDFSException e) {
                if (e.getCause() instanceof NullPointerException) {
                    fileService.delete(id);
                }
                e.printStackTrace();
            } catch (NullPointerException e) {
                fileService.delete(id);
            }
        }


        return ExtUtil.success("删除成功！");
    }

    /**
     * 下载文件
     * @param id
     * @param response
     */
    @RequestMapping("downloadFile")
    public void downloadFile(Long id, HttpServletResponse response) {
        File file = fileService.get(id);
        if (file != null) {
            try {
                byte[] buf = fastDFSTemplate.loadFile(file.getGroups(), file.getRealPath());
                // 设置response的Header
                String fileName = file.getName();
//                String fileName = file.getRealPath().substring(file.getRealPath().lastIndexOf("/")+1);
//                fileName=System.currentTimeMillis()+file.getRealPath().substring(file.getRealPath().lastIndexOf("."));
//                fileName=fileparser.getName().substring(0,fileparser.getName().lastIndexOf("."))+fileparser.getRealPath().substring(fileparser.getRealPath().lastIndexOf("."));

                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                response.addHeader("Content-Length", "" + buf.length);
                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream;charset=utf-8");
                toClient.write(buf);
                toClient.flush();
                toClient.close();
            } catch (FastDFSException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改文件分类
     * @param file
     * @return
     */
    @RequestMapping("updateFile")
    public Object updateFile(@ModelAttribute File file) {

        Long fileClassId = file.getClassId();
        Long uRecommendParserId = file.getRecommendParserId();
        FileType fileType = fileTypeService.getById(fileClassId);
        if(fileType!=null){
            file.setClassType("预分类");
            file.setClassId(fileType.getId());
            file.setClassName(fileType.getName());
            file.setFatherClassName(fileType.getType());

//            if(uRecommendParserId==null||uRecommendParserId==0L){
                String recommendParserIds = fileType.getFileParserIds();
                String recommendParserId = "";
                if(!Strings.isNullOrEmpty(recommendParserIds)){
                    if(recommendParserIds.indexOf(",")>0){
                        recommendParserId = recommendParserIds.substring(0,recommendParserIds.indexOf(","));
                    }else{
                        recommendParserId = recommendParserIds;
                    }
                }

                if(!Strings.isNullOrEmpty(recommendParserId)){
                    file.setRecommendParserId(Long.valueOf(recommendParserId));
                    FileParser fileParser = fileParserService.get(file.getRecommendParserId());
                    if(fileParser!=null){
                        file.setRecommendParserName(fileParser.getName());
                    }

                }
//            }else{
//                FileParser fileParser= fileParserService.get(fileparser.getRecommendParserId());
//                fileparser.setRecommendParserName(fileParser.getName());
//            }


        }else if(uRecommendParserId!=null){
            FileParser fileParser= fileParserService.get(file.getRecommendParserId());
            if(fileParser!=null){
                file.setRecommendParserName(fileParser.getName());
            }

        }
        fileService.update(file);
        return ExtUtil.success("文件修改成功！");
    }

    /**
     * 修改文件的所属文件夹
     * @param params
     * @return
     */
    @RequestMapping("updateFileDirectoryId")
    public Object updateFileDirectoryId(@RequestParam Map<String, Object> params) {
        String[] ids = null;
        Long  directoryId;
        if(params.containsKey("ids")){
            String sids = (String)params.get("ids");
            if(!Strings.isNullOrEmpty(sids)){
                sids=sids.substring(1);
            }
            ids = sids.split(",");
        }else{
            return ExtUtil.failure("请选择要移动到的文件！");
        }
        if(params.containsKey("directoryId")){
            directoryId = Long.parseLong((String)params.get("directoryId"));
        }else{
            return ExtUtil.failure("请选择要移动到的目录！");
        }

        for(String stid : ids){
            Long id = Long.parseLong(stid);
            File file = fileService.get(id);
            if(file!=null){
                file.setDirectoryId(directoryId);
            }
            fileService.update(file);
        }
        return ExtUtil.success("文件修改成功！");
    }

    /**
     *
     * @param request
     * @return
     */
    @RequestMapping("uploadFile")
    public Object uploadFile(HttpServletRequest request) {
        // 1.创建DiskFileItemFactory对象，配置缓存用
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

        // 2. 创建 ServletFileUpload对象
        ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

        // 3. 设置文件名称编码
//        servletFileUpload.setHeaderEncoding("utf-8");
        try {
            List<FileItem> items = servletFileUpload.parseRequest(request);
            for (FileItem fileItem : items) {
                if (fileItem.isFormField()) {
                    System.out.println(fileItem.getFieldName() + ":" + fileItem.getString("utf-8"));
                } else {
                    System.out.println(fileItem.getFieldName());
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "123";
    }
    @RequestMapping("getFileStatistic")
    public Object getFileStatistic() {
        return fileService.getFileStatistic();
    }

    @RequestMapping("getFileSuffixStatistic")
    public Object getFileSuffixStatistic() {
        return fileService.getFileSuffixStatistic();
    }
    @RequestMapping("jsonToFile")
    public Object jsonToFile(String json, HttpServletResponse response) {
        /*String json = "{\n" +
                "\t\"contactList\":{\n" +
                "\t\t\"contact\":[\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"id\":\"001\",\n" +
                "\t\t\t\t\"class\":\"style\",\n" +
                "\t\t\t\t\"name\":\"Alan\",\n" +
                "\t\t\t\t\"age\":20,\n" +
                "\t\t\t\t\"phone\":134222223333,\n" +
                "\t\t\t\t\"email\":\"zhangsan@qq.com\",\n" +
                "\t\t\t\t\"qq\":432221111\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"id\":\"002\",\n" +
                "\t\t\t\t\"name\":\"Mary\",\n" +
                "\t\t\t\t\"age\":20,\n" +
                "\t\t\t\t\"phone\":134222225555,\n" +
                "\t\t\t\t\"email\":\"lisi@qq.com\",\n" +
                "\t\t\t\t\"qq\":432222222\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\t\"contactTwo\":{\n" +
                "\t\t\t\"name\":\"Mike\",\n" +
                "\t\t\t\"age\":32,\n" +
                "\t\t\t\"phone\":465431341,\n" +
                "\t\t\t\"emali\":\"af@qq.com\",\n" +
                "\t\t\t\"qq\":46164694\n" +
                "\t\t},\n" +
                "\t\t\"test\":[\n" +
                "\t\t\t\"测试\",\n" +
                "\t\t\t\"其他用途\"\n" +
                "\t\t]\n" +
                "\t}\n" +
                "}";*/
        try {
            byte[] buf = json.getBytes();
            response.addHeader("Content-Disposition", "attachment;filename=" + System.currentTimeMillis() + ".json");
            response.addHeader("Content-Length", "" + buf.length);
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buf);
            toClient.flush();
            toClient.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

}
