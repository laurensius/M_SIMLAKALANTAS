package com.laurensius.simlakalantas.model;

public class PoliceStation {
    int id;
    String stationName;
    double latitude;
    double longitude;
    String address;

    public PoliceStation(int id,String stationName,double latitude,double longitude,String address){
        this.id = id;
        this.stationName = stationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getStationName() {
        return stationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}
