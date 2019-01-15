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
        Configuration configuration = new Configuration();

//        System.setProperty("hadoop.home.dir", "E:\\01_toole\\01_devLanguage\\03_hadoop\\hadoop-2.9.2");
        return FileSystem.get(new URI(hdfsPath), configuration, hdfsUserName);
    }

}
