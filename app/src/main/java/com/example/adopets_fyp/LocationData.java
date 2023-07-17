package com.example.adopets_fyp;

public class LocationData {
    private double latitude;
    private double longitude;
    private String markerName;

    public LocationData() {
        // Required empty constructor for Firebase
    }

    public LocationData(double latitude, double longitude, String markerName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.markerName = markerName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getMarkerName() {
        return markerName;
    }
}
