package com.example.mymeeting.bomb;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;

public class _User extends BmobUser {
//    private String username; //用户名/账号/用户唯一标志，可以是邮箱、手机号码、第三方平台的用户唯一标志
//    private String password;

//    private BmobDate createdAt;
//    private BmobDate updatedAt;
//
//    private String mobilePhoneNumber;
//    private Boolean mobilePhoneNumberVerified;
//
//    private String email;
//    private Boolean emailVerified;
    private Boolean sex;
    private String nick;
    private String introduction;
    private String school;

    private BmobRelation attendingMeeting;//参加会议，一对多关系
//
//    //////////////

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setAttendingMeeting(BmobRelation attendingMeeting) {
        this.attendingMeeting = attendingMeeting;
    }

    public String getNick() {
        return nick;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getSchool() {
        return school;
    }

    public BmobRelation getAttendingMeeting() {
        return attendingMeeting;
    }

    //
//    @Override
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
////    public void setCreatedAt(BmobDate createdAt) {
////        this.createdAt = createdAt;
////    }
////
////    public void setUpdatedAt(BmobDate updatedAt) {
////        this.updatedAt = updatedAt;
////    }
//
//    @Override
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    @Override
//    public void setEmailVerified(Boolean emailVerified) {
//        this.emailVerified = emailVerified;
//    }
//
//    @Override
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    @Override
//    public void setMobilePhoneNumber(String mobilePhoneNumber) {
//        this.mobilePhoneNumber = mobilePhoneNumber;
//    }
//
//    @Override
//    public void setMobilePhoneNumberVerified(Boolean mobilePhoneNumberVerified) {
//        this.mobilePhoneNumberVerified = mobilePhoneNumberVerified;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
////    @Override
////    public BmobDate getCreatedAt() {
////        return createdAt;
////    }
////
////    @Override
////    public BmobDate getUpdatedAt() {
////        return updatedAt;
////    }
//
//    @Override
//    public Boolean getEmailVerified() {
//        return emailVerified;
//    }
//
//    @Override
//    public Boolean getMobilePhoneNumberVerified() {
//        return mobilePhoneNumberVerified;
//    }
//
//    @Override
//    public String getEmail() {
//        return email;
//    }
//
//    @Override
//    public String getMobilePhoneNumber() {
//        return mobilePhoneNumber;
//    }
//
//    @Override
//    public String getUsername() {
//        return username;
//    }
}
