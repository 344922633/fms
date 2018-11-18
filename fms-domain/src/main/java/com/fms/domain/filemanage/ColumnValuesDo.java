package com.fms.domain.filemanage;

import lombok.Data;

import java.util.List;

@Data
public class ColumnValuesDo {
    private String tableName;

    private String columnvalue;

    private String selectLable;

    private String selectValue;

}
