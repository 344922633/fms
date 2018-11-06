package com.fms.domain.filemanage.upload;

import lombok.Data;

import java.io.Serializable;
@Data
public class FileInfo implements Serializable {
    private Long id;

    private String filename;

    private String identifier;

    private Long totalSize;

    private String type;

    private String location;

    private String webkitRelativePath;
    private Long directoryId;
}