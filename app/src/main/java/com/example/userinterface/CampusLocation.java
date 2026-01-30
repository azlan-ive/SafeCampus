package com.example.userinterface;

public class CampusLocation {
    public String name;
    public String type;
    public double lat;
    public double lng;

    public CampusLocation() {
        // Required for Firebase
    }

    public CampusLocation(String name, String type, double lat, double lng) {
        this.name = name;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
    }
}
