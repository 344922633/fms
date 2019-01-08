package com.fms.controller.schema;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fms.domain.schema.ColumnDic;
import com.fms.domain.schema.ColumnMapRelation;
import com.fms.domain.schema.Template;
import com.fms.service.column.ColumnMapRelationService;
import com.fms.service.schema.TemplateService;
import com.handu.apollo.utils.ExtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/template")
public class TemplateController {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private Environment env;

    @Autowired
    private ColumnMapRelationService columnMapRelationService;

    @RequestMapping("/getList")
    public Object getList(Map<String, Object> params) {
        List<Template> list = templateService.getList(params);
        for(Template tp : list){
            long tableId = tp.getTableId();
            int schemaId = tp.getSchemaId();
            int columnId = tp.getColumnId();
            String templateName = tp.getTemplateName();

            String tableName = templateService.getTableNameById(tableId);
            tp.setTableName(tableName);
            String schemaName = templateService.getSchemaNameById(schemaId);
            tp.setSchemaName(schemaName);
            String columnName = templateService.getColumnNameById(columnId);
            tp.setColumnName(columnName);
         /*   tp.setTemplateName(templateName);*/
/*
            //根据id查询到column_dic表记录
            List<ColumnDic> columnDics = templateService.findColumnDicById(tp.getId());

            Map<String,Object> dicMap = new HashMap<>();
            for (ColumnDic columnDic : columnDics) {
                //创建map数据
                dicMap.put(columnDic.getDicName(),columnDic.getDicValue());
            }

            tp.setDicMap(dicMap);*/

        }
        return list;
    }
//  映射模板删除
    @RequestMapping("/deleteTemplate")
    public void delete(Long id) {
        Long tid =id;
        templateService.deleteTemplate(id);
        templateService.deleteTemplateDic(tid);
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


//模板映射编辑信息显示
    @RequestMapping("/findAllByTemplate")
    public List<Template> findAllByTemplate(String templateName){
        List<Template> list=templateService.findAllByTemplate(templateName);
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

            //根据id查询到column_dic表记录
        List<ColumnDic> columnDics = templateService.findColumnDicById(tp.getId());

        Map<String,Object> dicMap = new HashMap<>();
        for (ColumnDic columnDic : columnDics) {
            //创建map数据
            dicMap.put(columnDic.getDicName(),columnDic.getDicValue());
        }

        tp.setDicMap(dicMap);

    }
    return list;
    }

/*   @RequestMapping("/updateColumnMapRelations")
    public Object updateColumnMapRelations(@RequestParam Map<String, Object> params){
        String json = (String) params.get("formList2");
        List<ColumnMapRelation> columnMapRelations = JSON.parseArray(json, ColumnMapRelation.class);
        try {
            columnMapRelationService.addColumnMapRelations(columnMapRelations);
            return ExtUtil.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ExtUtil.failure(e.getCause().getMessage());
        }
    }*/

}
