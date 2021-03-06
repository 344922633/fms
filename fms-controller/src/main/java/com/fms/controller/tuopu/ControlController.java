package com.fms.controller.tuopu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.fms.domain.tuopu.Control;
import com.fms.domain.tuopu.ControlProperty;
import com.fms.service.tuopu.ControlPropertyService;
import com.fms.service.tuopu.ControlService;
import com.fms.utils.MD5Util;
import com.handu.apollo.utils.ExtUtil;
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
import java.util.List;
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
        List<Control> list = controlService.getList(params);
        for (Control cl : list) {
            List<ControlProperty> cpList = controlService.queryPropertyById(cl.getId());
            cl.setProperties(cpList);
        }
        return list;
    }

    @RequestMapping("delete")
    public void delete(String id) {
        controlService.delete(id);
    }


    @Value("${fileUploadPath}")
    private String fileUploadPath;

    @RequestMapping(value = "/imgUpload")
    public Object imgUpload(HttpServletRequest req)
            throws IOException {
        JSONObject result = new JSONObject();

        // 检测是不是存在上传文件
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        //spring 要用

        try {
            MultipartHttpServletRequest multiReq = null;
            if (isMultipart) {
                multiReq = (MultipartHttpServletRequest) req;
            }
            String fileTmp = fileUploadPath + "/";
            System.out.println("---" + fileUploadPath);
            if (multiReq != null) {
                MultipartFile file = multiReq.getFile("file");
                String originalFilename = file.getOriginalFilename();
                String suffix = originalFilename.substring(originalFilename.indexOf("."));

                String localFileName = System.currentTimeMillis() + suffix;
                File localFile = new File(fileTmp + localFileName);
                if (!localFile.exists()) {
                    localFile.createNewFile();

                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(fileTmp + localFileName);
                    Files.write(path, bytes);

                    result.put("success", true);
                    result.put("url", "/fms/static/img/" + localFileName);
                    result.put("info", "上传成功！");

                } else {
                    result.put("success", false);
                    result.put("error", "上传失败， 文件已存在！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("error", "上传失败：" + e.getMessage());
        }
        System.out.println("---" + result);
        return result;
    }

    @RequestMapping("operationControl")
    public Object operationControl(@RequestParam(value = "name") String name, String type,String type1, String url, String properties, String id) {
        try {

        Control control = new Control();

        control.setImage(url);
        control.setType(type);
        control.setType1(type1);
        control.setName(name);
        if (id != null && name != null && properties != null) {
            //清空control_property数据
             controlPropertyService.delControlPropertyByControlId(id);
        }
        if (id != null && controlService.query(id)!=0){
            control.setId(id);
            controlService.update(control);
        }else{
            String controlId = String.valueOf(System.currentTimeMillis());
            control.setId(controlId);
            controlService.add(control);
        }

        JSONArray array = JSONArray.parseArray(properties);
        String property;
        for (int i = 0; i < array.size(); i++) {
            JSONObject propertyJson = array.getJSONObject(i);
            if (propertyJson.containsKey("column")) {
                JSONObject columnObj = propertyJson.getJSONObject("column");

                String data_type = propertyJson.getString("data_type");
                String dicList = propertyJson.getString("dicList");
                String isDic = columnObj.getString("isDic");
                String columnEnglish = columnObj.getString("columnEnglish");
                String columnChinese = columnObj.getString("columnChinese");
                String dicName = columnObj.getString("dicTableName");
                String tableId = columnObj.getString("tableId");
                ControlProperty controlProperty = new ControlProperty();
                controlProperty.setProperty(columnEnglish);
                controlProperty.setPropertyChinese(columnChinese);
                controlProperty.setIsDic(Integer.valueOf(isDic));
                controlProperty.setDataType(data_type);
                controlProperty.setDicName(dicName);
                controlProperty.setTableId(tableId);
                controlProperty.setDicList(dicList);
                controlProperty.setPropertyFlag(0);
                controlProperty.setControlId(control.getId());
                controlPropertyService.addControlProperty(controlProperty);
            } else {
                property = array.getJSONObject(i).getString("text");
                ControlProperty controlProperty = new ControlProperty();
                controlProperty.setControlId(control.getId());
                controlProperty.setProperty(property);
                controlProperty.setPropertyChinese(property);
                controlProperty.setPropertyFlag(1);
                controlPropertyService.addControlProperty(controlProperty);
            }
        }
            return ExtUtil.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ExtUtil.failure("系统出错了");
        }

    }

    @RequestMapping("/delControl")
    public Object delControl(String id) {
        try {
            //清空control_property数据
            controlPropertyService.delControlPropertyByControlId(id);
            //清空control数据
            controlService.delete(id);
            return ExtUtil.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ExtUtil.failure("系统出错了");
        }
    }
}
/*

    @RequestMapping("updateControl")
    public Object updateControl(@RequestParam(value="name") String name, String type, String url, String properties,String id) {
        if (name != null && properties != null) {
            //清空control_property数据
            controlPropertyService.delControlPropertyByControlId(id);
            //清空control数据
            controlService.delete(id);

            Control control = new Control();
            String controlId = String.valueOf(System.currentTimeMillis());
            control.setId(controlId);
            control.setImage(url);
            control.setType(type);
            control.setName(name);
            controlService.add(control);
            JSONArray array = JSONArray.parseArray(properties);
            String property;
            String columnInfo;
            for (int i = 0; i < array.size(); i++) {
                JSONObject propertyJson = array.getJSONObject(i);
                if (propertyJson.containsKey("column")) {
                    JSONObject columnObj = propertyJson.getJSONObject("column");

                    String data_type = propertyJson.getString("data_type");
                    String dicList = propertyJson.getString("dicList");
                    String isDic = columnObj.getString("isDic");
                    String columnEnglish = columnObj.getString("columnEnglish");
                    String columnChinese = columnObj.getString("columnChinese");
                    String dicName = columnObj.getString("dicTableName");
                    ControlProperty controlProperty = new ControlProperty();
                    controlProperty.setProperty(columnEnglish);
                    controlProperty.setPropertyChinese(columnChinese);
                    controlProperty.setIsDic(Integer.valueOf(isDic));
                    controlProperty.setDataType(data_type);
                    controlProperty.setDicName(dicName);
                    controlProperty.setDicList(dicList);
                    controlProperty.setPropertyFlag(0);
                    controlProperty.setControlId(controlId);
                    controlPropertyService.addControlProperty(controlProperty);
                } else {
                    property = array.getJSONObject(i).getString("text");
                    ControlProperty controlProperty = new ControlProperty();
                    controlProperty.setControlId(controlId);
                    controlProperty.setProperty(property);
                    controlProperty.setPropertyFlag(1);
                    controlPropertyService.addControlProperty(controlProperty);
                }
            }
            return ExtUtil.success("操作成功");
        } else {
            return ExtUtil.success("操作失败");
        }
    }
    }
*/
  /* @RequestMapping("/addControl")
    public Object addControl(Control control){
        try {
            //生成id
            String id = System.currentTimeMillis() + "";
            //添加Control数据
            control.setId(id);
            controlService.add(control);
            //添加control_property数据
            controlPropertyService.addControlPropertys(control.getProperties(),id);
            return ExtUtil.success("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return ExtUtil.failure("系统出错了");
        }

    }*/