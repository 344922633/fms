package com.fms.domain.filemanage;

import lombok.Data;

@Data
public class MasterSlaveDo {
    private String name;

    private String masterTable;

    private String slaveTable;

    private String type;
}
