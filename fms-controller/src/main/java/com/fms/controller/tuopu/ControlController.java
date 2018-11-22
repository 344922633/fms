package com.fms.controller.tuopu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.fms.domain.tuopu.Control;
import com.fms.domain.tuopu.ControlProperty;
import com.fms.service.tuopu.ControlPropertyService;
import com.fms.service.tuopu.ControlService;
import com.fms.utils.MD5Util;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/control")
public class ControlController {
    @Autowired
    private ControlService controlService;
    @Autowired
    private ControlPropertyService controlPropertyService;

    @Autowired
    private Environment env;

    @RequestMapping("getList")
    public Object getList(Map<String, Object> params) {
        return controlService.getList(params);
    }

  /*  @RequestMapping("add")
    public void add(Control control) {
        switch(control.getType()){
            case "网络":
                control.setRemark("net");
                break;
            case "硬件":
                control.setRemark("hardware");
                break;
            case "区块":
                control.setRemark("block");
                break;
        }
        controlService.add(control);
    }*/


   /* *//**
     * 上传控件
     * @param
     * @return
     *//*
    @RequestMapping("uploadControl")
    public Object uploadControl(HttpServletRequest req, MultipartHttpServletRequest multiReq)
    throws IOException {
        MultipartFile file = multiReq.getFile("file");
        String parserPath = env.getProperty("parser.path");
        Path filePath = null;
        try {
            Path dir = Paths.get(parserPath);
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                Files.createDirectories(dir);
            }

            String fileName = file.getOriginalFilename();
            filePath = Paths.get(parserPath + "/" + fileName);
            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                Files.createFile(filePath);
            } else {
                return ExtUtil.failure("该控件已经存在！");
            }
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ExtUtil.success(filePath.toString());
    }
*/

    @Value("${fileUploadPath}")
    private String fileUploadPath;

    @RequestMapping(value = "/imgUpload")
    public Object imgUpload(HttpServletRequest req)
            throws IOException {
        JSONObject result = new JSONObject();

        // 检测是不是存在上传文件
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        //spring 要用

        try{
            MultipartHttpServletRequest multiReq = null;
            if (isMultipart) {
                multiReq = (MultipartHttpServletRequest) req;
            }
            String fileTmp = fileUploadPath + "/";
            System.out.println("---" + fileUploadPath);
            if(multiReq != null){
                MultipartFile file = multiReq.getFile("file");
                String originalFilename = file.getOriginalFilename();
                String suffix = originalFilename.substring(originalFilename.indexOf("."));

                String localFileName = System.currentTimeMillis() + suffix;
                File localFile = new File(fileTmp +  localFileName);
                if (!localFile.exists()) {
                    localFile.createNewFile();

                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(fileTmp + localFileName);
                    Files.write(path, bytes);

                    result.put("success", true);
                    result.put("url", "/static/img/" +  localFileName);
                    result.put("info", "上传成功！");
                /*

                FileOutputStream fos = new FileOutputStream(
                        localFile);
                FileInputStream fs = (FileInputStream) multiReq.getFile("img").getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = fs.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                fs.close();*/

                } else {
                    result.put("success", false);
                    result.put("error", "上传失败， 文件已存在！");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("success", false);
            result.put("error", "上传失败：" + e.getMessage());
        }
        System.out.println("---" + result);
        return result;
    }

    @RequestMapping("add")
    public void add(@RequestParam String name, @RequestParam String type, @RequestParam String url, @RequestParam String properties) {
        Control control = new Control();
        String controlId=UUID.randomUUID().toString();
        control.setId(controlId);
        control.setImage(url);
        control.setType(type);
        control.setName(name);
        controlService.add(control);
        JSONArray array = JSONArray.parseArray(properties);
        String property;
        for (int i = 0; i < array.size(); i++) {
            property = array.getJSONObject(i).getString("text");
            ControlProperty controlProperty=new ControlProperty();
            controlProperty.setControlId(controlId);
            controlProperty.setProperty(property);
            controlPropertyService.add(controlProperty);
        }

    }
}
