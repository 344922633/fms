package com.fms.domain.filemanage;

import com.handu.apollo.base.TreeVo;
import lombok.Data;

@Data
public class Directory extends TreeVo {
    //文件夹名称
    private String text;
    //描述
    private String description;
    //等级
    private Integer level;

}
