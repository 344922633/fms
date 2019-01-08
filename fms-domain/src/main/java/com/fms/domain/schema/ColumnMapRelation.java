package com.fms.domain.schema;

import lombok.Data;

import java.util.Map;

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
    //存放ColumnDic数据
    private Map<String,Object> dicMap;
}
