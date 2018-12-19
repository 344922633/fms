package com.fms.controller.columnSet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fms.domain.columnSet.ColumnInfo;
import com.fms.domain.columnSet.TableInfo;
import com.fms.service.columnSet.ColumnInfoService;
import com.handu.apollo.utils.ExtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ColumnInfoController {
    @Autowired
    private ColumnInfoService columnInfoService;

    @RequestMapping("getDicTable")
    public List<String> getDicTable() {
        return columnInfoService.getDicTable();
    }

    @RequestMapping("/deleteTableInfo")
    public Object deleteTableInfo(String id) {
        int tid = Integer.valueOf(id);
        columnInfoService.deleteTableInfo(tid);
        columnInfoService.deleteColumnInfo(tid);
        return ExtUtil.success("操作成功");
    }

    @RequestMapping("/updateTableInfo")
    public Object updateTableInfo(HttpServletRequest request) {
        String tableId = request.getParameter("tableId");
        int tid = Integer.valueOf(tableId);
        String tableEnglish = request.getParameter("tableEnglish");
        String tableChinese = request.getParameter("tableChinese");

        TableInfo tableInfo = columnInfoService.queryTableInfoById(tid);
        tableInfo.setTableChinese(tableChinese);
        tableInfo.setTableEnglish(tableEnglish);
        columnInfoService.updateTableInfo(tableInfo);
        return ExtUtil.success("操作成功");
    }


    @RequestMapping("/addTableInfo")
    public Object addTableInfo(HttpServletRequest request) {
        String tableEnglish = request.getParameter("tableEnglish");
        String tableChinese = request.getParameter("tableChinese");
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableEnglish(tableEnglish);
        tableInfo.setTableChinese(tableChinese);
        columnInfoService.addTableInfo(tableInfo);
        return ExtUtil.success("操作成功");
    }

    @RequestMapping("getColumnsInfo")
    public Object getColumnsInfo(int id) {
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
                columnInfo.setDicTableName(dicName);
                columnInfo.setColumnChinese(columnChinese);
                columnInfo.setColumnEnglish(columnEnglish);
                columnInfo.setSchemaId(1);
                columnInfo.setTableId(Integer.valueOf(tableId));
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


    @RequestMapping("getDicByTableId")
    public List<String> getDicByTableId(int id) {
        return columnInfoService.getDicByTableId(id);
    }

/*    @RequestMapping("getDicInfoByName")
    public List<String> getDicInfoByName(String dicName) {
        return columnInfoService.getDicInfoByName(dicName);
    }*/


    @RequestMapping("isTableIdExist")
    public boolean isTableIdExist(String tableId) {
        int tid = Integer.valueOf(tableId);
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
                columnInfoService.addColunmInfo(columnInfo);
            }
        }

    }
}