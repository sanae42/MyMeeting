package com.example.mymeeting.db;

import org.litepal.crud.LitePalSupport;

public class relation extends LitePalSupport {
    private int id;  //主键 自增

    private int userId;
//    private String userName  //不符合范式

    private int meetingId;
//    private String meetingName  //不符合范式

    private Boolean ifApplicant;
    private Boolean ifCollection;
}
