package com.fms.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;
import java.util.Map;

public class Util {
    public static String invoke(String name) {
        System.out.println(name);
        return name;
    }


    /**
     * 删除文件及其子目录
     * @param file
     * @return
     */
    public static void delFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    delFile(f);
                }
            }
            file.delete();
        }
    }

}
