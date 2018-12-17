package com.fms.controller.columnSet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fms.domain.columnSet.ColumnDic;
import com.fms.domain.columnSet.ColumnInfo;
import com.fms.domain.columnSet.ColumnMapRelation;
import com.fms.domain.columnSet.TableInfo;
import com.fms.service.columnSet.ColumnSetService;
import com.handu.apollo.utils.ExtUtil;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author lib 2018/12/13
 */
@RestController
public class ColumnSetController {

    @Autowired
    private ColumnSetService columnSetService;
    @RequestMapping("/saveColumnMapInfos")
    public Object saveColumnMapInfos(String columnKeyNamesMap,String parserId) {
        JSONObject json = JSONObject.parseObject(columnKeyNamesMap);
        // 保存第一张表
        for(String key : json.keySet()) {

            if(StringUtils.isNotEmpty(json.getJSONObject(key).getString("columnId"))){
                ColumnMapRelation mr = new ColumnMapRelation();
                String columnMapId = UUID.randomUUID().toString();
                mr.setId(columnMapId);
                mr.setColumnKey(key);
                mr.setColumnId(json.getJSONObject(key).getInteger("columnId"));
                mr.setSchemaId(json.getJSONObject(key).getInteger("schemaId"));
                mr.setTableId(json.getJSONObject(key).getInteger("tableId"));
                mr.setParserId(Long.parseLong(parserId));
                columnSetService.insertColumnMapRelation(mr);

                // 保存dicName表
                if (json.getJSONObject(key).containsKey("dicMap")) {
                    JSONObject columnDicJson = json.getJSONObject(key).getJSONObject("dicMap");
                    for (String dicName : columnDicJson.keySet()) {
                        ColumnDic cd = new ColumnDic();
                        cd.setColumnMapId(columnMapId);
                        cd.setDicName(dicName);
                        cd.setDicValue(columnDicJson.getString(dicName));
                        columnSetService.insertColumnDic(cd);
                    }
                }
            }

        }
        return ExtUtil.success("保存成功！");
    }

    @RequestMapping("/getDicColumnsByDicName")
    public List<Map<String,Object>> getDicColumnsByDicName(String dicName) {
        return columnSetService.getDicColumnsByDicName(dicName);
    }

    @RequestMapping("/getDicNameByTableId")
    public JSONArray getDicNameByTableId(String tableId){
        int tid=Integer.valueOf(tableId);
        List<ColumnInfo> list = columnSetService.getDicNameByTableId(tid);

        JSONArray array = new JSONArray();
        // 循环获取字段 字典表列名
        for (ColumnInfo ci : list) {
            JSONObject obj = (JSONObject) JSONObject.toJSON(ci);
            if (StringUtils.isNotEmpty(ci.getDicTableName())) {
                List<Map<String, Object>> dicList = columnSetService.getDicColumnsByDicName(ci.getDicTableName());
                obj.put("dicList", dicList);

                array.add(obj);
            }
        }
        return array;
    }

    @RequestMapping("/getColumnsForTable")
    public List<ColumnInfo> getColumnsForTable(String tableId){
        int tid=Integer.valueOf(tableId);
        return columnSetService.getColumnsForTable(tid);
    }

    @RequestMapping("/getTablesBySchemaId")
    public List<TableInfo> getTablesBySchemaId(String schemaId){
        int sid=Integer.valueOf(schemaId);
        return columnSetService.getTablesBySchemaId(sid);
    }
	@RequestMapping("/getAllSchemas")
    public Object getAllSchemas(Map<String, Object> params) {
        return columnSetService.getAllSchemas(params);
    }

}


