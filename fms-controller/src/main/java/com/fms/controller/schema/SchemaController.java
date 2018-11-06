package com.fms.controller.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.domain.filemanage.MasterSlaveDo;
import com.fms.service.masterSlave.MasterSlaveService;
import com.fms.service.schema.SchemaService;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SchemaController {
    @Autowired
    private SchemaService schemaService;

    @Autowired
    private MasterSlaveService masterSlaveService;

    @RequestMapping("listColumns")
    public List<Map<String,Object>> listColumns(String tableName) {
        return schemaService.listColumns(tableName);
    }

    @RequestMapping("getMasterSlaveList")
    public Object getMasterSlaveList() {
        MasterSlaveDo masterSlaveDo = new MasterSlaveDo();
        return masterSlaveService.query(masterSlaveDo);
    }

    @RequestMapping("listColumnsFormasterslave")
    public List<Map<String,Object>> listColumnsFormasterslave(String masterslavename) {
        MasterSlaveDo masterSlaveDoForQuery = new MasterSlaveDo();
        masterSlaveDoForQuery.setName(masterslavename);
        List<MasterSlaveDo> list = masterSlaveService.query(masterSlaveDoForQuery);
        if (list != null && list.size() > 0)
        {
            MasterSlaveDo masterSlaveDo = list.get(0);

            if (StringUtils.isNotEmpty(masterSlaveDo.getMasterTable()))
            {
                List<Map<String,Object>> returnList = schemaService.listColumns(masterSlaveDo.getMasterTable());
                if (StringUtils.isNotEmpty(masterSlaveDo.getSlaveTable()))
                {
                    returnList.addAll(schemaService.listColumns(masterSlaveDo.getSlaveTable()));
                }
                return returnList;
            }
        }
        return null;
    }


    @RequestMapping("getTables")
    public Object getTables() {
        return schemaService.getTables();
    }


    @RequestMapping("insertDataFormasterslave")
    public Object insertDataFormasterslave(String masterslavename, String data) {
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
}
