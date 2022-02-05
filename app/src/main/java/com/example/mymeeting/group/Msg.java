package com.example.mymeeting.group;

import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;

import java.util.Date;

import cn.bmob.v3.datatype.BmobDate;

public class Msg {

    private String objectId;  //bomb数据库的id

    private int bomb_id;  //主键 自增

    private boolean ifMyMessage;

    private String type; //消息类型
    private String state; //消息状态

    private String content; // 消息内容

    private Date postDate; //发送时间

    private String senderType; //发送者类型

    private _User sender; //消息发送者，方便获取显示发送者信息

    //////////////////////////////////


    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public void setSender(_User sender) {
        this.sender = sender;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setBomb_id(int bomb_id) {
        this.bomb_id = bomb_id;
    }

    public void setIfMyMessage(boolean ifMyMessage) {
        this.ifMyMessage = ifMyMessage;
    }

    public String getSenderType() {
        return senderType;
    }

    public String getContent() {
        return content;
    }

    public Date getPostDate() {
        return postDate;
    }

    public String getState() {
        return state;
    }

    public String getType() {
        return type;
    }

    public String getObjectId() {
        return objectId;
    }

    public int getBomb_id() {
        return bomb_id;
    }

    public _User getSender() {
        return sender;
    }

    public boolean isIfMyMessage() {
        return ifMyMessage;
    }
}
