package com.fms.domain.schema;

import lombok.Data;

//列数据实体类
@Data
public class ColumnDic {
    private Long columnMapId;
    private String dicName;
    private String dicValue;

    public ColumnDic() {
    }

    public ColumnDic(String dicName, String dicValue) {
        this.dicName = dicName;
        this.dicValue = dicValue;
    }
}
