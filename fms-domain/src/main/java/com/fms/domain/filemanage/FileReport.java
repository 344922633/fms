package com.fms.domain.filemanage;


import lombok.Data;

@Data
public class FileReport {
	private long id;
	private String ip ;
	private String userName;
	private String password;
	private String port;
	private String path;

}
