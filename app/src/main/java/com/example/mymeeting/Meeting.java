package com.example.mymeeting;

public class Meeting {

    private String name;

    private int imageId;

    public Meeting(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }

}