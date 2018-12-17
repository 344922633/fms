package com.fms.controller.columnSet;


import com.fms.domain.columnSet.Manual;
import com.fms.domain.filemanage.ColumnValuesDo;
import com.fms.domain.filemanage.MasterSlaveDo;
import com.fms.service.columnSet.ManuallyService;
import com.fms.service.schema.SchemaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/manually")
public class ManuallyController {

    @Autowired
    private ManuallyService manuallyService;
    @Autowired
    private SchemaService schemaService;

    @RequestMapping("getManuallyList")
    public Object getManuallyList() {
        Manual manual = new Manual();
        return manuallyService.query(manual);
    }
    @RequestMapping("getAllNzList")
    public Object getAllNzList() {
    Manual manual = new Manual();

    List<String> list =  manuallyService.getAllNzList(manual);
    return  list;
}


    @RequestMapping("listColumnsForTable")
    public List<Map<String,Object>> listColumnsForTable(String tableName) {
        List<Map<String,Object>> list = schemaService.listColumns(tableName);
                return list;
    }

}

