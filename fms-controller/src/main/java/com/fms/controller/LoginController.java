package com.fms.controller;

import com.anniweiya.fastdfs.FastDFSTemplate;
import com.fms.domain.filemanage.Directory;
import com.fms.domain.filemanage.File;
import com.fms.domain.filemanage.User;
import com.fms.service.filemanage.DirectoryService;
import com.fms.service.filemanage.FileService;
import com.fms.service.user.UserService;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FastDFSTemplate fastDFSTemplate;

    @Autowired
    private Environment env;

    @RequestMapping("login")
    public Object login(User user) {
//        userService.getUser();
        /*Directory dir = new Directory();
        dir.setText("根目录");
        dir.setDescription("根目录");
        dir.setLevel(1);
        dir.setParentId(0L);
        dir.setCode("root");
        dir.setLeaf(false);
        directoryService.add(dir);
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentId", 0);
        List<Directory> dirs = directoryService.query(params);
        dir = dirs.get(0);
        dir.setDescription("根目录了");
        directoryService.update(dir);
        directoryService.delete(dir.getId());*/
//        File f = new File();
//        f.setId(System.currentTimeMillis());
//        f.setName("a.txt");
//        f.setRealPath("a/b/a.txt");
//        f.setTag("ok");
//        f.setType("txt");
//        fileService.add(f);
//        List<File> files = fileService.query(null);
//        f = files.get(0);
//        f.setDescription("en heng");
//        fileService.update(f);
//        fileService.delete(f.getId());
//<<<<<<< U
        String [] paths = {"一级","二级","三级"};
        ReentrantLock reentrantLock = new ReentrantLock();
        Long pId=3L;
        System.out.println(directoryService.createRelativePath(pId,paths));
//=======

        User gUser = userService.queryName(user);

        String psd = user.getPassword();
        String md5Password = MD5.md5for32(psd);

        if(md5Password.equals(gUser.getPassword()) && "1".equals(gUser.getState())){
            return "success";
        }

        return "fail";
    }

    @RequestMapping("addUser")
    public Object addUser(User user){

        User gUser = userService.queryName(user);
        if(gUser!=null && gUser.getId()!=null){

        }else{
            String psd = user.getPassword();
            String md5Password = MD5.md5for32(psd);
            user.setPassword(md5Password);
            user.setCreatd(new Date());
            user.setState("1");
            userService.add(user);
        }
        return "success";
    }
    @RequestMapping("getConfig")
    public Object getConfig() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("fileServerPath", "http://" + env.getProperty("fastdfs.nginxAddress") + ":" + env.getProperty("fastdfs.trackerHttpPort"));
        params.put("previewPath", env.getProperty("previewPath"));
        return params;
    }
    @RequestMapping("updateUser")
    public Object updateUser(User user){

        User gUser = userService.queryId(user);
        if(gUser==null ){

        }else{
            String psd = user.getPassword();
            if(!Strings.isNullOrEmpty(psd)){
                String md5Password = MD5.md5for32(psd);
                gUser.setPassword(md5Password);
            }

            String nState = user.getState();
            if(!Strings.isNullOrEmpty(nState)){
                gUser.setState(nState);
            }

            userService.update(gUser);
        }
        return ExtUtil.success("更新成功！");
    }
}
