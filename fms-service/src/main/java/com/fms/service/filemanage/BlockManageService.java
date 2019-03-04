package com.fms.service.filemanage;

import com.fms.domain.filemanage.BlockManage;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 名单管理service
 *
 * @author drc
 */
@Service
@Transactional
public class BlockManageService {
    public static final String CLASSNAME = BlockManageService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    /**
     * 查询名单管理列表
     *
     * @param params
     * @return
     */
    public List<BlockManage> getList(Map<String, Object> params) {
        return dao.getList(CLASSNAME, "getList", params);
    }
    public BlockManage getCountById(long id) {
        return dao.get(CLASSNAME, "getCountById", id);
    }

    /**
     * 根据id更新名单信息
     *
     * @param blockManage
     * @return
     */
    public void updateById(BlockManage blockManage) {
        dao.update(CLASSNAME, "updateById", blockManage);
    }

    /**
     * 根据解析器id更新名单信息
     *
     * @param blockManage
     * @return
     */
    public void updateByParserId(BlockManage blockManage) {
        dao.update(CLASSNAME, "updateByParserId", blockManage);
    }
    public void deleteById(long id) {
        dao.delete(CLASSNAME, "deleteById", id);
    }
    /**
     * 插入名单信息
     *
     * @param block 名单信息
     * @return
//     */
//    public void add(BlockManage block) {
//        long id = 1L;
//        //获取已有信息
//        BlockManage oldBlock = getCountById(id);
//        BlockManage newBlock =new BlockManage();
//            if (!StringUtils.isBlank(block.getBlackContent())) {
//                newBlock.setBlackContent(oldBlock.getBlackContent() + "," + block.getBlackContent());
//            }else{
//                newBlock.setBlackContent(oldBlock.getBlackContent());
//            }
//
//            if (!StringUtils.isBlank(block.getWhiteContent())) {
//                newBlock.setWhiteContent(oldBlock.getWhiteContent() + "," + block.getWhiteContent());
//            }else{
//                newBlock.setWhiteContent(oldBlock.getWhiteContent());
//            }
//        newBlock.setUpdated(new Date());
//        newBlock.setId(1L);
//        deleteById(id);
//        addNewCount(newBlock);
//}

    public void add(BlockManage block) {

        BlockManage oldBlock = get(block);
        if (oldBlock == null) {
            if (block.getId() == null) {
                block.setId(System.currentTimeMillis());
            }
            dao.insert(CLASSNAME, "add", block);
        } else {
            if (!StringUtils.isBlank(block.getBlackContent())) {
                oldBlock.setBlackContent(oldBlock.getBlackContent() == null ? block.getBlackContent() : oldBlock.getBlackContent() + "," + block.getBlackContent());
            }
            if (!StringUtils.isBlank(block.getWhiteContent())) {
                oldBlock.setWhiteContent(oldBlock.getWhiteContent() == null ? block.getWhiteContent() : oldBlock.getWhiteContent() + "," + block.getWhiteContent());
            }
            oldBlock.setUpdated(new Date());
            updateById(oldBlock);
        }

    }




    /**
     * 查询名单信息
     *
     * @param block 名单信息
     * @return
     */
    public BlockManage get(BlockManage block) {
        return dao.get(CLASSNAME, "get", block);
    }

    public void addNewCount(BlockManage newBlock) {
         dao.insert(CLASSNAME, "addNewCount", newBlock);
    }
}
