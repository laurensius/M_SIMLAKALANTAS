package com.laurensius.simlakalantas.model;

public class FW {

    PoliceStation ps;
    float distance;

    public FW(PoliceStation ps,float distance){
        this.ps = ps;
        this.distance = distance;
    }

    public PoliceStation getPs() {
        return ps;
    }

    public float getDistance() {
        return distance;
    }
}
