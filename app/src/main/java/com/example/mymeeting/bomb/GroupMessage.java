package com.example.mymeeting.bomb;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;

public class GroupMessage  extends BmobObject {
    private Number id;  //主键 自增

    private String type; //消息类型
    private String state; //消息状态

    private String content; // 消息内容

    private BmobDate postDate; //发送时间


    private _User sender; //发送者，一对一关系
    private String senderType; //发送者类型

    private Meeting meeting;//所属会议，一对多关系


    //////////


    public void setId(Number id) {
        this.id = id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPostDate(BmobDate postDate) {
        this.postDate = postDate;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public void setSender(_User sender) {
        this.sender = sender;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public Number getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public _User getSender() {
        return sender;
    }

    public BmobDate getPostDate() {
        return postDate;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public String getContent() {
        return content;
    }

    public String getSenderType() {
        return senderType;
    }
}
