package com.aftab.suspectory.Model;

public class SimSMS {

    private String id, address, msg, type, time,location;
    private boolean read;

    public SimSMS() {

    }

    public SimSMS(String id, String address, String msg, String type, String time, String location, boolean read) {
        this.id = id;
        this.address = address;
        this.msg = msg;
        this.type = type;
        this.time = time;
        this.location = location;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
