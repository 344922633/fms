package com.fms.service.masterSlave;

import com.fms.domain.filemanage.MasterSlaveDo;
import com.handu.apollo.base.Page;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MasterSlaveService {
    public static final String CLASSNAME = MasterSlaveService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    public List<MasterSlaveDo> query(MasterSlaveDo masterSlaveDo) {
        return dao.getList(CLASSNAME, "query", masterSlaveDo);
    }

    public Page<MasterSlaveDo> page(MasterSlaveDo masterSlaveDo, Page page) {
        return dao.page(CLASSNAME, "query", "queryCount", masterSlaveDo, page);
    }

    public void add(MasterSlaveDo masterSlaveDo) {
        dao.insert(CLASSNAME, "add", masterSlaveDo);
    }

    public void update(MasterSlaveDo masterSlaveDo) {
        dao.update(CLASSNAME, "update", masterSlaveDo);
    }

    public void delete(String id) {
        dao.delete(CLASSNAME, "delete", id);
    }

    public List<String> queryType() {
        return dao.getList(CLASSNAME, "queryType", null);
    }

    public Map findMasterSlaveById(Integer id) {
        return dao.get(CLASSNAME,"findMasterSlaveById",id);
    }
}
