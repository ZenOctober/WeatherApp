package com.crechelessons.howhumidisit.model;

/**
 * Created by Home on 31.1.2018 г..
 */

public class Coord {

    private double lat;
    private double lng;

    public Coord(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
