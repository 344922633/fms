package com.fms.config;

import com.anniweiya.fastdfs.FastDFSTemplate;
import com.anniweiya.fastdfs.FastDFSTemplateFactory;
import com.anniweiya.fastdfs.pool.PoolConfig;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

public class FastdfsConfig {
    @Autowired
    private Environment env;

//    @Bean
    public PoolConfig poolConfig() {
        Integer total = Integer.parseInt(Preconditions.checkNotNull(env.getProperty("fastdfs.poolTotal"), "fastdfs.poolTotal is null"));
        Integer idle = Integer.parseInt(Preconditions.checkNotNull(env.getProperty("fastdfs.poolMaxIdle"), "fastdfs.poolMaxIdle is null"));
        PoolConfig poolConfig = new PoolConfig();
        poolConfig.setMaxTotal(total);
        poolConfig.setMaxIdle(idle);
        return poolConfig;
    }

//    @Bean(initMethod = "init")
    public FastDFSTemplateFactory fastDFSTemplateFactory() throws Exception{
        Integer connectTimeout = Integer.parseInt(Preconditions.checkNotNull(env.getProperty("fastdfs.connectTimeout"), "fastdfs.connectTimeout is null"));
        Integer networkTimeout = Integer.parseInt(Preconditions.checkNotNull(env.getProperty("fastdfs.networkTimeout"), "fastdfs.networkTimeout is null"));
        Integer trackerHttpPort = Integer.parseInt(Preconditions.checkNotNull(env.getProperty("fastdfs.trackerHttpPort"), "fastdfs.trackerHttpPort is null"));
        String trackerServers = Preconditions.checkNotNull(env.getProperty("fastdfs.trackerServers"), "fastdfs.trackerServers is null");
        String nginxAddress = Preconditions.checkNotNull(env.getProperty("fastdfs.nginxAddress"), "fastdfs.nginxAddress is null");
        FastDFSTemplateFactory fastDFSTemplateFactory = new FastDFSTemplateFactory();
        fastDFSTemplateFactory.setG_connect_timeout(connectTimeout);
        fastDFSTemplateFactory.setG_network_timeout(networkTimeout);
        fastDFSTemplateFactory.setPoolConfig(poolConfig());
        fastDFSTemplateFactory.setTracker_servers(trackerServers);
        fastDFSTemplateFactory.setG_tracker_http_port(trackerHttpPort);
        fastDFSTemplateFactory.setNginx_address(nginxAddress);
        fastDFSTemplateFactory.init();
        return fastDFSTemplateFactory;
    }

    @Bean
    public FastDFSTemplate fastDFSTemplate() throws Exception{
        return new FastDFSTemplate(fastDFSTemplateFactory());
    }
}
