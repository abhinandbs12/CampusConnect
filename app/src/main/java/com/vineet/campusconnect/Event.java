package com.vineet.campusconnect.models;

public class Event {
    private String title;
    private String date;
    private String time;
    private String location;
    private String type; // "ONGOING" or "UPCOMING"

    public Event(String title, String date, String time, String location, String type) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.type = type;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getType() { return type; }
}