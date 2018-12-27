package com.fms.controller.schema;

import com.alibaba.fastjson.JSONObject;
import com.fms.domain.filemanage.MasterSlaveDo;
import com.fms.domain.schema.ColumnDic;
import com.fms.domain.schema.ColumnMapRelation;
import com.fms.domain.schema.Template;
import com.fms.service.schema.ColumnSetService;
import com.fms.service.schema.TemplateService;
import com.handu.apollo.utils.ExtUtil;
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
            long parserId = tp.getParserId();

            String tableName = templateService.getTableNameById(tableId);
            tp.setTableName(tableName);
            String schemaName = templateService.getSchemaNameById(schemaId);
            tp.setSchemaName(schemaName);
            String columnName = templateService.getColumnNameById(columnId);
            tp.setColumnName(columnName);
            String parserName = templateService.getParserNameById(parserId);
            tp.setParserName(parserName);

        }
        return list;
    }


/*

    @RequestMapping("update")
    public void update(@RequestParam Map<String, Object> params) {
        String id = (String) params.get("id");
        String ip = (String) params.get("ip");
        String userName = (String) params.get("userName");
        String password = (String) params.get("password");
        String port = (String) params.get("port");
        String path = (String) params.get("path");
        String format = (String) params.get("format");
        Template template = templateService.queryId(Long.parseLong(id));
        template.setIp(ip);
        template.setUserName(userName);
        template.setPassword(password);
        template.setPath(path);
        template.setFormat(format);
        template.setPort(port);

        templateService.update(template);
    }

    @RequestMapping("get")
    public Object get(String id) {
        return templateService.get((Long.parseLong(id)));
    }

    @RequestMapping("add")
    public void add(@RequestParam Map<String, Object> params) {
        String ip = (String) params.get("ip");
        String userName = (String) params.get("userName");
        String password = (String) params.get("password");
        String port = (String) params.get("port");
        String path = (String) params.get("path");
        String format = (String) params.get("format");
        Template template = new Template();
        template.setIp(ip);
        template.setUserName(userName);
        template.setPassword(password);
        template.setPort(port);
        template.setPath(path);
        template.setFormat(format);
        templateService.add(template);

    }
*/

    @RequestMapping("/deleteTemplate")
    public void delete(String id) {
        templateService.deleteTemplate(Long.parseLong(id));
        templateService.deleteTemplateDic(Long.parseLong(id));
    }

//模板映射新增
    @RequestMapping("/addTemplate")
    public void addTemplate(@RequestParam Map<String, Object> params) {
        long id =  Long.parseLong((String)params.get("id"));
        String columnKey = (String) params.get("columnKey");
        int schemaId = Integer.parseInt((String)params.get("schemaId"));
        long tableId =  Long.parseLong((String)params.get("tableId"));
        int columnId = Integer.parseInt((String)params.get("columnId"));
        long parserId =  Long.parseLong((String)params.get("parserId"));

        Template template = new Template();
        template.setId(id);
        template.setColumnKey(columnKey);
        template.setSchemaId(schemaId);
        template.setTableId(tableId);
        template.setColumnId(columnId);
        template.setParserId(parserId);
        templateService.addTemplate(template);
        templateService.addTemplateDic(template);

    }


    @Autowired
    private ColumnSetService columnSetService;
    @RequestMapping("/saveColumnMapInfos")
    public Object saveColumnMapInfos(String formList,String parserId) {
        JSONObject json = JSONObject.parseObject(formList);
        // 保存第一张表
        for(String key : json.keySet()) {

            if(StringUtils.isNotEmpty(json.getJSONObject(key).getString("columnId"))){
                ColumnMapRelation mr = new ColumnMapRelation();
                String columnMapId = String.valueOf(System.currentTimeMillis());
                /* mr.setId(columnMapId);*/
                mr.setColumnKey(key);
                mr.setColumnId(json.getJSONObject(key).getInteger("columnId"));
                mr.setSchemaId(json.getJSONObject(key).getInteger("schemaId"));
                mr.setTableId(Long.valueOf(json.getJSONObject(key).getString("tableId")));
                mr.setParserId(Long.parseLong(parserId));
                columnSetService.insertColumnMapRelation(mr);

            }

        }
        return ExtUtil.success("保存成功！");
    }




//模板映射编辑
    @RequestMapping("/updateTemplate")
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
