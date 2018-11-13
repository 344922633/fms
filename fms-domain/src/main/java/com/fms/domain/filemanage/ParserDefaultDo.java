package com.fms.domain.filemanage;

import lombok.Data;

@Data
public class ParserDefaultDo {
    private String user;

    /**
     * 文件解析器ids，多个用,分隔
     */
    private String fileParserIds;
}
