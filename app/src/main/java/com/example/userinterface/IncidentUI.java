package com.example.userinterface;

public class IncidentUI {
    public String type;
    public String description;
    public String time;
    public double latitude;
    public double longitude;
    public String userId;
    public String username; // Added to store the name of the reporter

    // No-argument constructor required for Firebase
    public IncidentUI() {}

    public IncidentUI(String type, String description, String time, double latitude, double longitude, String userId, String username) {
        this.type = type;
        this.description = description;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.username = username;
    }
}