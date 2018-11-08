package com.fms.domain.filemanage;

import lombok.Data;

import java.util.Date;

@Data
public class FileType {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 文件分类名称
     */
    private String name;

    /**
     * 文件父分类
     */
    private String type;
    /**
     * 文件后缀，多个用,分隔
     */
    private String fileSuffix;

    /**
     * 文件解析器ids，多个用,分隔
     */
    private String fileParserIds;

    /**
     * 文件解析器名称，多个用,分隔
     */
    private String fileParserNames;
    /**
     * c创建人id
     */
    private Long createUserId;
    /**
     * 创建时间
     */
    private Date created;
    /**
     * 更新时间
     */
    private Date updated;
}