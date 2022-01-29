package com.example.mymeeting;

import com.example.mymeeting.bomb._User;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;

public class meetingItem  extends LitePalSupport implements Serializable {

    private String objectId;  //bomb数据库的id

    private Number id;  //主键 自增
    private String name;
    //
    private String type; //会议类型
    private Number typeNumber;
    //
    private Date registrationDate; //注册时间
    private Date hostDate; //举办时间
    private String length; //持续时间（字符串类型数据）

    private String location; //会议地点
    private Number locationNumber;

    private String state; //会议状态
    private Number stateNumber;

    private String introduction; //简介
    private String comtent; // 详细内容

    private Number imageId; //图片id

    private String organizer;//举办方（不一定是申请人）


    private Boolean ifOriginator;
    private Boolean ifParticipant;


    //////////


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

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
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

    public void setHostDate(Date hostDate) {
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

    public void setImageId(Number imageId) {
        this.imageId = imageId;
    }

    public void setIfOriginator(Boolean ifOriginator) {
        this.ifOriginator = ifOriginator;
    }

    public void setIfParticipant(Boolean ifParticipant) {
        this.ifParticipant = ifParticipant;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public Date getHostDate() {
        return hostDate;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public Number getImageId() {
        return imageId;
    }

    public Boolean getIfOriginator() {
        return ifOriginator;
    }

    public String getObjectId() {
        return objectId;
    }

    public Boolean getIfParticipant() {
        return ifParticipant;
    }
}