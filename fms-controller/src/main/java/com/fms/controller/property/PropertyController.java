package com.fms.controller.property;

import com.fms.domain.property.Property;
import com.fms.service.property.PropertyService;
import com.google.common.base.Strings;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @RequestMapping("getAllConfProperty")
    public Object getAllConfProperty() {
        return propertyService.getAllConfProperty();
    }

    @RequestMapping("updateConfProperty")
    public Object updateConfProperty(Property property) {
        property.setId(1);
        propertyService.updateConfProperty(property);
        return "成功";
    }


}
