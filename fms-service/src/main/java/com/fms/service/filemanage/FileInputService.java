package com.fms.service.filemanage;

import com.fms.domain.filemanage.File;
import com.fms.domain.filemanage.FileInput;
import com.fms.domain.filemanage.User;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 离线文件导入服务实现类.
 *
 * @author drc
 */
@Service
@Transactional
public class FileInputService {
		public static final String CLASSNAME = FileInputService.class.getName() + CharPool.PERIOD;
	@Autowired
	private Dao dao;


	public void add(FileInput fileInput) {
		dao.insert(CLASSNAME,"addFileInput", fileInput);
	}
	public FileInput get(Long id) {
		return dao.get(CLASSNAME, "getFileInput", id);
	}


	public List<FileInput> getList(Map<String, Object> params) {

		return dao.getList(CLASSNAME, "getFileInputList", params);
	}


	public void delete(Long id) {
		dao.delete(CLASSNAME, "deleteFileInput", id);
	}

	public FileInput queryId(Long id)
	{
		return dao.get(CLASSNAME, "getFileInputById", id);
	}

	public void update(FileInput fileInput) {
		dao.update(CLASSNAME, "updateFInput", fileInput);
	}
}