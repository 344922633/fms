package com.fms.domain.schema;

import lombok.Data;

//列数据实体类
@Data
public class ColumnDic {
    private long columnMapId;
    private String dicName;
    private String dicValue;
}
