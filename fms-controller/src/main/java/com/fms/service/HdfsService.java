// Copyright (C) 2019 Zaiye
// All rights reserved
package com.fms.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author wujiangtao
 * @version 1.0
 * Created on 2019/1/12 1:35 AM
 **/
@Service
public class HdfsService {

    @Autowired
    private Environment env;

    @Autowired
    private FileSystem fileSystem;

    public String upload(byte[] buffer, String fileName) {
        try {
            String folderPath = env.getProperty("hdfs.file.path");
            Path newFolderPath = new Path(folderPath);
            if (!fileSystem.exists(newFolderPath)) {
                fileSystem.mkdirs(newFolderPath);
            }
            String realPath = folderPath + fileName;
            FSDataOutputStream outputStream =
                    fileSystem.create(new org.apache.hadoop.fs.Path(realPath));
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
            return realPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    public byte[] cat(String realPath) {
        try {
            FSDataInputStream in = fileSystem.open(new Path(realPath));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copyBytes(in, bos, 4096, true);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public void delete(String realPath) {
        try {
            // 第二个参数指定是否要递归删除，false=否，true=是
            fileSystem.delete(new Path(realPath), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mkdir() {
        try {
            // 需要传递一个Path对象
            fileSystem.mkdirs(new Path(env.getProperty("file.tmpPath")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
