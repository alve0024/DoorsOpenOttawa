package com.algonquincollege.alve0024.doorsopenottawa.model;


/**
 * Created by leonardoalps on 2016-11-07.
 */



public class Building {
    private int buildingId;
    private String name;
    private String address;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address + "Ottawa, Ontario";
    }

}
