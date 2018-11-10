package com.fms.domain.filemanage;

import com.handu.apollo.base.LongVo;
import lombok.Data;

@Data
public class File extends LongVo implements Cloneable{
    //文件名称
    private String name;
    //真实路径
    private String realPath;
    //描述
    private String description;
    //文件后缀
    private String type;
    //表圈
    private String tag;
    //所属目录
    private Long directoryId;
    //是否上传
    private Boolean isUpload;
    //文件系统groupUploadController
    private String groups;
    private Long classId;//分类ID
    private String classType;//分类类型
    private String fatherClassName;
    private String className;//分类名称
    private Long recommendParserId;//推荐解析器ID
    private String recommendParserName;//推荐解析器名称
    //是否解析
    private Integer isParser;
    private Integer isExport;//是否导出
    //文件md5值
    private String fileMd5;
    //解析结果
    private String parseResult;

    public Object clone() {
        File file = null;
        try {
            file = (File) super.clone();
            } catch (CloneNotSupportedException e) {
                 System.out.println(e.toString());
           }

        return file;
    }
}
