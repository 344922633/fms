package com.fms.service.filemanage;

import com.fms.domain.filemanage.Directory;
import com.google.common.collect.Maps;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import com.handu.apollo.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 目录操作
 */
@Service
@Transactional
public class DirectoryService {
    public static final String CLASSNAME = DirectoryService.class.getName() + CharPool.PERIOD;
    private ReentrantLock lock = new ReentrantLock();
    @Autowired
    private Dao dao;

    /**
     * 目录获取
     * @param params
     * @return
     */
    public List<Directory> query(Map<String, Object> params) {
        return dao.getList(CLASSNAME, "query", params);
    }

    /**
     * 新增目录
     * @param dir
     * @return
     */
    public Directory add(Directory dir) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("text", dir.getText());
        params.put("parentId", dir.getParentId());
        try {
            lock.lock();
            List<Directory> dirs = this.query(params);
            if (CollectionUtil.isNotEmpty(dirs)) {
                return dirs.get(0);
            }
            dir.setId(System.currentTimeMillis());
            dao.insert(CLASSNAME, "add", dir);
        } finally {
            lock.unlock();
        }

        return dir;
    }

    /**
     * 修改目录信息
     * @param dir
     * @return
     */
    public Directory update(Directory dir) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("text", dir.getText());
        params.put("parentId", dir.getParentId());
        try {
            lock.lock();
            List<Directory> dirs = this.query(params);
            if (CollectionUtil.isNotEmpty(dirs)) {
                return dirs.get(0);
            }
            dao.update(CLASSNAME, "update", dir);
        } finally {
            lock.unlock();
        }
        return dir;
    }

    /**
     * 删除文件目录
     * @param id
     */
    public void delete(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        dao.delete(CLASSNAME, "delete", params);
    }

    /**
     * 批量删除文件目录
     * @param params
     */
    public void deleteIds(Map<String, Object> params) {
        dao.delete(CLASSNAME, "deleteIds", params);
    }

    /**
     * 批量新增目录
     * @param parentId
     * @param paths
     * @return
     */
    public Long createRelativePath(Long parentId, String[] paths) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("text", paths[0]);
        params.put("parentId", parentId);
        try {
            lock.lock();
            List<Directory> dirs = this.query(params);
            if (CollectionUtil.isNotEmpty(dirs)) {
                Directory dir = dirs.get(0);
                parentId = dir.getId();
                String[] subPaths = new String[paths.length - 1];
                if (subPaths.length == 0) return parentId;
                System.arraycopy(paths, 1, subPaths, 0, subPaths.length);
                return this.createRelativePath(parentId, subPaths);
            } else {
                return this.createNewRelative(parentId, paths);
            }
        } finally {
            lock.unlock();
        }
    }

    private Long createNewRelative(Long parentId, String[] paths) {
        for (String path : paths) {
            Directory dir = new Directory();
            dir.setParentId(parentId);
            dir.setText(path);
            this.add(dir);
            parentId = dir.getId();
        }
        return parentId;
    }
}
