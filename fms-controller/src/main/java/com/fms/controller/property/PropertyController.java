package com.fms.controller.property;

import com.fms.domain.property.Property;
import com.fms.service.property.PropertyService;
import com.fms.utils.Constants;
import com.fms.utils.PropertyUtil;
import com.google.common.base.Strings;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyController {

    @Autowired
    private PropertyService propertyService;


    @RequestMapping("getConfProperty")
    public Property getConfProperty() {
        Property property=new Property();
        property.setPropertySchema(PropertyUtil.readValue("schema"));
        property.setBootStrapServers(PropertyUtil.readValue("BOOTSTRAP_SERVERS")); ;
        property.setDefaultTopic(PropertyUtil.readValue("DEFAULT_TOPIC"));
        property.setGroupIdConfig(PropertyUtil.readValue("GROUP_ID_CONFIG"));
        property.setHbaseZookeeperQuorum(PropertyUtil.readValue("HBASE_ZOOKEEPER_QUORUM"));
        return property;
    }

    @RequestMapping("updateConfProperty")
    public Object updateConfProperty(String id,String BOOTSTRAP_SERVERS,String GROUP_ID_CONFIG, String DEFAULT_TOPIC,String HBASE_ZOOKEEPER_QUORUM,String propertySchema){
        Property property = new Property();
        property.setPropertySchema(propertySchema);
        property.setBootStrapServers(BOOTSTRAP_SERVERS);
        property.setDefaultTopic(DEFAULT_TOPIC);
        property.setGroupIdConfig(GROUP_ID_CONFIG);
        property.setHbaseZookeeperQuorum(HBASE_ZOOKEEPER_QUORUM);
        propertyService.updateConfProperty(property);

        return ExtUtil.success("更新成功！");
    }

}
