package com.fms.service.filemanage;

import com.fms.domain.filemanage.FileParserExt;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 文件解析器服务实现类.
 *
 * @author drc
 */
@Service
@Transactional
public class FileParserExtService {
	public static final String CLASSNAME = FileParserExtService.class.getName() + CharPool.PERIOD;
	@Autowired
	private Dao dao;

	/**
	 * 查询文件解析器list
	 *
	 * @param params
	 * @return
	 */
	public List<FileParserExt> getList(Map<String, Object> params) {
		return dao.getList(CLASSNAME, "query", params);
	}


	public FileParserExt get(Long id) {
		return dao.get(CLASSNAME, "get", id);
	}

	public void add(FileParserExt fileParserext) {
		fileParserext.setId(System.currentTimeMillis());
		dao.insert(CLASSNAME, "add", fileParserext);
	}

	public void update(FileParserExt fileParserext) {
		dao.update(CLASSNAME, "update", fileParserext);
	}

	public void delete(Long id) {
		dao.delete(CLASSNAME, "delete", id);
	}

}
