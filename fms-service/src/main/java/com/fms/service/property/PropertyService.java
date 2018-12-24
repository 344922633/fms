package com.fms.service.property;

import com.fms.domain.filemanage.MasterSlaveDo;
import com.fms.domain.property.Property;
import com.fms.service.schema.SchemaService;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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