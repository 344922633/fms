package com.fms.domain.property;

import lombok.Data;

@Data
public class Property {
    private Integer id;
    private String bootStrapServers;
    private String groupIdConfig;
    private String hbaseZookeeperQuorum;
    private String defaultTopic;

}
