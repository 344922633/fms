package com.fms.controller.schema;

import com.alibaba.fastjson.JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.domain.schema.ColumnInfo;
import com.fms.domain.filemanage.MasterSlaveDo;
import com.fms.service.schema.ColumnInfoService;
import com.fms.service.schema.ColumnSetService;
import com.fms.service.masterSlave.ColumnValuesService;
import com.fms.service.masterSlave.MasterSlaveService;
import com.fms.service.schema.SchemaService;
import com.fms.utils.ParamUtil;
import com.fms.utils.PropertyUtil;
import com.handu.apollo.base.Page;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.json.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

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
    public List<Map<String, Object>> listColumnsFormasterslave(Integer masterSlaveId) {
        MasterSlaveDo masterSlaveDoForQuery = new MasterSlaveDo();
        masterSlaveDoForQuery.setId(masterSlaveId);
        List<MasterSlaveDo> list = masterSlaveService.query(masterSlaveDoForQuery);

        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        if (list != null && list.size() > 0) {
            MasterSlaveDo masterSlaveDo = list.get(0);

            List<ColumnInfo> masterList = columnInfoService.getColumnsInfo(masterSlaveDo.getMasterTableId());

            for (ColumnInfo column : masterList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("column", column);
                if (StringUtils.isNotEmpty(column.getDicTableName())) {
                    List<Map<String, Object>> dicList = columnSetService.getDicColumnsByDicName(column.getDicTableName());
                    map.put("dicList", dicList);
                }
                returnList.add(map);
            }

            List<ColumnInfo> returnListForSLave = columnInfoService.getColumnsInfo(masterSlaveDo.getSlaveTableId());

/*            for (ColumnInfo column : returnListForSLave) {
                TableInfo tableInfo = columnInfoService.queryTableInfoById(column.getTableId());
                List<ColumnInfo> columnInfoList = columnInfoService.getColumnsInfo(column.getTableId());

//                Map<String, Object> columnMap = schemaService.getColumnnInfo(tableInfo.getTableEnglish(), column.getColumnEnglish());
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
            }*/

            for (ColumnInfo column : returnListForSLave) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("column", column);
                if (StringUtils.isNotEmpty(column.getDicTableName())) {
                    List<Map<String, Object>> dicList = columnSetService.getDicColumnsByDicName(column.getDicTableName());
                    map.put("dicList", dicList);
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

    /* JSONObject data = JSONObject.parseObject(columnKeyNamesMap);*/
    @RequestMapping("insertDataFormasterslave")
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
            masterSlaveDo.setMasterTable("11111");
            masterSlaveDo.setSlaveTable("2222");
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
    }

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
    public Object detail(Integer id, HttpServletRequest request) {
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

    @RequestMapping("/masterSlave/findOne")
    public Object findOne(Integer id){
        return masterSlaveService.findMasterSlaveById(id);
    }

    @RequestMapping("/masterSlave/update")
    public Object updatemasterSlave(MasterSlaveDo masterSlaveDo) {
        try {
            masterSlaveService.update(masterSlaveDo);
        } catch (Exception e) {
            e.printStackTrace();
            return ExtUtil.failure(e.getCause().getMessage());
        }
        return ExtUtil.success("操作成功");
    }

    @RequestMapping("/masterSlave/delete")
    public Object delete(String id, HttpServletRequest request) {
        masterSlaveService.delete(id);
        return ExtUtil.success("操作成功");
    }


    /**
     *
     * 表单录入发送kafka
     * @param data
     * @return
     */
    @RequestMapping("/formEntrySendKafka")
    public Object formEntrySendKafka(String data) {

        List<Map> oData = JSON.parseArray(data,Map.class);
        Map<String,Object> params = getParams(oData);


        String json = JSON.toJSONString(params);

        System.out.println("kafka消息格式：\n" + json);
        kafkaTemplate.send(PropertyUtil.readValue("DEFAULT_TOPIC"),json);

        return "success";

    }

    private Map<String, Object> getParams(List<Map> data) {

        //查询tables
        Set<String> tables = gettables(data);

        //构建对象
        Map<String,Object> params = new HashMap<>();

        List<Map<String,Object>> objectDatas = new ArrayList<>();
        for (String table : tables) {
            //构建data
            Map<String,Object> objectData = getObjectData(table,data);
            objectDatas.add(objectData);
        }

        params.put("data",objectDatas);
        params.put("operationSource",PropertyUtil.readValue("DEFAULT_TOPIC"));


        return params;
    }

    private Map<String, Object> getObjectData(String tableId, List<Map> data) {

        Map<String, Object> objectData = new HashMap<>();
        //构建columns
        List<Map<String,Object>> columns = new ArrayList<>();

        for (Map<String, Object> datum : data) {
            Map<String, Object> childdata = (Map<String, Object>) datum.get("column");

            if(tableId.equals(childdata.get("tableId") + "")){
                //    "columnEnglish": "DXBM",
                //            "dataValue": "2133",
                Map<String,Object> column = new HashMap<>();
                String columnEnglish = (String)childdata.get("columnEnglish");
                String dataValue = (String) childdata.get("dataValue");

                column.put("name",columnEnglish.toLowerCase());
                column.put("value",dataValue);
                //判断是否为字典表
                String dicListName = (String) childdata.get("dicList");
                if (StringUtils.isNotEmpty(dicListName)){
                    List<Map> dicList = (List<Map>) datum.get("dicList");
                    if (dicList != null && dicList.size() > 0){
                        for (Map m: dicList) {
                            String mc = (String) m.get("MC");

                            if(dicListName.equals(mc)){
                                column.put("value",m.get("DM"));
                                break;
                            }

                        }
                    }
                }

                columns.add(column);

                //查看字段是否是dxbm
                if("dxbm".equals(columnEnglish.toLowerCase())){
                    //存放数据
                    objectData.put("objectCode",columnEnglish.toLowerCase());
                    objectData.put("objectCodeValue","dwj_" + dataValue);
                    objectData.put("operarionType","insert");
                    objectData.put("schema","renzhi 1208");

                    //根据表id获取表名
                    String tableName = columnInfoService.queryTableInfoById(Long.parseLong(tableId)).getTableEnglish();

                    objectData.put("table",tableName);
                }
            }

        }

        objectData.put("columns",columns);

        return objectData;

    }

    //获取集合中所有tableid
    private Set<String> gettables(List<Map> data) {

        Set<String> tables = new TreeSet<>();

        for (Map<String, Object> datum : data) {
            //获取column数据
            Map<String,Object> column = (Map<String, Object>) datum.get("column");
            tables.add( column.get("tableId") + "");
        }

        return tables;
    }
}



















