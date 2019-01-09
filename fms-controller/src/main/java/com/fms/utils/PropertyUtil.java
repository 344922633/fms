package com.fms.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

public class PropertyUtil {
    //项目的根路径
//    static String filepath=System.getProperty("user.dir")+ "/application.properties";;
//    static String profilepath="..\\..\\fms\\fms-controller\\src\\main\\resources\\application.properties";
    static String filepath = System.getProperty("user.dir");
//    filepath.replace('fms-web', 'fms-controller\\src\\main\\resources\\application.properties"');
    static String profilepath = filepath.replace("fms-web","fms-controller\\src\\main\\resources\\application.properties");
//    static String profilepath = "..\\..\\fms\\fms-controller\\src\\main\\resources\\application.properties";
    //    String packagePath =  PropertyUtil.class.getClassLoader().getResourceAsStream("/testcase/test.txt");
//    String packagePath = this.getClass().getResource("application.properties").getPath();
//    static String path = PropertyUtil.class.getResource("/application.properties").getPath();
    /**
     * 采用静态方法
     */
    private static Properties props = new Properties();

    static {
        try {
//            props.load(PropertyUtil.class.getClassLoader().getResourceAsStream("/application.properties"));
            System.out.println(profilepath);
            props.load(new FileInputStream(profilepath));
//            FileInputStream fis = new FileInputStream(new File(path));
//            InputStream is = PropertyUtil.class.getClassLoader().getResourceAsStream("application.properties");
//            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
//            props.load(br);
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
        Properties props = new Properties();
        try {
//            String filepath=System.getProperty("user.dir");
//            String profilepath=filepath+"\\fms-controller\\src\\main\\resources\\application.properties";
//            InputStream is = PropertyUtil.class.getClassLoader().getResourceAsStream("/resources/application.properties");
//            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
//            props.load(br);
//            props.load( new FileInputStream(profilepath));
            System.out.println(profilepath);
            InputStream in = new BufferedInputStream(new FileInputStream(
                    profilepath));
            props.load(in);
            String value = props.getProperty(key);
            System.out.println(key +"键的值是："+ value);
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
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
            OutputStream fos = new FileOutputStream(profilepath);
            props.setProperty(keyname, keyvalue);
            // 以适合使用 load 方法加载到 Properties 表中的格式，
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            props.store(fos, "Update '" + keyname + "' value");
        } catch (IOException e) {
            System.err.println("属性文件更新错误");
        }
    }

    /**
     * 更新properties文件的键值对
     * 如果该主键已经存在，更新该主键的值；
     * 如果该主键不存在，则插件一对键值。
     * @param keyname 键名
     * @param keyvalue 键值
     */
    public void updateProperties(String keyname,String keyvalue) {
        try {
            props.load(new FileInputStream(profilepath));
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
            OutputStream fos = new FileOutputStream(profilepath);
            props.setProperty(keyname, keyvalue);
            // 以适合使用 load 方法加载到 Properties 表中的格式，
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            props.store(fos, "Update '" + keyname + "' value");
        } catch (IOException e) {
            System.err.println("属性文件更新错误");
        }
    }
    //测试代码
    public static void main(String[] args) {
        //当前项目路径
        String filepath=System.getProperty("user.dir");
        String profilepath=filepath+"\\fms-controller\\src\\main\\resources\\application.properties";

        // readValue("MAIL_SERVER_INCOMING");
//        writeProperties("KAFKA", "121");
        System.out.println("文件路径:"+profilepath);
        System.out.println("获取schema: "+props.getProperty("schema"));
        System.out.println("获取schema: " + PropertyUtil.readValue("schema"));
        PropertyUtil.writeProperties("schema","1");
        System.out.println("改schema: "+props.getProperty("schema"));
        PropertyUtil.writeProperties("schema","renzhi_1208");
        System.out.println("复原schema: " + PropertyUtil.readValue("schema"));

    }
}
