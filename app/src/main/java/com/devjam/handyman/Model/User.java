package com.devjam.handyman.Model;

public class User {
    private String id;
    private String Name;
    private String Address;
    private String Email;
    private long Contact;
    private int Pincode;

    public User() {
    }

    public User(String id, String name, String address, String email, long contact, int pincode) {
        this.id = id;
        Name = name;
        Address = address;
        Email = email;
        Contact = contact;
        Pincode = pincode;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
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
