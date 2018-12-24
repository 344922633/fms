package com.fms.controller.configure;

import com.fms.domain.configure.Property;
import com.fms.service.configure.PropertyService;
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
