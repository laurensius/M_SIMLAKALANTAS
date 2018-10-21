package com.laurensius.simlakalantas.model;

public class Notif{

    int id;
    int aim;
    int incident;
    int station;
    String content;
    String datetime;
    String is_open;

    public Notif(int id,int aim,int incident,int station,String content,String datetime,String is_open){
        this.id = id;
        this.aim = aim;
        this.incident = incident;
        this.station = station;
        this.content = content;
        this.datetime =datetime;
        this.is_open =is_open;
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

    public String getIs_open() {return is_open;}
}
