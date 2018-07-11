package com.laurensius.simlakalantas.model;

public class User {

    int id;
    String username;
    String password;
    String fullName;
    String address;
    String phone;
    String email;
    Boolean isOfficer;
    int station;
    String lastLogin;

    public User( int id,String username,String password,String fullName,String address,String phone,
            String email,Boolean isOfficer,int station,String lastLogin){
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.isOfficer = isOfficer;
        this.station = station;
        this.lastLogin = lastLogin;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFull_name() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIs_officer() {
        return isOfficer;
    }

    public int getStation() {
        return station;
    }

    public String getLast_login() {
        return lastLogin;
    }
}
