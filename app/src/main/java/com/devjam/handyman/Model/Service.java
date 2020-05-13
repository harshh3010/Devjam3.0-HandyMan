package com.devjam.handyman.Model;

public class Service {
    private String Id;
    private String Name;
    private String Description;
    private String Cost;

    public Service() {
    }

    public Service(String id, String name, String description, String cost) {
        Id = id;
        Name = name;
        Description = description;
        Cost = cost;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }
}
