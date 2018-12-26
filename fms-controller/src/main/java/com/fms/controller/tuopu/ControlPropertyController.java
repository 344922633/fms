package com.fms.controller.tuopu;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.fms.domain.tuopu.ControlProperty;
import com.fms.service.tuopu.ControlPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
public class ControlPropertyController {
    @Autowired
    ControlPropertyService controlPropertyService;

    @RequestMapping("getControlPropertyList")
    public Object getControlPropertyList(Map<String, Object> params) {
        List<ControlProperty> list = controlPropertyService.getControlPropertyList(params);

        Set<String> idSet = new HashSet<String>();// 控件id集合
        for (int i = 0; i < list.size(); i++) {
            idSet.add(list.get(i).getControlId());
        }

        JSONArray controlArray = new JSONArray();// 控件数组

        for (String controlId : idSet) {
            JSONObject propertyObj = new JSONObject();// 控件信息

            JSONArray propertyArray = new JSONArray();// 属性数组
            for (int i = 0; i < list.size(); i++) {
                ControlProperty property = list.get(i);
                if (controlId.equals(property.getControlId())) {
                    JSONObject obj = new JSONObject();// 属性信息
                    obj.put("id", property.getId());
                    obj.put("isDic", property.getIsDic());
                    obj.put("dicName", property.getDicName());
                    obj.put("propertyChinese", property.getPropertyChinese());
                    obj.put("property", property.getProperty());
                    obj.put("propertyFlag", property.getPropertyFlag());
                    obj.put("dataType",property.getDataType());
                    obj.put("dicList",property.getDicList());
                    propertyArray.add(obj);
                }
            }
            propertyObj.put("controlId", controlId);// 控件ID
            propertyObj.put("properties", propertyArray);// 控件属性数组

            controlArray.add(propertyObj);
        }

        return controlArray;
    }

}

