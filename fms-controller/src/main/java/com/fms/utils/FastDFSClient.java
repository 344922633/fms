package com.fms.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.ServerInfo;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

public class FastDFSClient {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(FastDFSClient.class);
	private static TrackerClient trackerClient;
	private static TrackerServer trackerServer;
	private static StorageClient storageClient;
	private static StorageServer storageServer;

	/**
	 * 初始化fastdfs，只有首次访问时才会初始化
	 */
	static {
		try {
			String filePath = new ClassPathResource("fdfs_client.conf").getFile().getAbsolutePath();;
			ClientGlobal.init(filePath);
			trackerClient = new TrackerClient();
			trackerServer = trackerClient.getConnection();
			storageServer = trackerClient.getStoreStorage(trackerServer);
			storageClient = new StorageClient(trackerServer, storageServer);
		} catch (Exception e) {
			logger.error("FastDFS Client Init Fail!",e);
		}
	}

	/**
	 * @param multipartFile
	 * @return
	 * @throws IOException
	 */
	public static String saveFile(MultipartFile multipartFile) throws IOException {
		String[] fileAbsolutePath = {};
		String fileName = multipartFile.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
		byte[] file_buff = null;
		InputStream inputStream = multipartFile.getInputStream();
		if (inputStream != null) {
			int len1 = inputStream.available();
			file_buff = new byte[len1];
			inputStream.read(file_buff);
		}
		inputStream.close();
		FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
		try {
			fileAbsolutePath = FastDFSClient.upload(file); //upload to fastdfs
		} catch (Exception e) {
			logger.error("upload fileparser Exception!", e);
		}
		if (fileAbsolutePath == null) {
			logger.error("upload fileparser failed,please upload again!");
		}
		String path = FastDFSClient.getTrackerUrl() + fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
		return path;
	}
	/**
	 * 上传功能
	 * @param file 上传文件
	 * @return 上传结果
	 */
	public static String[] upload(FastDFSFile file) {
		logger.info("File Name: " + file.getName() + "File Length:" + file.getContent().length);

		NameValuePair[] meta_list = new NameValuePair[1];
		meta_list[0] = new NameValuePair("author", file.getAuthor());

		long startTime = System.currentTimeMillis();
		String[] uploadResults = null;
		try {
			uploadResults = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
		} catch (IOException e) {
			logger.error("IO Exception when uploadind the fileparser:" + file.getName(), e);
		} catch (Exception e) {
			logger.error("Non IO Exception when uploadind the fileparser:" + file.getName(), e);
		}
		logger.info("upload_file time used:" + (System.currentTimeMillis() - startTime) + " ms");

		//uploadResults为空，说明上传失败，不为空，获取上传分组名称，以及远程服务器对应的文件名
		if (uploadResults == null) {
			logger.error("upload fileparser fail, error code:" + storageClient.getErrorCode());
		} else {
			String groupName = uploadResults[0];
			String remoteFileName = uploadResults[1];

			//remoteFileName返回防盗地址（即：加上token和ts）：
			//String token = getToken(remoteFileName, ClientGlobal.getG_secret_key());
			//logger.info("防盗图片url:" + getTrackerUrl() + groupName + "/" + remoteFileName + "?" + token);

			logger.info("upload fileparser successfully!!!" + "group_name:" + groupName + ", remoteFileName:" + " " + remoteFileName);
		}

		return uploadResults;
	}

	/**
	 * 获取文件信息.
	 * @param groupName 分组名
	 * @param remoteFileName 远程文件名
	 * @return
	 */
	public static FileInfo getFile(String groupName, String remoteFileName) {
		try {

			return storageClient.get_file_info(groupName, remoteFileName);
		} catch (IOException e) {
			logger.error("IO Exception: Get File from Fast DFS failed", e);
		} catch (Exception e) {
			logger.error("Non IO Exception: Get File from Fast DFS failed", e);
		}
		return null;
	}

	/**
	 *  下载远程文件.
	 * @param groupName 分组名
	 * @param remoteFileName  远程文件名
	 * @return 文件流
	 */
	public static InputStream downFile(String groupName, String remoteFileName) {
		try {
			byte[] fileByte = storageClient.download_file(groupName, remoteFileName);
			InputStream ins = new ByteArrayInputStream(fileByte);
			return ins;
		} catch (IOException e) {
			logger.error("IO Exception: Get File from Fast DFS failed", e);
		} catch (Exception e) {
			logger.error("Non IO Exception: Get File from Fast DFS failed", e);
		}
		return null;
	}

	/**
	 * 删除文件
	 * @param groupName 组名
	 * @param remoteFileName 远程文件名字
	 * @throws Exception
	 */
	public static int deleteFile(String groupName, String remoteFileName)
			throws Exception {
		int i = storageClient.delete_file(groupName, remoteFileName);
		logger.info("delete fileparser successfully!!!" + i);
		return i;
	}

	public static StorageServer[] getStoreStorages(String groupName)
			throws IOException {
		return trackerClient.getStoreStorages(trackerServer, groupName);
	}

	public static ServerInfo[] getFetchStorages(String groupName,
												String remoteFileName) throws IOException {
		return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
	}


	public static String getTrackerUrl() {
		InetSocketAddress address = trackerServer.getInetSocketAddress();
		String host = address.getHostString();
		int port = ClientGlobal.getG_tracker_http_port();
		String trackerUrl =  "http://" + host + ":" + port + "/";
		return trackerUrl;
	}

	/**
	 * 获取访问服务器的token，拼接到地址后面
	 *
	 * @param fileName 文件路径 M00/00/00/wKgzgFnkTPyAIAUGAAEoRmXZPp876.jpeg
	 * @param httpSecretKey 密钥
	 * @return 返回token，如： token=078d370098b03e9020b82c829c205e1f&ts=1508141521
	 */
	public static String getToken(String fileName, String httpSecretKey) {
		// unix seconds
		int ts = (int) (System.currentTimeMillis() / 1000);
		// token
		String token = "null";
		try {
			token = ProtoCommon.getToken(fileName, ts, httpSecretKey);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (MyException e) {
			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("token=").append(token);
		sb.append("&ts=").append(ts);

		return sb.toString();
	}
}