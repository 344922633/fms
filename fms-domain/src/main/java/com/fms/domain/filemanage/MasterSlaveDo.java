package com.fms.domain.filemanage;

import lombok.Data;

import java.util.List;

@Data
public class MasterSlaveDo {
    private String id;

    private String name;

    private long masterTable;

    private long slaveTable;

    private String type;

    private String lable;

    private String value;

    private List<MasterSlaveDo> children;
}
