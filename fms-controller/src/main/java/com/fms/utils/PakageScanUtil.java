package com.fms.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PakageScanUtil {
    public static Map<String, List<String>> getJarName(String jarFile) throws Exception {

        Map<String, List<String>> classAndMethodsMap = new HashMap<String,List<String>>();
        try{
            //通过将给定路径名字符串转换为抽象路径名来创建一个新File实例
            File f = new File(jarFile);
            URL url1 = f.toURI().toURL();
            URLClassLoader myClassLoader = new URLClassLoader(new URL[]{url1},Thread.currentThread().getContextClassLoader());

            //通过jarFile和JarEntry得到所有的类
            JarFile jar = new JarFile(jarFile);
            //返回zip文件条目的枚举
            Enumeration<JarEntry> enumFiles = jar.entries();
            JarEntry entry;

            //测试此枚举是否包含更多的元素
            while(enumFiles.hasMoreElements()){
                entry = (JarEntry)enumFiles.nextElement();
                if(entry.getName().indexOf("META-INF")<0){
                    String classFullName = entry.getName();
                    if(!classFullName.endsWith(".class")){
                        classFullName = classFullName.substring(0,classFullName.length()-1);
                    } else{
                        //去掉后缀.class
                        String className = classFullName.substring(0,classFullName.length()-6).replace("/", ".");

                        //打印类名
//                        System.out.println("*****************************");
//                        System.out.println("全类名:" + className);

                        if(className.startsWith("com.caeit.parser")&&className.endsWith("Parser")){
                            List<String> methodsList = new ArrayList<String>();
                            Class<?> myclass = myClassLoader.loadClass(className);
                            //得到类中包含的属性

//                            Method[] methods = myclass.getMethods();
                            Method[] methods = myclass.getDeclaredMethods();
                            for (Method method : methods) {
                                if( Modifier.isPublic(method.getModifiers())){ //public
                                    String methodName = method.getName();
                                    if (methodName.startsWith("parse"))
                                    {
                                        methodsList.add(methodName);
                                    }

//                                    System.out.println("方法名称:" + methodName);
                                }
//                            Class<?>[] parameterTypes = method.getParameterTypes();
//                            for (Class<?> clas : parameterTypes) {
//                                // String parameterName = clas.getName();
//                                String parameterName = clas.getSimpleName();
//                                System.out.println("参数类型:" + parameterName);
//                            }
//                                System.out.println("==========================");
                            }
                            classAndMethodsMap.put(className,methodsList);
                        }



                    }
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return classAndMethodsMap;
    }

    /**
     * 这些默认方法不打印
     */
    private static String DEFAULT_METHOD = "waitequalsnotifynotifyAlltoStringhashCodegetClass";


    public static void main(String[] args) throws Exception {
        //jar包所在路径
            /*getJarName("F:\\user.jar");
            getJarName("F:\\role1.jar");
            getJarName("F:\\role2.jar"); */
        //getJarName("F:\\UserInfo.jar");
        Map<String, List<String>> map = getJarName("/Users/xhb/Downloads/nex xml parser/20180927-TXT文件解析器/01-jar包/TxtParser.jar");

        Set<String> className =  map.keySet();
        for(String cln : className){
            System.out.println(cln);
            List<String> methods = map.get(cln);
            for (String methd : methods){
                System.out.println(methd);
            }
        }
    }
}
