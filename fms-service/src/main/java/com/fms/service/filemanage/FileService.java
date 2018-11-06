package com.fms.service.filemanage;

import com.fms.domain.filemanage.File;
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
public class FileService {
    public static final String CLASSNAME = FileService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    /**
     * 根据id获取文件信息
     * @param id
     * @return
     */
    public File get(Long id) {
        return dao.get(CLASSNAME, "get", id);
    }

    /**
     * 获取总条数
     * @param params
     * @return
     */
    public int queryCount(Map<String, Object> params) {
        return dao.get(CLASSNAME, "queryCount", params);
    }

    /**
     *
     * @param params
     * @return
     */
    public int queryCountByIsParserAndRemoveRepeat(Map<String, Object> params) {
        return dao.get(CLASSNAME, "queryCountByIsParserAndRemoveRepeat", params);
    }

    /**
     * 分页查询
     * @param params
     * @param page
     * @return
     */
    public Page<File> page(Map<String, Object> params, Page page) {
        return dao.page(CLASSNAME, "query", "queryCount", params, page);
    }
    public Page<File> pageFileByIsParserAndRemoveRepeat(Map<String, Object> params, Page page) {
        return dao.page(CLASSNAME, "pageFileByIsParserAndRemoveRepeat", "queryCountByIsParserAndRemoveRepeat", params, page);
    }

    /**
     * 条件查询
     * @param params 条件
     * @return
     */
    public List<File> query(Map<String, Object> params) {
        return dao.getList(CLASSNAME, "query", params);
    }

    /**
     * 根据所属文件夹id列表查询文件
     * @param params
     * @return
     */
    public List<File> getFileListByDirectoryIds(Map<String, Object> params) {
        return dao.getList(CLASSNAME, "getFileListByDirectoryIds", params);
    }

    /**
     * 新增文件
     * @param file
     */
    public void add(File file) {
        dao.insert(CLASSNAME, "add", file);
    }

    /**
     * 修改文件
     * @param file
     */
    public void update(File file) {
        dao.update(CLASSNAME, "update", file);
    }

    /**
     * 删除文件
     * @param id
     */
    public void delete(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        dao.delete(CLASSNAME, "delete", params);
    }

    public List<Map> getFileStatistic() {
        List<Map> list = dao.getList(CLASSNAME, "getFileStatistic", null);
        return list;
    }

    public List<Map> getFileSuffixStatistic() {
        return dao.getList(CLASSNAME, "getFileSuffixStatistic", null);
    }
}
