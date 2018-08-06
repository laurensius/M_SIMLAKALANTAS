package com.laurensius.simlakalantas.model;

public class Notif{

    int id;
    int aim;
    int incident;
    int station;
    String content;
    String datetime;

    public Notif(int id,int aim,int incident,int station,String content,String datetime){
        this.id = id;
        this.aim = aim;
        this.incident = incident;
        this.station = station;
        this.content = content;
        this.datetime =datetime;
    }

    public int getId() {
        return id;
    }

    public int getAim() {
        return aim;
    }

    public int getIncident() {
        return incident;
    }

    public int getStation() {
        return station;
    }

    public String getContent() {
        return content;
    }

    public String getDatetime() {
        return datetime;
    }
}
