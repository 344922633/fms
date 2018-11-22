package com.fms.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SchemaUtil {

    /**
     * 格式化成kafka要接收的数据
     *
     * @param tableName 表名
     * @param list      数据
     * @return
     */
    public static String schemaStrFormat(String tableName, List<Map<String, Object>> list) {

        JSONObject rootObj = new JSONObject();
        rootObj.put("operationSource", "HuiJu_PLATFORM");
        JSONArray infoArr = new JSONArray();
        JSONObject infoObj = new JSONObject();
        infoObj.put("operationType", "INSERT");
        infoObj.put("objectCode", list.get(0).get("column_name"));
        infoObj.put("objectCodeValue", list.get(0).get("value"));
        infoObj.put("schema", "wltsfx_analysis");//库名
        infoObj.put("table", tableName);//表名
        JSONArray columnArr = new JSONArray();
        JSONObject columnObj = new JSONObject();
        for (int i = 0; i < list.size(); i++) {
            columnObj.put((String) list.get(i).get("column_name"), list.get(i).get("value"));
        }
        columnArr.add(columnObj);
        infoObj.put("columns", columnArr);
        infoArr.add(infoObj);
        rootObj.put("data", infoArr);
        return rootObj.toJSONString();
    }
}
