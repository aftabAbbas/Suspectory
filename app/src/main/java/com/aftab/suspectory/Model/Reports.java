package com.aftab.suspectory.Model;

/**
 * Created by Adnan Ashraf on 5/9/2021.
 */
public class Reports {

    String name, email, desc;

    public Reports() {
    }

    public Reports(String name, String email, String desc) {
        this.name = name;
        this.email = email;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
