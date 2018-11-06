package com.fms.domain.filemanage;

import lombok.Data;

import java.util.Date;

@Data
public class BlockManage {
    /**
     * 主键.
     */
    private Long id;
    /**
     *白名单内容
     */
    private String whiteContent;

    /**
     *黑名单内容
     */
    private String blackContent;

    /**
     * 创建人
     */
    private Long createUserId;
    /**
     * 创建时间
     */
    private Date created;
    /**
     * 更新时间.
     */
    private Date updated;

    /**
     * 解析器id
     */
    private Long fileParserId;
}
