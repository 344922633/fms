package com.fms.domain.schema;

import lombok.Data;

//列数据实体类
@Data
public class ColumnMapRelation {
    private Long id;
    private String columnKey;
    private long tableId;
    private int columnId;
    private int schemaId;
    private long parserId;
    private String templateName;
    //存放ColumnDic实体类
    private String dicName;
    private String dicValue;

}
