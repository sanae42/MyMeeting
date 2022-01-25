package com.example.mymeeting.db;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class User extends LitePalSupport {
    private int id;  //主键 自增
    private String userName;

    private String password;  //密码

    private String degree;
    private int degreeNumber;

    private Date registrationDate;

}
