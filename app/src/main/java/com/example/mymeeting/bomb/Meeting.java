package com.example.mymeeting.bomb;

import android.text.format.Time;
import cn.bmob.v3.datatype.BmobDate;
import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;


public class Meeting extends BmobObject {
    private Number id;  //主键 自增
    private String name;
//
    private String type; //会议类型
    private Number typeNumber;

    private BmobFile picture;//图片文件
//
    private BmobDate registrationDate; //注册时间
    private BmobDate hostDate; //举办时间
    private String length; //持续时间（字符串类型数据）

    private String location; //会议地点
    private Number locationNumber;

    private String state; //会议状态
    private Number stateNumber;

    private String introduction; //简介
    private String comtent; // 详细内容
//    private Number imageId;

//    private Number userId;  //申请人id（已弃用）

    private String organizer;//举办方（不一定是申请人）

    private String groupId;//环信群组id

    private _User originator; //发起人，一对一关系

    private BmobRelation participant;//与会者，一对多关系

    private BmobRelation signinParticipant;//已签到与会者，一对多关系

//    private String objectId;  //不可设置，否则会出BmobObject的那个问题


    //////////


    public void setSigninParticipant(BmobRelation signinParticipant) {
        this.signinParticipant = signinParticipant;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypeNumber(Number typeNumber) {
        this.typeNumber = typeNumber;
    }

    public void setStateNumber(Number stateNumber) {
        this.stateNumber = stateNumber;
    }

    public void setRegistrationDate(BmobDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setComtent(String comtent) {
        this.comtent = comtent;
    }

    public void setHostDate(BmobDate hostDate) {
        this.hostDate = hostDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocationNumber(Number locationNumber) {
        this.locationNumber = locationNumber;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setOriginator(_User originator) {
        this.originator = originator;
    }

    public void setParticipant(BmobRelation participant) {
        this.participant = participant;
    }

    public Number getId() {
        return id;
    }

    public String getLength() {
        return length;
    }

    public String getIntroduction() {
        return introduction;
    }

    public Number getTypeNumber() {
        return typeNumber;
    }

    public String getState() {
        return state;
    }

    public String getOrganizer() {
        return organizer;
    }

    public Number getLocationNumber() {
        return locationNumber;
    }

    public String getComtent() {
        return comtent;
    }

    public Number getStateNumber() {
        return stateNumber;
    }

    public String getType() {
        return type;
    }

    public BmobRelation getSigninParticipant() {
        return signinParticipant;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public BmobFile getPicture() {
        return picture;
    }

    public String getGroupId() {
        return groupId;
    }

    public BmobDate getHostDate() {
        return hostDate;
    }

    public BmobDate getRegistrationDate() {
        return registrationDate;
    }

    public _User getOriginator() {
        return originator;
    }

    public BmobRelation getParticipant() {
        return participant;
    }
}
