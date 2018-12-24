package com.fms.service.configure;

import com.fms.domain.configure.Property;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PropertyService {
    public static final String CLASSNAME = PropertyService.class.getName() + CharPool.PERIOD;


    @Autowired
    private Dao dao;

    public Property getAllConfProperty() {
        return dao.get(CLASSNAME, "getAllConfProperty", 1);
    }

    public void updateConfProperty(Property property) {
        dao.update(CLASSNAME, "updateConfProperty", property);
    }
}