package com.fms.controller.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.domain.schema.ColumnInfo;
import com.fms.domain.schema.TableInfo;
import com.fms.domain.filemanage.MasterSlaveDo;
import com.fms.domain.schema.Template;
import com.fms.service.schema.ColumnInfoService;
import com.fms.service.schema.ColumnSetService;
import com.fms.service.masterSlave.ColumnValuesService;
import com.fms.service.masterSlave.MasterSlaveService;
import com.fms.service.schema.SchemaService;
import com.fms.utils.ParamUtil;
import com.handu.apollo.base.Page;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.json.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SchemaController {
    @Autowired
    private SchemaService schemaService;
    @Autowired
    private ColumnSetService columnSetService;
    @Autowired
    private MasterSlaveService masterSlaveService;

    @Autowired
    private ColumnValuesService columnValuesService;
    @Autowired
    private ColumnInfoService columnInfoService;

    @Autowired
    private KafkaTemplate kafkaTemplate;


    @RequestMapping("listColumns")
    public List<Map<String, Object>> listColumns(String tableName) {
        return schemaService.listColumns(tableName);
    }

    @RequestMapping("getMasterSlaveList")
    public Object getMasterSlaveList() {
        MasterSlaveDo masterSlaveDo = new MasterSlaveDo();
        return masterSlaveService.query(masterSlaveDo);
    }

    @RequestMapping("getMenuListFormasterslave")
    public Object getMenuListFormasterslave() {
        List<MasterSlaveDo> masterSlaveDoList = new ArrayList<MasterSlaveDo>();
        List<String> parentList = masterSlaveService.queryType();
        for (String parentType : parentList) {
            MasterSlaveDo masterSlaveDo = new MasterSlaveDo();
            masterSlaveDo.setName(parentType);

            MasterSlaveDo masterSlaveDoForQuery = new MasterSlaveDo();
            masterSlaveDoForQuery.setType(parentType);
            List<MasterSlaveDo> child = masterSlaveService.query(masterSlaveDoForQuery);
            masterSlaveDo.setChildren(child);
            masterSlaveDoList.add(masterSlaveDo);
        }
        return masterSlaveDoList;
    }


    @RequestMapping("listColumnsFormasterslave")
    public List<Map<String, Object>> listColumnsFormasterslave(String masterSlaveId) {
        MasterSlaveDo masterSlaveDoForQuery = new MasterSlaveDo();
        masterSlaveDoForQuery.setId(masterSlaveId);
        List<MasterSlaveDo> list = masterSlaveService.query(masterSlaveDoForQuery);

        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        if (list != null && list.size() > 0) {
            MasterSlaveDo masterSlaveDo = list.get(0);

            List<ColumnInfo> masterList = schemaService.listColumnsForMasterTable(masterSlaveDo.getMasterTableId());

            for (ColumnInfo column : masterList) {
                TableInfo tableInfo = columnInfoService.queryTableInfoById(column.getTableId());
                Map<String, Object> columnMap = schemaService.getColumnnInfo(tableInfo.getTableEnglish(), column.getColumnEnglish());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("column", column);
                if (columnMap != null) {
                    map.putAll(columnMap);
                }
                if (StringUtils.isNotEmpty(column.getDicTableName())) {
                    List<Map<String, Object>> dicList = columnSetService.getDicColumnsByDicName(column.getDicTableName());
                    map.put("dicList", dicList);

                }
                returnList.add(map);
            }
            List<ColumnInfo> returnListForSLave = schemaService.listColumnsForMasterTable(masterSlaveDo.getSlaveTableId());

            for (ColumnInfo column : returnListForSLave) {
                TableInfo tableInfo = columnInfoService.queryTableInfoById(column.getTableId());
                Map<String, Object> columnMap = schemaService.getColumnnInfo(tableInfo.getTableEnglish(), column.getColumnEnglish());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("column", column);
                
                if (StringUtils.isNotEmpty(column.getDicTableName())) {
                    List<Map<String, Object>> dicList = columnSetService.getDicColumnsByDicName(column.getDicTableName());
                    map.put("dicList", dicList);
                    if (columnMap != null) {
                        map.putAll(columnMap);
                    }
                }
                returnList.add(map);
            }
            return returnList;
        }
        return null;
    }


    @RequestMapping("getTables")
    public Object getTables() {
        return schemaService.getTables();
    }


/*    @RequestMapping("insertDataFormasterslave")
    public Object insertDataFormasterslave(String masterslavename, String data) {
//        if(true){
//            kafkaTemplate.send("schema",data);
//        }else{
            ObjectMapper mapper = JsonUtil.getMapper();
            MasterSlaveDo masterSlaveDoForQuery = new MasterSlaveDo();
            masterSlaveDoForQuery.setName(masterslavename);
            List<MasterSlaveDo> list = masterSlaveService.query(masterSlaveDoForQuery);

            if (list != null && list.size() > 0)
            {
                MasterSlaveDo masterSlaveDo = list.get(0);

                try {
                    if (StringUtils.isNotEmpty(masterSlaveDo.getMasterTable()))
                    {
                        schemaService.insertData(masterSlaveDo.getMasterTable(), mapper.readValue(data, List.class));
                        if (StringUtils.isNotEmpty(masterSlaveDo.getSlaveTable()))
                        {
                            schemaService.insertData(masterSlaveDo.getSlaveTable(), mapper.readValue(data, List.class));
                        }
                    }
                } catch (Exception e) {
                    return ExtUtil.failure(e.getCause().getMessage());
                }
            }
//        }

        return ExtUtil.success("操作成功");
    }*/

    @RequestMapping("insertData")
    public Object insertData(String tableName, String data) {
        ObjectMapper mapper = JsonUtil.getMapper();
        try {
            schemaService.insertData(tableName, mapper.readValue(data, List.class));
        } catch (Exception e) {
            return ExtUtil.failure(e.getCause().getMessage());
        }
        return ExtUtil.success("操作成功");
    }

    /**
     * 分页查询解析器
     *
     * @param request
     * @return
     */
    @RequestMapping("/masterSlave/page")
    public Object page(HttpServletRequest request) {
        MasterSlaveDo masterSlaveDo = new MasterSlaveDo();
        Page page = ParamUtil.getPager(request);
        Page<MasterSlaveDo> pageList = masterSlaveService.page(masterSlaveDo, page);
        List<MasterSlaveDo> list = pageList.getList();
        for(MasterSlaveDo msd : list){
            long masterTableId = msd.getMasterTableId();
            long slaveTableId = msd.getSlaveTableId();

            String masterTableName = schemaService.getTableNameById(masterTableId);
            msd.setMasterTableName(masterTableName);

            String slaveTableName = schemaService.getTableNameById(slaveTableId);
            msd.setSlaveTableName(slaveTableName);
        }
        return pageList;
    }

    @RequestMapping("/masterSlave/detail")
    public Object detail(String id, HttpServletRequest request) {
        MasterSlaveDo masterSlaveDo = new MasterSlaveDo();
        masterSlaveDo.setId(id);
        List<MasterSlaveDo> list = masterSlaveService.query(masterSlaveDo);
        MasterSlaveDo masterSlaveDoForDetail = new MasterSlaveDo();
        if (list != null && list.size() > 0) {
            masterSlaveDoForDetail = list.get(0);
        }
        return masterSlaveDoForDetail;
    }

    @RequestMapping("/masterSlave/add")
    public Object addmasterSlave(MasterSlaveDo masterSlaveDo) {
        try {
            masterSlaveService.add(masterSlaveDo);
        } catch (Exception e) {
            return ExtUtil.failure(e.getCause().getMessage());
        }
        return ExtUtil.success("操作成功");
    }

    @RequestMapping("/masterSlave/update")
    public Object updatemasterSlave(MasterSlaveDo masterSlaveDo) {
        try {
            masterSlaveService.update(masterSlaveDo);
        } catch (Exception e) {
            return ExtUtil.failure(e.getCause().getMessage());
        }
        return ExtUtil.success("操作成功");
    }

    @RequestMapping("/masterSlave/delete")
    public Object delete(String id, HttpServletRequest request) {
        masterSlaveService.delete(id);
        return ExtUtil.success("操作成功");
    }


}
