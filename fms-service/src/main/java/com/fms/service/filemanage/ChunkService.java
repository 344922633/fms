package com.fms.service.filemanage;

import com.fms.domain.filemanage.upload.Chunk;
import com.google.common.collect.Maps;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 文件分片上传断点续传使用
 */
@Service
@Transactional
public class ChunkService {
    public static final String CLASSNAME = ChunkService.class.getName() + CharPool.PERIOD;

    @Autowired
    private Dao dao;

    /**
     * 查询文件分片信息
     * @param chunk
     * @return
     */
    public Chunk query(Chunk chunk){return dao.get(CLASSNAME, "query", chunk);}

    /**
     *新增分片信息
     * @param chunk
     */
    public void add(Chunk chunk) {
        dao.insert(CLASSNAME, "add", chunk);
    }

    /**
     *删除分片信息
     * @param identifier
     */
    public void delete(String identifier) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("identifier", identifier);
        dao.delete(CLASSNAME, "delete", params);
    }
}
