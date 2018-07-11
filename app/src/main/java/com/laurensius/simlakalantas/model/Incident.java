package com.laurensius.simlakalantas.model;

public class Incident {
    int id;
    int sender;
    String image;
    String description;
    String latitude;
    String longitude;
    String receivedAt;
    int lastStage;
    String lastStageDatetime;
    int processedBy;
    int station;

    public Incident(int id,int sender,String image,String description,String latitude,String longitude,
         String receivedAt,int lastStage,String lastStageDatetime,int processedBy,int station){
        this.id = id;
        this.sender = sender;
        this.image = image;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.receivedAt = receivedAt;
        this.lastStage = lastStage;
        this.lastStageDatetime = lastStageDatetime;
        this.processedBy = processedBy;
        this.station = station;
    }

    public int getId() {
        return id;
    }

    public int getSender() {
        return sender;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public int getLastStage() {
        return lastStage;
    }

    public String getLastStageDatetime() {
        return lastStageDatetime;
    }

    public int getProcessedBy() {
        return processedBy;
    }

    public int getStation() {
        return station;
    }
}
