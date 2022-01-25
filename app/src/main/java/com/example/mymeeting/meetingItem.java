package com.example.mymeeting;

public class meetingItem {

    private String name;

    private int imageId;

    public meetingItem() {

    }

    public meetingItem(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }

}