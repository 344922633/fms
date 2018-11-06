package com.fms;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        System.out.println("123.txt".substring("123.txt".lastIndexOf(".")));
        String[] a = new String[]{"a", "b", "c", "d"};
        String[] b = new String[a.length - 1];
        System.arraycopy(a, 1, b, 0 , b.length);
        System.out.println(b.toString());
        File f = new File("/Users/fencho/Downloads/cn_windows_10_multiple_editions_version_1607_updated_jul_2016_x64_dvd_9056935.iso");
        System.out.println(f.getName());
    }
}
