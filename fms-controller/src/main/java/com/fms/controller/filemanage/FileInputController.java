package com.fms.controller.filemanage;

import org.springframework.beans.factory.annotation.Autowired;
import com.fms.domain.filemanage.FileInput;
import com.fms.service.filemanage.FileInputService;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fileInput")
public class FileInputController {
    @Autowired
    private FileInputService fileInputService;
    @Autowired
    private Environment env;

    @RequestMapping("getList")
    public Object getList(Map<String, Object> params) {
        return fileInputService.getList(params);
    }


    @RequestMapping("update")
    public void update(@RequestParam Map<String, Object> params) {
        String id = (String) params.get("id");
        String ip = (String) params.get("ip");
        String userName = (String) params.get("userName");
        String password = (String) params.get("password");
        String port = (String) params.get("port");
        String path = (String) params.get("path");
        String format = (String) params.get("format");
        FileInput fileInput = fileInputService.queryId(Long.parseLong(id));
        fileInput.setIp(ip);
        fileInput.setUserName(userName);
        fileInput.setPassword(password);
        fileInput.setPath(path);
        fileInput.setFormat(format);
        fileInput.setPort(port);

        fileInputService.update(fileInput);
    }

    @RequestMapping("get")
    public Object get(String id) {
        return fileInputService.get((Long.parseLong(id)));
    }

    @RequestMapping("add")
    public void add(@RequestParam Map<String, Object> params) {
        String ip = (String) params.get("ip");
        String userName = (String) params.get("userName");
        String password = (String) params.get("password");
        String port = (String) params.get("port");
        String path = (String) params.get("path");
        String format = (String) params.get("format");
        FileInput fileInput = new FileInput();
        fileInput.setIp(ip);
        fileInput.setUserName(userName);
        fileInput.setPassword(password);
        fileInput.setPort(port);
        fileInput.setPath(path);
        fileInput.setFormat(format);
        fileInputService.add(fileInput);

    }

    @RequestMapping("delete")
    public void delete(String id) {
        fileInputService.delete(Long.parseLong(id));
    }
}
