package com.zxy.demo.xml.bean;

import java.util.Date;

public class Student {
    private int id;
    private String name;
    private String address;
    private String email;
    private Date birthday;
    
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getEmail() {
        return email;
    }
    public Date getBirthday() {
        return birthday;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
    
}
