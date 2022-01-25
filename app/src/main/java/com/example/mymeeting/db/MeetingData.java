package com.example.mymeeting.db;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class MeetingData extends LitePalSupport {
    private int id;  //主键 自增
    private String name;

    private String type; //会议类型
    private int typeNumber;

    private Date registrationDate; //注册时间
    private Date hostDate; //举办时间
    private String  length; //持续时间（字符串类型数据）

    private String location; //会议地点
    private int locationNumber;

    private String state; //会议状态
    private int stateNumber;

    private String introduction; //简介
    private String comtent; // 详细内容
//    private int imageId;

    private String organizer;//举办方（不一定是申请人）

    ////////////////
    /*
    private int userId;
//    private String userName;   //  不符合范式
    */
    ////////////////
    private Boolean ifApplicant;
    private Boolean ifCollection;
}
