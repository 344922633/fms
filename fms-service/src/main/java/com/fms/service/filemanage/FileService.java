package com.fms.service.filemanage;

import com.fms.domain.filemanage.File;
import com.google.common.collect.Maps;
import com.handu.apollo.base.Page;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
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

    public String getTextById(Long id) {
        return dao.get(CLASSNAME, "getTextById", id);
    }
    public String getNameById(Long id) {
        return dao.get(CLASSNAME, "getNameById", id);
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

        Date utilDate  =new Date();
        java.sql.Date addtime = new java.sql.Date(utilDate.getTime());
        file.setAddTime(addtime);
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

    public List<File> getAllFIles() {
        List<File> list = dao.getList(CLASSNAME, "getAllFIles", null);
        return list;
    }

    //修改解析状态
    public void updateFileInIsParserById(File file){

        dao.update(CLASSNAME,"updateFileInIsParserById",file);
    }

    /**
     * 根据id添加file表中的MapTemplateName和IsSaveTemplateName信息
     * @param file
     */
    public void updateFileInMapTemplateNameAndIsSaveTemplateName(File file){
        dao.update(CLASSNAME,"updateFileInMapTemplateNameAndIsSaveTemplateName",file);
    }

    /**
     * 根据id查询MapTemplateName
     * @param
     * @return
     */
    public String findMapTemplateNameById(Long id){
        return dao.get(CLASSNAME,"findMapTemplateNaomeById",id);
    }

    /**
     * 根据id修改IsReport字段状态
     * @param id
     * @param isReport
     */
    public void updateFileIsReportById(Long id, int isReport) {

        Map<String,Object> params = new HashMap<>();
        params.put("id",id);
        params.put("isReport",isReport);
        dao.update(CLASSNAME,"updateFileIsReportById",params);

    }
}
