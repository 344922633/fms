package com.fms.domain.filemanage;

import lombok.Data;

import java.util.Date;

@Data
public class FileParserExt {
    //主键
    private Long id;
    //解析器ID
    private Long parserId;

    //参数名字
    private String parameterName;

    //参数描述
    private String parameterDesc;

    //参数值
    private String parameterValue;

}
