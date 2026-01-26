package com.example.userinterface;

public class IncidentData {

    public String id;
    public String type;
    public String description;
    public String datetime;
    public double latitude;
    public double longitude;

    // REQUIRED empty constructor for Firebase
    public IncidentData() {}

    public IncidentData(String id, String type, String description,
                        String datetime, double latitude, double longitude) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.datetime = datetime;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
