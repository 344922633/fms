package com.fms.controller.schema;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fms.domain.filemanage.MasterSlaveDo;
import com.fms.domain.schema.ColumnDic;
import com.fms.domain.schema.Template;
import com.fms.service.schema.TemplateService;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/template")
public class TemplateController {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private Environment env;


    @RequestMapping("/getList")
    public Object getList(Map<String, Object> params) {
        List<Template> list = templateService.getList(params);
        for(Template tp : list){
            long tableId = tp.getTableId();
            int schemaId = tp.getSchemaId();
            int columnId = tp.getColumnId();

            String tableName = templateService.getTableNameById(tableId);
            tp.setTableName(tableName);
            String schemaName = templateService.getSchemaNameById(schemaId);
            tp.setSchemaName(schemaName);
            String columnName = templateService.getColumnNameById(columnId);
            tp.setColumnName(columnName);

        }
        return list;
    }


    @RequestMapping("/delete")
    public void delete(String id) {
            templateService.deleteTemplate(Long.parseLong(id));
            templateService.deleteTemplateDic(Long.parseLong(id));

    }

//模板映射新增
    @RequestMapping("/add")
    public void addTemplate(@RequestParam Map<String, Object> params) {
        JSONArray array = JSONArray.parseArray((String) params.get("formList"));
        long id=System.currentTimeMillis();
        for(int i=0;i<array.size();i++){
            JSONObject obj = JSONObject.parseObject(array.get(i).toString());
            int schemaId = obj.getInteger("schemaId");
            long tableId = obj.getLong("tableId");
            int columnId = obj.getInteger("columnId");
            String columnKey =  obj.getString("columnKey");
            String templateName =  obj.getString("templateName");
            Template template = new Template();
            template.setId(id);
            template.setColumnKey(columnKey);
            template.setSchemaId(schemaId);
            template.setTableId(tableId);
            template.setColumnId(columnId);
            template.setTemplateName(templateName);
            templateService.addTemplate(template);
       /*     ColumnDic columnDic = new ColumnDic();
            columnDic.setColumnMapId(id);
            columnDic.setDicName();
            templateService.addTemplateDic(columnDic);*/
        }

    }

//模板映射编辑
    @RequestMapping("/update")
    public Object updateTemplate(Template template) {
        try {
            templateService.updateTemplate(template);
            templateService.updateTemplateDic(template);
        } catch (Exception e) {
            return ExtUtil.failure(e.getCause().getMessage());
        }
        return ExtUtil.success("操作成功");
    }


}
