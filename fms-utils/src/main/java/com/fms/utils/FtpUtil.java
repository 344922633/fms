package com.fms.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caeit.parser.xml.XmlParser;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.kafka.core.KafkaTemplate;

public class FtpUtil {


    private static FTPClient ftp;

    /**
     * 获取ftp连接
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static boolean connectFtp(Ftp f) throws Exception {
        ftp = new FTPClient();
        boolean flag = false;
        int reply;
        if (f.getPort() == null) {
            ftp.connect(f.getIpAddr(), 21);
        } else {
            ftp.connect(f.getIpAddr(), f.getPort());
        }
        ftp.login(f.getUserName(), f.getPwd());
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return flag;
        }
        ftp.changeWorkingDirectory(f.getPath());
        flag = true;
        return flag;
    }

    /**
     * 关闭ftp连接
     */
    public static void closeFtp() {
        if (ftp != null && ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ftp上传文件
     *
     * @param f
     * @throws Exception
     */
    public static void upload(File f) throws Exception {
        if (f.isDirectory()) {
            ftp.makeDirectory(f.getName());
            ftp.changeWorkingDirectory(f.getName());
            String[] files = f.list();
            for (String fstr : files) {
                File file1 = new File(f.getPath() + "/" + fstr);
                if (file1.isDirectory()) {
                    upload(file1);
                    ftp.changeToParentDirectory();
                } else {
                    File file2 = new File(f.getPath() + "/" + fstr);
                    FileInputStream input = new FileInputStream(file2);
                    ftp.storeFile(file2.getName(), input);
                    input.close();
                }
            }
        } else {
            File file2 = new File(f.getPath());
            FileInputStream input = new FileInputStream(file2);
            ftp.storeFile(file2.getName(), input);
            input.close();
        }
    }

    /**
     * 下载链接配置
     *
     * @param f
     * @param localBaseDir  本地目录
     * @param remoteBaseDir 远程目录
     * @throws Exception
     */
    public static void startDown(Ftp f, String localBaseDir, String remoteBaseDir) throws Exception {
        if (FtpUtil.connectFtp(f)) {

            try {
                FTPFile[] files = null;
                boolean changedir = ftp.changeWorkingDirectory(remoteBaseDir);
                if (changedir) {
                    ftp.setControlEncoding("GBK");
                    files = ftp.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        try {
                            downloadFile(files[i], localBaseDir, remoteBaseDir);
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (Exception e) {
            }
        } else {
        }

    }


    public static String handleFile(KafkaTemplate kafka, File file) throws Exception {
        String jsonStr = "";
        try {
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            //JSONArray kafkaArray = new JSONArray();
            JSONObject obj = new JSONObject();
            JSONObject obj1 = new JSONObject();
            JSONObject obj2 = new JSONObject();
            JSONArray data = new JSONArray();
            JSONArray columns = new JSONArray();

            obj.put("operationSource", "HuiJu_PLATFORM");
            obj1.put("operationType", "INSERT");
            obj1.put("objectCode", "dxbm");
            obj1.put("objectCodeValue", "sbqg002");
            obj1.put("columns", columns);
            columns.add(obj2);

            if (suffix.equals("xml")) {
                Map<String, String> map = new XmlParser().parseXml(file);
                String outPut = map.get("jsonBottomLevel");
                System.out.println(outPut);
                JSONObject outPutJson = JSONObject.parseObject(outPut);
                Iterator<String> it = outPutJson.keySet().iterator();
                if (it.hasNext()) {
                    String key = it.next();// entitys entity
                    JSONArray array = outPutJson.getJSONArray(key);
                   // JSONArray array = JSONArray.parseArray(tableStr);
                            /*    String schema="";
                                if(jsonObject.containsKey("schema")){
                                    schema= jsonObject.getString("schema");
                                }
                                String table="";
                                if(jsonObject.containsKey("table")){
                                    table= jsonObject.getString("table");
                                }

                                obj1.put("schema", schema);
                                obj1.put("table", table);*/

                    for (int i = 0; i < array.size(); i++) {
                        columns.clear();
                        JSONObject jsonObject = array.getJSONObject(i);
                        Iterator<String> colIt = jsonObject.keySet().iterator();
                        while (colIt.hasNext()) {
                            String jsonKey = colIt.next();
                            JSONObject jsonCol = new JSONObject();
                            jsonCol.put("name", jsonKey);
                            jsonCol.put("value", jsonObject.get(jsonKey));
                            columns.add(jsonCol);
                        }
                        data.add(obj1);
                        obj.put("data", data);

                        kafka.send("operation_3rd3", obj.toJSONString());

                        //kafkaArray.add(obj);
                        System.out.println(obj);

                    }
                }
            }
                      /*     else if (suffix.equals("json")) {


                                Map<String, String> map = new JsonParser().parseJson(new File("D:\\JsonParser_testFile.json"));
                                String outPut = map.get("jsonBottomLevel");
                            }*/
                       /*     else if (suffix.equals("xls") || suffix.equals("xlsx")) {
                                Map<String, String> map = new ExcelParser().parseExcel(new File("D:\\XlsParser_testFile.xls"), true); //true 表示是否行排列，false表示列排列，目前仅支持行排列即可
                                for (int j = 0; j < map.size(); j++) {
                                    String outPut = map.get("");
                                    JSONObject jsonObject = JSONObject.parseObject(outPut);

                                    JSONArray array = JSON.parseArray(jsonObject.get("").toString());
                                    JSONObject jo = JSON.parseObject(array.get(0).toString());
                                    JSONObject DXBM = jo.getJSONObject("DXBM");
                                    JSONObject SBMC = jo.getJSONObject("SBMC");
                                    JSONObject SBLX = jo.getJSONObject("SBLX");
                                    JSONObject JBXX = jo.getJSONObject("JBXX");
                                    JSONObject KSZT = jo.getJSONObject("KSZT");
                                    JSONObject SCCK = jo.getJSONObject("SCCK");
                                    JSONObject SLSJ = jo.getJSONObject("SLSJ");
                                    columns.add(DXBM);
                                    columns.add(SBMC);
                                    columns.add(SBLX);
                                    columns.add(JBXX);
                                    columns.add(KSZT);
                                    columns.add(SCCK);
                                    columns.add(SLSJ);
                                   obj1.put("schema", schema);
                                    obj1.put("table", table);
                                    data.add(obj1);
                                }
                            }*/
            //jsonStr = kafkaArray.toJSONString();
        } catch (Exception e) {
        }
        return jsonStr;
    }

    /**
     * 下载FTP文件
     * 当你需要下载FTP文件的时候，调用此方法
     * 根据<b>获取的文件名，本地地址，远程地址</b>进行下载
     *
     * @param ftpFile
     * @param relativeLocalPath
     * @param relativeRemotePath
     */
    private static void downloadFile(FTPFile ftpFile, String relativeLocalPath, String relativeRemotePath) {
        if (ftpFile.isFile()) {
            if (ftpFile.getName().indexOf("?") == -1) {
                OutputStream outputStream = null;
                try {
                    File locaFile = new File(relativeLocalPath + ftpFile.getName());
                    //判断文件是否存在，存在则返回
                    if (locaFile.exists()) {
                        return;
                    } else {
                        outputStream = new FileOutputStream(relativeLocalPath + ftpFile.getName());
                        ftp.retrieveFile(ftpFile.getName(), outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (Exception e) {
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        } else {
            String newlocalRelatePath = relativeLocalPath + ftpFile.getName();
            String newRemote = new String(relativeRemotePath + ftpFile.getName().toString());
            File fl = new File(newlocalRelatePath);
            if (!fl.exists()) {
                fl.mkdirs();
            }
            try {
                newlocalRelatePath = newlocalRelatePath + '/';
                newRemote = newRemote + "/";
                String currentWorkDir = ftpFile.getName().toString();
                boolean changedir = ftp.changeWorkingDirectory(currentWorkDir);
                if (changedir) {
                    FTPFile[] files = null;
                    files = ftp.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        downloadFile(files[i], newlocalRelatePath, newRemote);
                    }
                }
                if (changedir) {
                    ftp.changeToParentDirectory();
                }
            } catch (Exception e) {
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Ftp f = new Ftp();
        f.setIpAddr("114.115.177.163");
        f.setUserName("root");
        f.setPwd("Hosting@2018");
        f.setPort(22);
        FtpUtil.connectFtp(f);
        File file = new File("F:/test/com/test/Testng.java");
        FtpUtil.upload(file);//把文件上传在ftp上
        FtpUtil.startDown(f, "e:/", "/xxtest");//下载ftp文件测试
        System.out.println("ok");

    }

}