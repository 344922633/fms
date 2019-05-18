package com.fms.controller.user;

import com.fms.domain.filemanage.User;
import com.fms.service.filemanage.DirectoryService;
import com.fms.service.user.UserService;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private Environment env;

    @Value("${hdfs.path}")
    private String hdfsPath;

    @Value("${previewPath}")
    private String previewPath;

    @RequestMapping("login")
    public Object login(User user, HttpServletRequest request) {

        String [] paths = {"一级","二级","三级"};
        ReentrantLock reentrantLock = new ReentrantLock();
        Long pId=3L;


        User gUser = userService.queryName(user);

        if(gUser == null){
            return "用户名不存在";
        }

        String psd = user.getPassword();
        String md5Password = MD5.md5for32(psd);

        if(md5Password.equals(gUser.getPassword()) && "1".equals(gUser.getState())){
            //登录标识
            HttpSession session = request.getSession();
            session.setAttribute("user",gUser);

            return "redirect:http://127.0.0.1:8089/dashboard";

        }else{
            return "密码错误或权限不足";
        }
    }

    @ResponseBody
    @RequestMapping("/logout")
    public Object logout(HttpSession session){
        session.invalidate();
        return "退出成功";
    }
    @ResponseBody
    @RequestMapping("addUser")
    public Object addUser(User user){

        User gUser = userService.queryName(user);
        if(gUser!=null && gUser.getId()!=null){

        }else{
            String psd = user.getPassword();
            String md5Password = MD5.md5for32(psd);
            user.setPassword(md5Password);
            user.setCreatd(new Date());
//            user.setState("1");
            userService.add(user);
        }
        return "success";
    }

    /**
     * 获取配置
     * @return
     */
    @Deprecated
    @ResponseBody
    @RequestMapping("getConfig")
    public Object getConfig() {
        Map<String, Object> params = Maps.newHashMap();
//        params.put("fileServerPath", "http://" + env.getProperty("fastdfs.nginxAddress") + ":" + env.getProperty("fastdfs.trackerHttpPort"));
//        params.put("previewPath", env.getProperty("previewPath"));
        params.put("fileServerPath",hdfsPath);
        params.put("previewPath", previewPath);
        return params;
    }
    @ResponseBody
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
