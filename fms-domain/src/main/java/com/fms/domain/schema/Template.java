package com.fms.domain.schema;

import lombok.Data;

import java.util.Map;

@Data
public class Template {
    public long id;
    private String columnKey;
    private int columnId;
    private long tableId;
    private int schemaId;
    private long parserId;
    private String parserName;
    private String tableName;
    private String columnName;
    private String SchemaName;
    private String templateName;

    private Map<String,Object> dicMap;
}
