package com.example.mymeeting.bomb;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class User extends BmobObject {
    private Number id;  //主键 自增
    private String userName;

    private String password;  //密码

    private String degree;
    private Number degreeNumber;

    private BmobDate registrationDate;

    //////////////////

    public void setRegistrationDate(BmobDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setDegreeNumber(Number degreeNumber) {
        this.degreeNumber = degreeNumber;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Number getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public BmobDate getRegistrationDate() {
        return registrationDate;
    }

    public Number getDegreeNumber() {
        return degreeNumber;
    }

    public String getDegree() {
        return degree;
    }

    public String getUserName() {
        return userName;
    }
}
