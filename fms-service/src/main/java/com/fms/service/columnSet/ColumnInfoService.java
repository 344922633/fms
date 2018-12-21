package com.fms.service.columnSet;

import com.fms.domain.columnSet.ColumnInfo;
import com.fms.domain.columnSet.TableInfo;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColumnInfoService {
    public static final String CLASSNAME = ColumnInfoService.class.getName() + CharPool.PERIOD;

    @Autowired
    private Dao dao;

    public void deleteTableInfo(long tableId) {
        dao.delete(CLASSNAME, "deleteTableInfo", tableId);
    }

    public void deleteColumnInfo(long tableId) {
        dao.delete(CLASSNAME, "deleteColumnInfo", tableId);
    }

    public void addTableInfo(TableInfo tableInfo) {
        dao.insert(CLASSNAME, "addTableInfo", tableInfo);
    }

    public void updateTableInfo(TableInfo tableInfo) {
        dao.insert(CLASSNAME, "updateTableInfo", tableInfo);
    }

    public List<ColumnInfo> getColumnsInfo(long tid) {

        return dao.getList(CLASSNAME, "getColumnsInfo", tid);
    }

    public List<TableInfo> getAllTables() {

        return dao.getList(CLASSNAME, "getAllTables");
    }

    public List<String> getDicByTableId(long tid) {
        return dao.get(CLASSNAME, "getDicByTableId", tid);
    }

    public TableInfo queryTableInfoById(long tableId) {
        return dao.get(CLASSNAME, "queryTableInfoById", tableId);
    }

    public int queryColumnInfoBytableId(long tableId) {
        return dao.get(CLASSNAME, "queryColumnInfoBytableId", tableId);
    }

    public void addColunmInfo(ColumnInfo columnInfo) {
        dao.insert(CLASSNAME, "addColumnInfo", columnInfo);
    }

    public void updateColumnInfo(ColumnInfo columnInfo) {
        dao.update(CLASSNAME, "updateColumnInfo", columnInfo);
    }

    public List<String> getDicTable() {
        return dao.getList(CLASSNAME, "getDicTable", null);
    }

}


