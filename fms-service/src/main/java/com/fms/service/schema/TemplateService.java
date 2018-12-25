package com.fms.service.schema;

import com.fms.domain.schema.Template;
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

	public String getParserNameById(long pid){
		return dao.get(CLASSNAME, "getParserNameById", pid);
	}
	public String getTableNameById(long tid){
		return dao.get(CLASSNAME, "getTableNameById", tid);
	}

	public String getSchemaNameById(int sid){
		return dao.get(CLASSNAME, "getSchemaNameById", sid);
	}

	public String getColumnNameById(int cid){
		return dao.get(CLASSNAME, "getColumnNameById", cid);
	}

	public Page<Template> page(Template template, Page page) {
		return dao.page(CLASSNAME, "query", "queryCount", template, page);
	}

	public void add(Template template) {
		dao.insert(CLASSNAME,"addTemplate", template);
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

	public Template queryId(Long id)
	{
		return dao.get(CLASSNAME, "getTemplateById", id);
	}

	public void update(Template template) {
		dao.update(CLASSNAME, "update", template);
	}
}