package com.fms.domain.schema;

import lombok.Data;

//列数据实体类
@Data
public class ColumnInfo {
    private int id;
    private String columnEnglish;
    private int isDic;
    private int isMasterKey;
    private String dataType;
    private long tableId;
    private int schemaId;
    private String dicTableName;
    private String columnChinese;
    private String dicList;

}
