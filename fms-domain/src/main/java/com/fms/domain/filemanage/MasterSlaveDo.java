package com.fms.domain.filemanage;

import lombok.Data;

import java.util.List;

@Data
public class MasterSlaveDo {
    private String name;

    private String masterTable;

    private String slaveTable;

    private String type;

    private List<MasterSlaveDo> children;
}
