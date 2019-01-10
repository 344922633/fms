package com.fms.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

public class PropertyUtil {

    static String fileName = "/application.properties"; // /不能少

    /**
     * 采用静态方法
     */
    private static Properties props = new Properties();

    static {
        try {
            InputStream in = PropertyUtil.class.getResourceAsStream(fileName);
//            InputStream in = new BufferedInputStream (new FileInputStream(fileName));
            props.load(in);     ///加载属性列表
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.exit(-1);
        }
    }

    /**
     * 读取属性文件中相应键的值
     * @param key
     *            主键
     * @return String
     */
    public static String getKeyValue(String key) {
        return props.getProperty(key);
    }

    /**
     * 根据主键key读取主键的值value
     * @param key 键名
     */
    public static String readValue(String key) {
//        Properties props = new Properties();
        try {
//            InputStream in = PropertyUtil.class.getResourceAsStream(fileName);
//            props.load(in);     ///加载属性列表
            String value = props.getProperty(key);
            System.out.println(key +"键的值是："+ value);
//            in.close();
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新（或插入）一对properties信息(主键及其键值)
     * 如果该主键已经存在，更新该主键的值；
     * 如果该主键不存在，则插件一对键值。
     * @param keyname 键名
     * @param keyvalue 键值
     */
    public static void writeProperties(String keyname,String keyvalue) {
        try {
//            Properties props = new Properties();
            ///保存属性到b.properties文件
            FileOutputStream oFile = new FileOutputStream(fileName,true);//true表示追加打开
//            System.out.println(oFile.);
            props.setProperty(keyname, keyvalue);
            props.store(oFile, "Update '" + keyname + "' value");
            oFile.close();
//            System.out.println(fileName);

        } catch (IOException e) {
            System.err.println("属性文件更新错误");
        }
    }

    //    /**
//     * 更新properties文件的键值对
//     * 如果该主键已经存在，更新该主键的值；
//     * 如果该主键不存在，则插件一对键值。
//     * @param keyname 键名
//     * @param keyvalue 键值
//     */
//    public void updateProperties(String keyname,String keyvalue) {
//        try {
//            props.load(new FileInputStream(profilepath));
//            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
//            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
//            OutputStream fos = new FileOutputStream(profilepath);
//            props.setProperty(keyname, keyvalue);
//            // 以适合使用 load 方法加载到 Properties 表中的格式，
//            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
//            props.store(fos, "Update '" + keyname + "' value");
//        } catch (IOException e) {
//            System.err.println("属性文件更新错误");
//        }
//    }
    //测试代码
    public static void main(String[] args) {

//        System.out.println("获取schema: "+props.getProperty("schema"));
        System.out.println("获取schema: " + PropertyUtil.readValue("schema"));
        PropertyUtil.writeProperties("schema","1");
        System.out.println("改schema: "+props.getProperty("schema"));
        System.out.println("改后schema: "+PropertyUtil.readValue("schema"));
        PropertyUtil.writeProperties("schema","renzhi_1208");
        System.out.println("复原schema: " + PropertyUtil.readValue("schema"));

    }
}
