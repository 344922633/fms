package com.fms.controller.column;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fms.domain.schema.ColumnMapRelation;
import com.fms.service.column.ColumnMapRelationService;
import com.handu.apollo.utils.ExtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.applet.Main;

import java.util.List;
import java.util.Map;

@RestController
public class ColumnMapRelationControl {

    @Autowired
    private ColumnMapRelationService columnMapRelationService;

    /**
     *
     * 单文件解析 根据columnKeys对模板筛选
     * @param columnKeys
     * @return
     */
    @RequestMapping("/getColumnMapRelation")
    public Map<String,Object> getColumnMapRelation(@RequestParam(required = false) List<String> columnKeys){
        return columnMapRelationService.getColumnMapRelation(columnKeys);
    }

    /**
     * 单文件解析  根据templateName匹配对应信息
     * @param templateName
     * @return
     */
    @RequestMapping("/getColumnMapRelationByTemplateName")
    public List<ColumnMapRelation> getColumnMapRelationByTemplateName(String templateName) {
        return columnMapRelationService.getColumnMapRelationByTemplateName(templateName);
    }


    /**
     * 映射模板新增及更新
     */
    @RequestMapping("/addColumnMapRelations")
    public Object addColumnMapRelations(@RequestParam Map<String, Object> params){
        String json = (String) params.get("formList");
        List<ColumnMapRelation> columnMapRelations = JSON.parseArray(json, ColumnMapRelation.class);
        try {
            columnMapRelationService.addColumnMapRelations(columnMapRelations);
            return ExtUtil.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ExtUtil.failure(e.getCause().getMessage());
        }
    }
    }




