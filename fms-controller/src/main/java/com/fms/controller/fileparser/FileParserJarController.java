package com.fms.controller.fileparser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fms.domain.filemanage.BlockManage;
import com.fms.domain.filemanage.FileParser;
import com.fms.domain.filemanage.FileParserExt;
import com.fms.domain.filemanage.FileParserJar;
import com.fms.service.filemanage.*;
import com.fms.utils.JarLoadUtil;
import com.fms.utils.PakageScanUtil;
import com.fms.utils.ParamUtil;
import com.google.common.base.Strings;
import com.handu.apollo.utils.CollectionUtil;
import com.handu.apollo.utils.ExtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/fileParserJar")
public class FileParserJarController {

    @Autowired
    private FileParserJarService fileParserJarService;

    @Autowired
    private FileParserExtService fileParserExtService;

    @Autowired
    private BlockManageService blockManageService;

    @Autowired
    private Environment env;

    /**
     * 文件解析器service
     */
    @Autowired
    private FileParserService fileParserService;

    @RequestMapping("getJarList")
    public Object getFileParserJar(Map<String, Object> params){
        return fileParserJarService.query(params);
    }
    @RequestMapping("getJarClassAndMethodList")
    public Object getJarClassAndMethodList(String  path){
//        Map<String, Object> params = new HashMap<String,Object>();
//        params.put("id",id);
//       List<FileParserJar> fileParserJarList =  fileParserJarService.query(params);
       Map<String,List<String>> classAndmethodsMap = new HashMap<String,List<String>>();
//       if(fileParserJarList.size()>0){
//           FileParserJar fileParserJar = fileParserJarList.get(0);
//           String path = fileParserJar.getPath();
           if(Strings.isNullOrEmpty(path)){
               return classAndmethodsMap;
           }

           try {
               Path filePath = Paths.get(path);
               if (!Files.exists(filePath)) {
                   return classAndmethodsMap;
               }
               classAndmethodsMap = PakageScanUtil.getJarName(path);

               List<String> classNames = new ArrayList<String>();
               if(classAndmethodsMap!=null){
                Set<String> keyClass = classAndmethodsMap.keySet();
                   for(String className:keyClass){
                       classNames.add(className);
                   }
                   classAndmethodsMap.put("classNames",classNames);
               }

           }catch (Exception e){
               e.printStackTrace();
           }
//       }
       return classAndmethodsMap;
    }

    @RequestMapping("getJarClassParamList")
    public Object getJarClassParamList(String path,String className,String parserId){

        List<FileParserExt> parserExtList = new ArrayList<FileParserExt>();

        try
        {
            JarLoadUtil.loadJar(path);

            Class clz = Class.forName(className);

            //
            Object obj = clz.newInstance();
            //获取方法
            Method m = obj.getClass().getDeclaredMethod("getParmsInfo");

            String  jsonResult = (String) m.invoke(obj);

            JSONArray arrayList= JSONArray.parseArray(jsonResult);

            for(int i=0;i<arrayList.size();i++){
                // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                JSONObject jsonObject = arrayList.getJSONObject(i);
                Iterator<String> it = jsonObject.keySet().iterator();
                if (it.hasNext()){
                    // 获得key
                    String key = it.next();
                    String value = jsonObject.getString(key);


                        FileParserExt parserExt = new FileParserExt();
                        parserExt.setParameterName(key);
                        parserExt.setParameterDesc(value);

                        parserExtList.add(parserExt);
                }
            }

            parserExtList.remove(0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        return parserExtList;
    }

    @RequestMapping("getJarClassParamListById")
    public Object getJarClassParamListById(Long recommendParserId){
        FileParser fileParser = fileParserService.get(recommendParserId);
        List<FileParserExt> parserExtList = new ArrayList<FileParserExt>();
        if (null != fileParser)
        {
            try
            {
                JarLoadUtil.loadJar(fileParser.getSource());

                Class clz = Class.forName(fileParser.getClassName());

                //
                Object obj = clz.newInstance();
                //获取方法
                Method m = obj.getClass().getDeclaredMethod("getParmsInfo");

                String  jsonResult = (String) m.invoke(obj);

                JSONArray arrayList= JSONArray.parseArray(jsonResult);

                for(int i=0;i<arrayList.size();i++){
                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    JSONObject jsonObject = arrayList.getJSONObject(i);
                    Iterator<String> it = jsonObject.keySet().iterator();
                    if (it.hasNext()){
                        // 获得key
                        String key = it.next();
                        String value = jsonObject.getString(key);

                        FileParserExt parserExt = new FileParserExt();
                        parserExt.setParameterName(key);
                        parserExt.setParameterDesc(value);

                        parserExtList.add(parserExt);


                    }
                }

                parserExtList.remove(0);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }


        return parserExtList;
    }

    @RequestMapping(value = "/uploadFileParam", method = RequestMethod.POST)
    public String uploadFileParam(MultipartFile file, HttpServletResponse response) {

        String fileTmp = env.getProperty("fileparam.path") + "/";

        String name = file.getOriginalFilename();

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(fileTmp+name);
            Files.write(path, bytes);

            return fileTmp+name;
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return "后端异常...";
        }

    }


    @RequestMapping("checkJarList")
    public Object checkJarList(HttpServletRequest request){

        String[] fileNameArr = ParamUtil.get(request, "fileNameList", "," ,null);

        String repeatJarNameList = "";
        for(String mf : fileNameArr){
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("name",mf);
            List<FileParserJar> list = fileParserJarService.query(params);
            if(CollectionUtil.isNotEmpty(list)){
                repeatJarNameList=repeatJarNameList+","+mf;
            }
        }
        if(Strings.isNullOrEmpty(repeatJarNameList)){
            return ExtUtil.success("检测完成无重复！");
        }else{
            return ExtUtil.failure("以下jar包已存在是否替换--"+repeatJarNameList.substring(1));
        }

    }
}
