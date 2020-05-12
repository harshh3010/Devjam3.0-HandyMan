package com.devjam.handyman.Util;

import android.app.Activity;

public class UserApi extends Activity {

    private String id;
    private String Name;
    private String Email;
    private String Address;
    private long Contact;
    private int Pincode;
    private static UserApi instance;

    public static UserApi getInstance() {
        if(instance == null)
            instance = new UserApi();
        return instance;
    }

    public UserApi() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public long getContact() {
        return Contact;
    }

    public void setContact(long contact) {
        Contact = contact;
    }

    public int getPincode() {
        return Pincode;
    }

    public void setPincode(int pincode) {
        Pincode = pincode;
    }
}
