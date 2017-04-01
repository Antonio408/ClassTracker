package com.example.gary.classtracker;

import java.io.Serializable;

/**
 * Created by Gary on 4/1/2017.
 * Mylocation class to be serializable
 */

public class MyLocation implements Serializable {
    private String name;
    private double longitude;
    private double latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public MyLocation (String name, double longitude, double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Location: " + name + " Longitude: " + longitude + " Latitude: " + latitude;
    }
}
