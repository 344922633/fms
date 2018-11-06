package com.fms.utils;
 
import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
 
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
 

public class SFTPUtils {
 
 
    private static  String host;
    private static  int port;
    private static  String username;
    private static  String password;
//    public static  String directory = "/usr/local/test";
 
    private static ChannelSftp sftp;
 
    private static SFTPUtils instance = null;
 
    private SFTPUtils() {
    }
 
    public static SFTPUtils getInstance(Ftp ftp) {
    	host = ftp.getIpAddr();
    	port = ftp.getPort();
    	username = ftp.getUserName();
    	password = ftp.getPwd();
        if (instance == null) {
            if (instance == null) {
                instance = new SFTPUtils();
                sftp = instance.connect(host, port, username, password);   //获取连接
            }
        }
        return instance;
    }
 
    /**
     * 连接sftp服务器
     *
     * @param host     主机
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public ChannelSftp connect(String host, int port, String username, String password) {
        ChannelSftp sftp = null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            Session sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (Exception e) {
        }
        return sftp;
    }
 
    /**
     * 上传文件
     *
     * @param directory  上传的目录
     * @param uploadFile 要上传的文件
     */
    public boolean upload(String directory, String uploadFile) {
        try {
            sftp.cd(directory);
            File file = new File(uploadFile);
            FileInputStream fileInputStream = new FileInputStream(file);
            sftp.put(fileInputStream, file.getName());
            fileInputStream.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
 
    /**
     * 下载文件
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     */
    public File download(String directory, String downloadFile, String saveFile) {
        try {
            sftp.cd(directory);
            File file = new File(saveFile);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            sftp.get(downloadFile, fileOutputStream);
            fileOutputStream.close();
            return file;
        } catch (Exception e) {
            return null;
        }
    }
 
    /**
     * 下载文件
     *
     * @param downloadFilePath 下载的文件完整目录
     * @param saveFile     存在本地的路径
     */
    public File download(String downloadFilePath, String saveFile) {
        try {
            int i = downloadFilePath.lastIndexOf('/');
            if (i == -1)
                return null;
            sftp.cd(downloadFilePath.substring(0, i));
            File file = new File(saveFile);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            sftp.get(downloadFilePath.substring(i + 1), fileOutputStream);
            fileOutputStream.close();
            return file;
        } catch (Exception e) {
            return null;
        }
    }
 
    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void delete(String directory, String deleteFile) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (Exception e) {
        }
    }
 
    public void disconnect() {
        try {
            sftp.getSession().disconnect();
        } catch (JSchException e) {
        }
        sftp.quit();
        sftp.disconnect();
    }
 
    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @throws SftpException
     */
    public Vector<LsEntry> listFiles(String directory) throws SftpException {
        return sftp.ls(directory);

    }
 
    public static void main(String[] args) throws IOException {
    	 Ftp f=new Ftp();
         f.setIpAddr("114.115.177.163");
         f.setUserName("root");
         f.setPwd("Hosting@2018");
         f.setPort(22);
         String directory = "/usr/local/test";
         
        SFTPUtils sf = SFTPUtils.getInstance(f);
       
 
//        sf.delete(directory, deleteFile); //删除文件
        Vector<LsEntry> files = null;        //查看文件列表
        try {
            files = sf.listFiles(directory);
            if (files != null && files.size() > 0)
            {
            	for (LsEntry lsEntry : files)
            	{
            		 File download = sf.download("/usr/local/test/" + lsEntry.getFilename(), "F:\\Users\\"+ lsEntry.getFilename());
            	}
            }
        } catch (SftpException e) {
            e.printStackTrace();
        }
        for (LsEntry file : files) {
            System.out.println("###\t" + file.getFilename());
        }
        sf.disconnect();
    }
}

