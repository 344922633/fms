package com.fms.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fms.domain.tuopu.Control;
import com.fms.domain.tuopu.ControlProperty;
import com.fms.service.tuopu.ControlPropertyService;
import com.fms.service.tuopu.ControlService;
import com.fms.utils.Constants;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/confProperty")
public class ConfPropertyController {
    @Autowired
    private ControlPropertyService controlPropertyService;

    @Autowired
    private Environment env;


    @RequestMapping("/applicationConf")
    public JSONObject confProperty() {
        JSONObject obj=new JSONObject();
        try {
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.setEncoding("UTF-8");
            config.load("application.properties");

            String parserPath=(String) config.getProperty("parser.path");
            String fileTmpPath=(String) config.getProperty("file.tmpPath");
            String url=(String) config.getProperty("db.url");
            String fileUploadPath=(String) config.getProperty("fileUploadPath");
            String posyspath=(String) config.getProperty("posyspath");
            String popassword=(String) config.getProperty("popassword");
            String poolTotal=(String) config.getProperty("fastdfs.poolTotal");
            String poolMaxIdle=(String) config.getProperty("fastdfs.poolMaxIdle");
            String connectTimeout=(String) config.getProperty("fastdfs.connectTimeout");
            String networkTimeout=(String) config.getProperty("fastdfs.networkTimeout");
            String trackerHttpPort=(String) config.getProperty("fastdfs.trackerHttpPort");
            obj.put("parserPath",parserPath);
            obj.put("fileTmpPath",fileTmpPath);
            obj.put("url",url);
            obj.put("fileUploadPath",fileUploadPath);
            obj.put("posyspath",posyspath);
            obj.put("popassword",popassword);
            obj.put("poolTotal",poolTotal);
            obj.put("poolMaxIdle",poolMaxIdle);
            obj.put("connectTimeout",connectTimeout);
            obj.put("networkTimeout",networkTimeout);
            obj.put("trackerHttpPort",trackerHttpPort);
            System.out.println(obj.toJSONString());
        } catch(ConfigurationException cex)
            {
                System.err.println("loading of the configuration file failed");
            }

        return obj;
    }


    @RequestMapping("/updateApplicationConf")
    public void updateApplicationConf(HttpServletRequest request) {
        String parserPath=request.getParameter("parserPath");
        String fileTmpPath=request.getParameter("fileTmpPath");
        String url=request.getParameter("url");
        String fileUploadPath=request.getParameter("fileUploadPath");
        String posyspath=request.getParameter("posyspath");
        String popassword=request.getParameter("popassword");
        String poolTotal=request.getParameter("poolTotal");
        String poolMaxIdle=request.getParameter("poolMaxIdle");
        String connectTimeout=request.getParameter("connectTimeout");
        String networkTimeout=request.getParameter("networkTimeout");
        String trackerHttpPort=request.getParameter("trackerHttpPort");
        String profilepath = "src/main/resources/application.properties";
        try
        {
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.setEncoding("UTF-8");
            config.load("application.properties");

            config.setAutoSave(true);

            config.setProperty("parser.path", parserPath);
            config.setProperty("file.tmpPath", fileTmpPath);
            config.setProperty("db.url", url);
            config.setProperty("fileUploadPath", fileUploadPath);
            config.setProperty("posyspath", posyspath);
            config.setProperty("popassword", popassword);
            config.setProperty("fastdfs.poolTotal", poolTotal);
            config.setProperty("fastdfs.poolMaxIdle", poolMaxIdle);
            config.setProperty("fastdfs.connectTimeout", connectTimeout);
            config.setProperty("fastdfs.networkTimeout", networkTimeout);
            config.setProperty("fastdfs.trackerHttpPort", trackerHttpPort);

            //PropertiesConfiguration config  = new PropertiesConfiguration(profilepath);
            config.setReloadingStrategy(new FileChangedReloadingStrategy());
            System.out.println(config.getString("fastdfs.trackerHttpPort"));
        }
        catch(ConfigurationException cex)
        {
            System.err.println("loading of the configuration file failed");
        }
    }

    @RequestMapping("/kafkaHbaseConf")
    public JSONObject kafkaConf() {
        JSONObject obj = new JSONObject();
        String BOOTSTRAP_SERVERS= Constants.BOOTSTRAP_SERVERS;
        String GROUP_ID_CONFIG= Constants.GROUP_ID_CONFIG;
        String DEFAULT_TOPIC = Constants.DEFAULT_TOPIC;
        String HBASE_ZOOKEEPER_QUORUM = Constants.HBASE_ZOOKEEPER_QUORUM;

        obj.put("BOOTSTRAP_SERVERS",BOOTSTRAP_SERVERS);
        obj.put("GROUP_ID_CONFIG",GROUP_ID_CONFIG);
        obj.put("DEFAULT_TOPIC",DEFAULT_TOPIC);
        obj.put("HBASE_ZOOKEEPER_QUORUM",HBASE_ZOOKEEPER_QUORUM);

        return obj;
    }

    @RequestMapping("/updateKafkaHbaseConf")
    public void updateKafkaConf(HttpServletRequest request) {
        String BOOTSTRAP_SERVERS=request.getParameter("BOOTSTRAP_SERVERS");
        String GROUP_ID_CONFIG=request.getParameter("GROUP_ID_CONFIG");
        String DEFAULT_TOPIC=request.getParameter("DEFAULT_TOPIC");
        String HBASE_ZOOKEEPER_QUORUM=request.getParameter("HBASE_ZOOKEEPER_QUORUM");
        String profilepath = "/fms/Constants";
        try
        {
            PropertiesConfiguration config  = new PropertiesConfiguration(profilepath);
            config.setAutoSave(true);
            config.setProperty("BOOTSTRAP_SERVERS", BOOTSTRAP_SERVERS);
            config.setProperty("GROUP_ID_CONFIG", GROUP_ID_CONFIG);
            config.setProperty("DEFAULT_TOPIC", DEFAULT_TOPIC);
            config.setProperty("HBASE_ZOOKEEPER_QUORUM", HBASE_ZOOKEEPER_QUORUM);

        }
        catch(ConfigurationException cex)
        {
            System.err.println("loading of the configuration file failed");
        }
    }

}