package com.example.mymeeting.db;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;

public class notificationItem  extends LitePalSupport implements Serializable {

    //Litepal数据库自动生成的自增的ID
    private long id;

    private String title; //通知标题
    private String content; //通知内容

    private Date createDate; //创建时间

    private String userObjectId;

    /////////////////////////////////////

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    /////////////////////////////////////


    public void setUserObjectId(String userObjectId) {
        this.userObjectId = userObjectId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserObjectId() {
        return userObjectId;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getContent() {
        return content;
    }

}
