package com.fms.service.filemanage;

import com.fms.domain.filemanage.FileReport;
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
public class FileReportService {
		public static final String CLASSNAME = FileReportService.class.getName() + CharPool.PERIOD;
	@Autowired
	private Dao dao;


	public void add(FileReport fileReport) {
		dao.insert(CLASSNAME,"addFileReport", fileReport);
	}
	public FileReport get(Long id) {
		return dao.get(CLASSNAME, "getFileReport", id);
	}


	public List<FileReport> getList(Map<String, Object> params) {

		return dao.getList(CLASSNAME, "getFileReportList", params);
	}


	public void delete(Long id) {
		dao.delete(CLASSNAME, "deleteFileReport", id);
	}

	public FileReport queryId(Long id)
	{
		return dao.get(CLASSNAME, "getFileReportById", id);
	}

	public void update(FileReport fileReport) {
		dao.update(CLASSNAME, "updateFReport", fileReport);
	}
}