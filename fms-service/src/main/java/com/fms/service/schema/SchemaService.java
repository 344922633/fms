package com.fms.service.schema;

import com.fms.domain.filemanage.MasterSlaveDo;
import com.fms.domain.schema.ColumnInfo;
import com.fms.domain.schema.Template;
import com.fms.utils.SchemaUtil;
import com.handu.apollo.base.Page;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.data.utils.Param;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SchemaService {
    public static final String CLASSNAME = SchemaService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    private Environment env;
    public List<Map<String,Object>> listColumns(String tableName) {
        Map<String, Object> params = Param.get()
                .put("tableName", tableName)
                .build();
        return dao.getList(CLASSNAME, "listColumns", params);
    }

    public Map<String,Object> getColumnnInfo(String tableName, String columnName) {
        Map<String, Object> params = Param.get()
                .put("tableName", tableName).put("columnName", columnName)
                .build();
        return dao.get(CLASSNAME, "getColumnnInfo", params);
    }

    public List<ColumnInfo> listColumnsForMasterTable(long tableId) {
        return dao.getList(CLASSNAME, "listColumnsForMasterTable", tableId);
    }

    public List<String> getTables() {
        return dao.getList(CLASSNAME, "getAllTables", null);
    }

    public void insertData(String tableName, List<Map<String, Object>> data) {
        List<Map<String,Object>> tableColumnTemp =  listColumns(tableName);
        for (Map<String,Object> column : tableColumnTemp)
        {
            for (Map<String,Object> columnData : data)
            {
                if (column.get("column_name").equals(columnData.get("column_name")))
                {
                    column.put("value",columnData.get("value"));
                    break;
                }
            }
        }
        Map<String, Object> params = Param.get()
                .put("tableName", tableName)
                .put("data", tableColumnTemp)
                .build();
 //       dao.insert(CLASSNAME, "insertData", params);
        //发送kafka消息
        kafkaTemplate.send(env.getProperty("DEFAULT_TOPIC"),SchemaUtil.schemaStrFormat(tableName,tableColumnTemp));
    }

    public String getTableNameById(long tid){
        return dao.get(CLASSNAME, "getTableNameById", tid);
    }
    public String getTableEnglish(long id){
        return dao.get(CLASSNAME, "getTableEnglish", id);
    }


}
