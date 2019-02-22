package com.fms.controller.schema;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fms.domain.schema.ColumnInfo;
import com.fms.domain.schema.TableInfo;
import com.fms.service.schema.ColumnInfoService;
import com.fms.service.schema.SchemaService;
import com.handu.apollo.utils.ExtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ColumnInfoController {
    @Autowired
    private ColumnInfoService columnInfoService;
    @Autowired
    private SchemaService schemaService;

    @RequestMapping("getDicTable")
    public List<String> getDicTable() {
        return columnInfoService.getDicTable();
    }

    @RequestMapping("getXxDicTable")
    public List<String> getXxDicTable() {
        return columnInfoService.getXxDicTable();
    }

    @RequestMapping("/deleteTableInfo")
    public Object deleteTableInfo(String id) {
        long tid = Long.parseLong(id);
        columnInfoService.deleteTableInfo(tid);
        columnInfoService.deleteColumnInfo(tid);
        return ExtUtil.success("操作成功");
    }

    @RequestMapping("/updateTableInfo")
    public Object updateTableInfo(HttpServletRequest request) {
        String tableId = request.getParameter("tableId");
        long tid = Long.parseLong(tableId);
        String tableEnglish = request.getParameter("tableEnglish");
        String tableChinese = request.getParameter("tableChinese");

        TableInfo tableInfo = columnInfoService.queryTableInfoById(tid);
        tableInfo.setTableChinese(tableChinese);
        tableInfo.setTableEnglish(tableEnglish);
        columnInfoService.updateTableInfo(tableInfo);
        columnInfoService.deleteColumnInfo(tid);
        // 字段列表
        List<Map<String, Object>> tableColumns = schemaService.listColumns(tableEnglish);

        for (Map<String, Object> map : tableColumns) {
            ColumnInfo columnInfo = new ColumnInfo();

            columnInfo.setColumnEnglish((String) map.get("column_name"));
            columnInfo.setDataType((String) map.get("data_type"));
            columnInfo.setTableId(tid);
            columnInfoService.addColumnInfo(columnInfo);
        }

        return ExtUtil.success("操作成功");
    }


    @RequestMapping("/addTableInfo")
    public Object addTableInfo(TableInfo tableInfo) {
        //根据tableEnglish查询表是否存在
        Integer count = columnInfoService.countTableInfoByEnglish(tableInfo.getTableEnglish());

        if (count > 0) {
            return ExtUtil.failure("操作失败,当前表已存在");
        }

        long id = System.currentTimeMillis();
        tableInfo.setId(id);
        columnInfoService.addTableInfo(tableInfo);

        // 字段列表
        List<Map<String, Object>> tableColumns = schemaService.listColumns(tableInfo.getTableEnglish());

        for (Map<String, Object> map : tableColumns) {
            ColumnInfo columnInfo = new ColumnInfo();

            columnInfo.setColumnEnglish((String) map.get("column_name"));
            columnInfo.setDataType((String) map.get("data_type"));
            columnInfo.setTableId(id);

            columnInfoService.addColumnInfo(columnInfo);
        }

        return ExtUtil.success("操作成功");
    }

    @RequestMapping("getColumnsInfo")
    public Object getColumnsInfo(long id) {
        return columnInfoService.getColumnsInfo(id);
    }

    @RequestMapping("getAllTables")
    public List<TableInfo> getAllTables() {
        return columnInfoService.getAllTables();
    }

    @RequestMapping("saveColumnInfos")
    public Object saveColumnInfos(HttpServletRequest request) {
        try {
            List<ColumnInfo> columnInfos = new ArrayList<ColumnInfo>();
            String data = request.getParameter("data");
            String tableId = request.getParameter("tableId");
            String tableEnglish = request.getParameter("tableEnglish");

            JSONArray btnArray = JSONArray.parseArray(data);
            for (int i = 0; i < btnArray.size(); i++) {
                ColumnInfo columnInfo = new ColumnInfo();
                JSONObject obj = btnArray.getJSONObject(i);
                String dicName = obj.getString("dicTableName");
                String columnEnglish = obj.getString("columnEnglish");
                String columnChinese = obj.getString("columnChinese");
                Boolean isDicBoolean = obj.getBoolean("isDic");
                Boolean isMasterKeyBoolean = obj.getBoolean("isMasterKey");

                int isDic;
                int isMasterKey;

                if (isDicBoolean) {
                    isDic = 1;
                } else {
                    isDic = 0;
                }
                if (isMasterKeyBoolean) {
                    isMasterKey = 1;
                } else {
                    isMasterKey = 0;
                }
                String dataType = obj.getString("data_type");
//                获取字段类型并插入
                List<String> types = getColumnsInfoDataType(tableEnglish);


                for (int j = 0; j < types.size(); j++) {

                    if (j == i) {
                        dataType = types.get(j);
                    break;
                    }

                }
                columnInfo.setDicTableName(dicName);
                columnInfo.setColumnChinese(columnChinese);
                columnInfo.setColumnEnglish(columnEnglish);
                columnInfo.setSchemaId(1);
                columnInfo.setTableId(Long.parseLong(tableId));
                columnInfo.setDataType(dataType);
                columnInfo.setIsMasterKey(isMasterKey);
                columnInfo.setIsDic(isDic);
                System.out.println(columnInfo);
                columnInfos.add(columnInfo);
            }
            saveColumnInfo(columnInfos, tableId);
            return ExtUtil.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ExtUtil.failure(e.getMessage());
        }

    }
    @RequestMapping("getColumnsInfoDataType")
    public List<String> getColumnsInfoDataType(String tableEnglish) {
        return columnInfoService.getColumnsInfoDataType(tableEnglish);
    }


    @RequestMapping("getDicByTableId")
    public List<String> getDicByTableId(long id) {
        return columnInfoService.getDicByTableId(id);
    }

/*    @RequestMapping("getDicInfoByName")
    public List<String> getDicInfoByName(String dicName) {
        return columnInfoService.getDicInfoByName(dicName);
    }*/


    @RequestMapping("isTableIdExist")
    public boolean isTableIdExist(String tableId) {
        long tid = Long.parseLong(tableId);
        boolean flag = false;
        int count = columnInfoService.queryColumnInfoBytableId(tid);
        if (count > 0) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    public void saveColumnInfo(List<ColumnInfo> columnInfos, String tableId) {
        boolean flag = isTableIdExist(tableId);
        for (ColumnInfo columnInfo : columnInfos) {
            if (flag) {
                //添加操作
                columnInfoService.updateColumnInfo(columnInfo);
            } else {
                //修改操作
                columnInfoService.addColumnInfo(columnInfo);
            }
        }

    }
}