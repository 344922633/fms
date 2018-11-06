package com.fms.domain.filemanage;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;
    private String name;
    private String password;
    private String state;//1：正常；2：禁用
    private Date creatd;
}
