package com.fms.service.tuopu;

import com.fms.domain.tuopu.ControlProperty;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ControlPropertyService {
    public static final String CLASSNAME = ControlPropertyService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;



    public void addControlProperty(ControlProperty property){
        dao.insert(CLASSNAME,"addControlProperty",property);
    }


    public List<ControlProperty> getControlPropertyList(Map<String, Object> params) {
        return dao.getList(CLASSNAME, "getControlPropertyList", params);
    }
    /**
     * 删除数据，根据外键
     */
    public void delControlPropertyByControlId(String controlId) {
        dao.delete(CLASSNAME,"delControlPropertyByControlId",controlId);
    }
}
