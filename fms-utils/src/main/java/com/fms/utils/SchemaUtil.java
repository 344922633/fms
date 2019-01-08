package com.fms.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

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
        rootObj.put("operationSource", "XX_PLATFORM");
        JSONArray infoArr = new JSONArray();
        JSONObject infoObj = new JSONObject();
        infoObj.put("operationType", "INSERT");
        String tempCode=list.get(0).get("column_name").toString();
        String tempCode1=tempCode.toLowerCase();
        infoObj.put("objectCode", tempCode1);
        infoObj.put("objectCodeValue", list.get(0).get("value").toString());
        infoObj.put("schema",Constants.schema);//库名
        infoObj.put("table", tableName);//表名
        JSONArray columnArr = new JSONArray();
        JSONObject columnObj =null;
        for (int i = 0; i < list.size(); i++) {

            Object o=list.get(i).get("value");
            if(o!=null){
                columnObj = new JSONObject();
                String strTemp=list.get(i).get("column_name").toString();
                String strTemp1=strTemp.toLowerCase();
                columnObj.put("name", strTemp1);
                columnObj.put("value", list.get(i).get("value"));
                columnArr.add(columnObj);
            }

        }
//        columnArr.add(columnObj);
        infoObj.put("columns", columnArr);
        infoArr.add(infoObj);
        rootObj.put("data", infoArr);
//        System.out.print(rootObj.toJSONString());
        return rootObj.toJSONString();
    }

    public static JSONArray singleFileStrFormat(String tableName, JSONObject data,JSONObject customKey) {
        JSONArray rtArr=new JSONArray();
        Iterator<String> rootKeys = data.keySet().iterator();
        while (rootKeys.hasNext()) {

            String rootKey = rootKeys.next();
            //rootKey就是tabName
            JSONArray arrObj = data.getJSONArray(rootKey);
            for (int i = 0; i < arrObj.size(); i++) {
                String str="dwj_"+System.nanoTime();
                System.out.println("=================>" + str + "<=======================");

                JSONObject rootObj = new JSONObject();
                rootObj.put("operationSource", "XX_PLATFORM");
                JSONArray infoArr = new JSONArray();
                JSONObject infoObj = new JSONObject();
                infoObj.put("operationType", "INSERT");
                infoObj.put("objectCode", "dxbm");
                infoObj.put("objectCodeValue", str);
                infoObj.put("schema", Constants.schema);//库名
                infoObj.put("table", tableName);//表名
                JSONArray columnArr = new JSONArray();
                JSONObject columnObj =null;
                JSONObject columnObj1 =new JSONObject();

                Iterator<String> cls = arrObj.getJSONObject(i).keySet().iterator();
                while (cls.hasNext()){
                    columnObj = new JSONObject();
                    String strTemp0=cls.next();
                    String strTemp=strTemp0.toLowerCase();//key
                    System.out.println(customKey);
                    String strTemp1=customKey.getString(strTemp);
                    columnObj.put("name", strTemp1);
                    columnObj.put("value", arrObj.getJSONObject(i).getString(strTemp0));
                    columnArr.add(columnObj);
                }
                    columnObj1.put("name", "dxbm");
                    columnObj1.put("value", str);
                    columnArr.add(columnObj1);


                infoObj.put("columns", columnArr);
                infoArr.add(infoObj);
                rootObj.put("data", infoArr);

                rtArr.add(rootObj);
                System.out.println(rtArr);
            }

        }


        return rtArr;
    }


}
