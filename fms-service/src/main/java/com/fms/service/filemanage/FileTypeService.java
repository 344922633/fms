package com.fms.service.filemanage;

import com.fms.domain.filemanage.File;
import com.fms.domain.filemanage.FileType;
import com.google.common.collect.Maps;
import com.handu.apollo.base.Page;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import com.handu.apollo.utils.ExtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 文件分类管理服务接口.
 * @author drc
 */
@Service
@Transactional
public class FileTypeService {
    public static final String CLASSNAME = FileTypeService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    /**
     * 查询文件分类list
     * @param params
     * @return
     */
    public Page<FileType> getList(Map<String, Object> params, Page page) {
        return dao.page(CLASSNAME, "query", "queryCount", params, page);
    }

    public List<FileType> query(Map<String, Object> params) {
        return dao.getList(CLASSNAME, "query", params);
    }

    /**
     * 根据文件后缀获取文件分类
     * @param params
     * @return
     */
    public List<FileType> getListBySuffix(Map<String, Object> params) {
        return dao.getList(CLASSNAME, "queryBySuffix",  params);
    }

    /**
     * 根据解析器id获取有哪些分类引用了解析器
     * @id id
     * @return
     */
    public List<FileType> getListByFileParserId(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("fileParserIds", id+"");
        return dao.getList(CLASSNAME, "getListByFileParserId",  params);
    }
    /**
     * 更新文件分类
     * @param fileType 文件分类实体类
     * @return
     */
    public void update(FileType fileType) {
        dao.update(CLASSNAME, "update", fileType);
    }

    /**
     * 删除文件分类
     * @param id 文件分类id
     * @return
     */
    public void delete(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        dao.delete(CLASSNAME, "delete", params);
    }

    /**
     * 更新文件分类
     * @param fileType 文件分类实体类
     * @return
     */
    public void add(FileType fileType) {
        fileType.setId(System.currentTimeMillis());
        dao.insert(CLASSNAME, "add", fileType);
    }

    /**
     * 根据id查询文件分类.
     * @param id 文件分类id
     * @return
     */
    public FileType getById(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        return dao.get(CLASSNAME, "get", params);
    }
    /**
     * 根据name查询文件分类.
     * @param name 文件分类name
     * @return
     */
    public FileType getByName(String name) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("name", name);
        return dao.get(CLASSNAME, "getByName", params);
    }

    /**
     * 根据id查询文件分类.
     * @param fileType 文件分类
     * @return
     */
    public int getCountByName(FileType fileType) {
        return dao.get(CLASSNAME, "getCountByName", fileType);
    }

    /**
     * 验证参数.
     * @param fileType
     * @return
     */
    public String checkParam(FileType fileType) {
        if (fileType == null) {
            return "对象为空！";
        } else if (StringUtils.isEmpty(fileType.getName())) {
            return "分类为空！";
        } else if (StringUtils.isEmpty(fileType.getFileSuffix())) {
            return "文件后缀为空！";
        } else if (StringUtils.isEmpty(fileType.getFileParserIds())) {
            return "解析器为空！";
        }

        return null;
    }
}
