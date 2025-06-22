package com.example.tlupickleball.model;

public class Player {
   private String name;
   private String gender;
   private String dob;
   private String email;
   private double soloPoint;
   private double douPoint;
   private int avatarResourceId;
    public Player(String name, double soloPoint, int avatarResourceId) {
        this.name = name;
        this.soloPoint = soloPoint;
        this.avatarResourceId = avatarResourceId;
    }

    public Player(String name, String email, int avatarResourceId){
        this.name = name;
        this.email = email;
        this.avatarResourceId = avatarResourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getSoloPoint() {
        return soloPoint;
    }

    public void setSoloPoint(double soloPoint) {
        this.soloPoint = soloPoint;
    }

    public double getDouPoint() {
        return douPoint;
    }

    public void setDouPoint(double douPoint) {
        this.douPoint = douPoint;
    }

    public int getAvatarResourceId() {
        return avatarResourceId;
    }

    public void setAvatarResourceId(int avatarResourceId) {
        this.avatarResourceId = avatarResourceId;
    }
}
