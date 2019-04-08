package com.fms.service.tuopu;

import com.fms.domain.filemanage.*;
import com.fms.domain.tuopu.Control;
import com.fms.domain.tuopu.Picture;
import com.handu.apollo.base.Page;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 拓扑图服务实现类.
 *
 * @author drc
 */
@Service
@Transactional
public class PictureService {
	public static final String CLASSNAME = PictureService.class.getName() + CharPool.PERIOD;

	@Autowired
	private Dao dao;

	/**
	 * 查询拓扑图列表
	 *
	 * @param params
	 * @return
	 */
	public List<Picture> getList(Map<String, Object> params) {
		return dao.getList(CLASSNAME, "getList", params);
	}
	public int query(String name) {
		return dao.get(CLASSNAME, "query", name);
	}

	public Page<Picture> page(Map<String, Object> params, Page page) {
		return dao.page(CLASSNAME, "query", "queryCount", params, page);
	}
	public void add(Picture picture){
		dao.insert(CLASSNAME,"add",picture);
	}


	public Picture get(Long id) {
		return dao.get(CLASSNAME, "get", id);
	}

	public Control getImageByName(String masterName) {
		return dao.get(CLASSNAME, "getImageByName", masterName);
	}


	public void delete(Long id) {
		dao.delete(CLASSNAME, "delete", id);
	}
	public void deleteNameById(Long id) {
		dao.update(CLASSNAME, "deleteNameById", id);
	}

	public void update(Picture picture) {
		dao.update(CLASSNAME, "update", picture);
	}

}
