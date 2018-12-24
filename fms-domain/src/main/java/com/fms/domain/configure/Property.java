package com.fms.domain.configure;

import lombok.Data;

@Data
public class Property {
    private Integer id;
    private String bootStrapServers;
    private String groupIdConfig;
    private String hbaseZookeeperQuorum;
    private String defaultTopic;

}
