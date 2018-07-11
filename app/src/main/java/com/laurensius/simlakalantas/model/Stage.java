package com.laurensius.simlakalantas.model;

public class Stage {
    int id;
    String stage;

    public Stage(int id, String stage){
        this.id = id;
        this.stage = stage;
    }

    public int getId() {
        return id;
    }

    public String getStage() {
        return stage;
    }
}
