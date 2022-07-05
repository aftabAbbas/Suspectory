package com.aftab.suspectory.Model;

import java.io.Serializable;

public class Contacts implements Serializable {

    private String name, number, color,lastMsgDate,unReadCount,lastNoti;

    public Contacts() {
    }

    public Contacts(String name, String number, String color, String lastMsgDate, String unReadCount, String lastNoti) {
        this.name = name;
        this.number = number;
        this.color = color;
        this.lastMsgDate = lastMsgDate;
        this.unReadCount = unReadCount;
        this.lastNoti = lastNoti;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLastMsgDate() {
        return lastMsgDate;
    }

    public void setLastMsgDate(String lastMsgDate) {
        this.lastMsgDate = lastMsgDate;
    }

    public String getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(String unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getLastNoti() {
        return lastNoti;
    }

    public void setLastNoti(String lastNoti) {
        this.lastNoti = lastNoti;
    }
}