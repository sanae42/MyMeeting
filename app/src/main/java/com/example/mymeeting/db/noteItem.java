package com.example.mymeeting.db;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;

public class noteItem  extends LitePalSupport implements Serializable {

    private String title; //笔记标题
    private String content; //笔记内容
    private String type; //笔记类型

    private Date createDate; //创建时间
    private Date updateDate; //修改时间

//    private meetingItem meeting;
    private String meetingObjectId;
    private int meetingBombId;
    private String meetingName;

    /////////////////////////////////////


    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setMeetingBombId(int meetingBombId) {
        this.meetingBombId = meetingBombId;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public void setMeetingObjectId(String meetingObjectId) {
        this.meetingObjectId = meetingObjectId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public int getMeetingBombId() {
        return meetingBombId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public String getMeetingObjectId() {
        return meetingObjectId;
    }

    public String getTitle() {
        return title;
    }
}
