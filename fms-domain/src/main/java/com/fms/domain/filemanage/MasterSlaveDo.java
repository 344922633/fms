package com.fms.domain.filemanage;

import lombok.Data;

import java.util.List;

@Data
public class MasterSlaveDo {
    private String id;

    private String name;

    private long masterTableId;

    private long slaveTableId;

    private String type;

    private List<MasterSlaveDo> children;
}
