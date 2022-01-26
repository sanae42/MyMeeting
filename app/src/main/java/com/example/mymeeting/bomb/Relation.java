package com.example.mymeeting.bomb;

import cn.bmob.v3.BmobObject;

public class Relation extends BmobObject {
    private Number id;  //主键 自增

    private Number userId;
    private Number meetingId;
    private Boolean ifApplicant;
    private Boolean ifCollection;

    public void setId(Number id) {
        this.id = id;
    }

    public void setUserId(Number userId) {
        this.userId = userId;
    }

    public void setIfApplicant(Boolean ifApplicant) {
        this.ifApplicant = ifApplicant;
    }

    public void setIfCollection(Boolean ifCollection) {
        this.ifCollection = ifCollection;
    }

    public void setMeetingId(Number meetingId) {
        this.meetingId = meetingId;
    }

    public Number getId() {
        return id;
    }

    public Number getUserId() {
        return userId;
    }

    public Boolean getIfApplicant() {
        return ifApplicant;
    }

    public Boolean getIfCollection() {
        return ifCollection;
    }

    public Number getMeetingId() {
        return meetingId;
    }
}
