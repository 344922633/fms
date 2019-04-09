// Copyright (C) 2019 Zaiye
// All rights reserved
package com.fms.config;

import com.google.common.base.Preconditions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.net.URI;

/**
 * @author wujiangtao
 * @version 1.0
 * Created on 2019/1/8 11:53 PM
 **/
public class HdfsConfig {

    @Autowired
    private Environment env;

    @Bean
    public FileSystem fileSystem() throws Exception {
        String hdfsPath = Preconditions.checkNotNull(env.getProperty("hdfs.path"), "hdfs.path is null");
        String hdfsUserName = Preconditions.checkNotNull(env.getProperty("hdfs.user.name"), "hdfs.user.name is null");
        String defaultFS = Preconditions.checkNotNull(env.getProperty("fs.defaultFS"),"fs.defaultFS is null");
        String nameService = Preconditions.checkNotNull(env.getProperty("dfs.nameservices"),"dfs.nameservices is null");
        String mycluster = Preconditions.checkNotNull(env.getProperty("dfs.ha.namenodes.mycluster"),"dfs.ha.namenodes.mycluster is null");
        String nn1 = Preconditions.checkNotNull(env.getProperty("dfs.namenode.rpc-address.mycluster.nn1"),"dfs.namenode.rpc-address.mycluster.nn1 is null");
        String nn2 = Preconditions.checkNotNull(env.getProperty("dfs.namenode.rpc-address.mycluster.nn2"),"dfs.namenode.rpc-address.mycluster.nn2 is null");
        String providerMucluster = Preconditions.checkNotNull(env.getProperty("dfs.client.failover.proxy.provider.mycluster"),"dfs.client.failover.proxy.provider.mycluster is null");
        Configuration configuration = new Configuration();
        System.setProperty("HADOOP_USER_NAME", hdfsUserName);
        configuration.set("fs.defaultFS", defaultFS);
        configuration.set("dfs.nameservices", nameService);
        configuration.set("dfs.ha.namenodes.mycluster", mycluster);
        configuration.set("dfs.namenode.rpc-address.mycluster.nn1", nn1);
        configuration.set("dfs.namenode.rpc-address.mycluster.nn2", nn2);
        configuration.set("dfs.client.failover.proxy.provider.mycluster", providerMucluster);
//        return FileSystem.get(new URI(hdfsPath), configuration, hdfsUserName);
        return FileSystem.get(configuration);
    }

}
