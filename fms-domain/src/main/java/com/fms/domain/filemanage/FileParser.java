package com.fms.domain.filemanage;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FileParser {
    //主键
    private Long id;
    //名称
    private String name;
    //创建人
    private Long createUserId;
    //创建时间
    private Date created;
    //更新时间
    private Date updated;
    //路径
    private String source;
    //调用类名
    private String className;
    //参数
    private String params;
    //调用方法
    private String methodName;
    //录入方式0：自动；1：手动
    private Integer inputType;

    private String parserExt;

}
