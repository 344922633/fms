package com.fms.controller.filemanage;

import com.fms.domain.filemanage.FileReport;
import com.fms.service.filemanage.FileReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fileReport")
public class FileReportController {
    @Autowired
    private FileReportService fileReportService;
    @Autowired
    private Environment env;

    @RequestMapping("getList")
    public Object getList(Map<String, Object> params) {
        return fileReportService.getList(params);
    }


    @RequestMapping("update")
    public void update(@RequestParam Map<String, Object> params) {
        String id = (String) params.get("id");
        String ip = (String) params.get("ip");
        String userName = (String) params.get("userName");
        String password = (String) params.get("password");
        String port = (String) params.get("port");
        String path = (String) params.get("path");
        FileReport fileReport = fileReportService.queryId(Long.parseLong(id));
        fileReport.setIp(ip);
        fileReport.setUserName(userName);
        fileReport.setPassword(password);
        fileReport.setPath(path);
        fileReport.setPort(port);

        fileReportService.update(fileReport);
    }

    @RequestMapping("get")
    public Object get(String id) {
        return fileReportService.get((Long.parseLong(id)));
    }

    @RequestMapping("add")
    public void add(@RequestParam Map<String, Object> params) {
        String ip = (String) params.get("ip");
        String userName = (String) params.get("userName");
        String password = (String) params.get("password");
        String port = (String) params.get("port");
        String path = (String) params.get("path");
        FileReport fileReport = new FileReport();
        fileReport.setIp(ip);
        fileReport.setUserName(userName);
        fileReport.setPassword(password);
        fileReport.setPort(port);
        fileReport.setPath(path);
        fileReportService.add(fileReport);

    }

    @RequestMapping("delete")
    public void delete(String id) {
        fileReportService.delete(Long.parseLong(id));
    }
}
