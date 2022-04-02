package com.example.mymeeting.bomb;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class Schedule extends BmobObject implements Serializable {
    private Number id;  //主键 自增

    private String type; //类型
    private String state; //状态

    private String title; // 标题
    private String content; // 内容

    private String date;
    private String start;
    private String end;

    private String day;
    private String month;
    private String year;

    private _User sender; //发送者，一对一关系

    private _User speaker; //主讲人，一对一关系

    private Meeting meeting;//所属会议，一对一关系

    ////////////////////

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSender(_User sender) {
        this.sender = sender;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setSpeaker(_User speaker) {
        this.speaker = speaker;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public _User getSender() {
        return sender;
    }

    public String getState() {
        return state;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public Number getId() {
        return id;
    }

    public String getEnd() {
        return end;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public _User getSpeaker() {
        return speaker;
    }

    public String getStart() {
        return start;
    }

}
