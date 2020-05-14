package com.devjam.handyman.Model;

public class Booking {
    private String Id;
    private String ServiceId;
    private String Date;
    private String Time;
    private String Status;

    public Booking() {
    }

    public Booking(String id, String serviceId, String date, String time, String status) {
        Id = id;
        ServiceId = serviceId;
        Date = date;
        Time = time;
        Status = status;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getServiceId() {
        return ServiceId;
    }

    public void setServiceId(String serviceId) {
        ServiceId = serviceId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
