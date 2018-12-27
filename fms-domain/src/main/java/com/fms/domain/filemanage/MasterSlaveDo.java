package com.fms.domain.filemanage;

import lombok.Data;

import java.util.List;

@Data
public class MasterSlaveDo {
    private Integer id;

    private String name;

    private long masterTableId;

    private long slaveTableId;

    private String type;

    private String lable;

    private String value;

    private List<MasterSlaveDo> children;

    private String masterTableName;

    private String slaveTableName;

}
