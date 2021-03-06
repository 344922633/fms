package com.fms.service.schema;

import com.fms.domain.filemanage.MasterSlaveDo;
import com.fms.domain.schema.ColumnDic;
import com.fms.domain.schema.Template;
import com.google.common.collect.Maps;
import com.handu.apollo.base.Page;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TemplateService {
    public static final String CLASSNAME = TemplateService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    public String getParserNameById(long pid) {
        return dao.get(CLASSNAME, "getParserNameById", pid);
    }

    public String getTableNameById(long tid) {
        return dao.get(CLASSNAME, "getTableNameById", tid);
    }

    public String getSchemaNameById(int sid) {
        return dao.get(CLASSNAME, "getSchemaNameById", sid);
    }

    public String getColumnNameById(int cid) {
        return dao.get(CLASSNAME, "getColumnNameById", cid);
    }

    public String getTemplateNameById(long id) {
        return dao.get(CLASSNAME, "getTemplateNameById", id);
    }


    public Page<Template> page(Template template, Page page) {
        return dao.page(CLASSNAME, "query", "queryCount", template, page);
    }


    public Template get(Long id) {
        return dao.get(CLASSNAME, "getTemplate", id);
    }


    public List<Template> getList(Map<String, Object> params) {

        return dao.getList(CLASSNAME, "getList", null);
    }


    public void delete(Long id) {
        dao.delete(CLASSNAME, "delete", id);
    }

    public Template queryId(Long id) {
        return dao.get(CLASSNAME, "getTemplateById", id);
    }

    public void update(Template template) {
        dao.update(CLASSNAME, "update", template);
    }


    public void addTemplate(Template template) {
        dao.insert(CLASSNAME, "addTemplate", template);
    }

    public void addTemplateDic(Template template) {
        dao.insert(CLASSNAME, "addTemplateDic", template);
    }


    public void updateTemplate(Template template) {
        dao.update(CLASSNAME, "updateTemplate", template);
    }

    public void updateTemplateDic(Template template) {
        dao.update(CLASSNAME, "updateTemplateDic", template);
    }
    public void deleteTemplateDic(Long id) { dao.delete(CLASSNAME, "deleteTemplateDic", id); }

    public void deleteTemplate(Long id) {
        dao.delete(CLASSNAME, "deleteTemplate", id);
    }

    public List<ColumnDic> findColumnDicById(Long id){
        return dao.getList(CLASSNAME,"findColumnDicById",id);
    }


    public List<Template> findAllByTemplate(String templateName) {
        return dao.getList(CLASSNAME,"findAllByTemplate",templateName);
    }

}