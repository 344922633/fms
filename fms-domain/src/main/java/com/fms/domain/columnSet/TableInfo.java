package com.fms.domain.columnSet;

import lombok.Data;

//表数据实体类
@Data
public class TableInfo {
    private long id;
    private String tableEnglish;
    private String tableChinese;
    private int schemald;

}
