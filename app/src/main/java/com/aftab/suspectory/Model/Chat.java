package com.aftab.suspectory.Model;

import java.io.Serializable;

public class Chat implements Serializable {

    private String id, message, time, type, duration, des,location,url;
    private boolean read;

    public Chat() {
    }


    public Chat(String id, String message, String time, String type, String duration, String des, String location, String url, boolean isRead) {
        this.id = id;
        this.message = message;
        this.time = time;
        this.type = type;
        this.duration = duration;
        this.des = des;
        this.location = location;
        this.url = url;
        this.read = isRead;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean getIsRead() {
        return read;
    }

    public void setRead(boolean read) {
        read = read;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
