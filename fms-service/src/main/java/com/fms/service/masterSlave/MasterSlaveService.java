package com.fms.service.masterSlave;

import com.fms.domain.filemanage.MasterSlaveDo;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasterSlaveService {
    public static final String CLASSNAME = MasterSlaveService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    public List<MasterSlaveDo> query(MasterSlaveDo masterSlaveDo) {
        return dao.getList(CLASSNAME, "query", masterSlaveDo);
    }

    public List<String> queryType() {
        return dao.getList(CLASSNAME, "query", null);
    }
}
