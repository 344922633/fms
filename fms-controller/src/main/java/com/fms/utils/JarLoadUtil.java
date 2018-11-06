package com.fms.utils;


import com.handu.apollo.utils.exception.ApolloRuntimeException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class JarLoadUtil {
    public static void loadJar(String jarPath) {
        File jarFile = new File(jarPath);
        // 从URLClassLoader类中获取类所在文件夹的方法，jar也可以认为是一个文件夹
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException | SecurityException e1) {
            e1.printStackTrace();
        }
        // 获取方法的访问权限以便写回
        boolean accessible = method.isAccessible();
        try {
            method.setAccessible(true);
            // 获取系统类加载器
//            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            URLClassLoader classLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
            URL url = jarFile.toURI().toURL();
            method.invoke(classLoader, url);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            method.setAccessible(accessible);
        }
    }

    public static Object execute(String jarPath, String fullClassName, String methodName, Class[] paramClass, Object[] params) {
        Class<?> aClass = null;
        try {
            aClass = Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            loadJar(jarPath);
            try {
                aClass = Class.forName(fullClassName);
            } catch (ClassNotFoundException e1) {
                throw new ApolloRuntimeException("该解析器不存在！!");
            }
        }
        if (aClass == null) {
            throw new ApolloRuntimeException("该解析器不存在！");
        }
        try {
            Object instance = aClass.newInstance();
            Method method = aClass.getDeclaredMethod(methodName, paramClass);
            method.setAccessible(true);
            return method.invoke(instance, params);
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
//        String jarPath = "/Users/fencho/Documents/H/LdifParser.jar";
        String jarPath = "D:\\Users\\xhb\\Documents\\tmp\\DbfParser.jar";
//        File fileparser = new File("/Users/fencho/Documents/0-测试文件/1.ldif");
        File file = new File("D:\\Users\\xhb\\Documents\\tmp\\dbf.dbf");
        Object obj = execute(jarPath,"com.caeit.parser.dbf.DbfParser", "readSingle", new Class[] {File.class}, new Object[] { file });
        System.out.println(obj);
    }
}
