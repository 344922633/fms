package com.fms.controller.schema;

import com.fms.domain.schema.Template;
import com.fms.service.schema.TemplateService;
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

    @RequestMapping("delete")
    public void delete(String id) {
        templateService.delete(Long.parseLong(id));
    }
}
