/*********************************************************************************************
 *   Doors Open Ottawa - List information about the Buildings with the Doors Open            *
 *                                                                                           *
 *   @author Leonardo Alps (alve0024@algonquinlive.com)                                      *
 *                                                                                           *
 *   Supervision: Gerald.Hurdle@AlgonquinCollege.com                                         *
 *                                                                                           *
 *   Algonquin College - All right reserved!                                                 *
 *                                                                                           *
 *********************************************************************************************/

package com.algonquincollege.alve0024.doorsopenottawa.model;


import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;


/**
 * Definition of the Building class
 */
public class Building {
    // Instance Variables
    private int buildingId;
    private String name;
    private String address;
    private String image;
    private List<String> openHours = new ArrayList<>();
    private String description;
    private Bitmap bitmap;
    private Boolean favorite;


    // Getters
    public int getBuildingId() {
        return buildingId;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getImage() {
        return image;
    }
    public List<String> getOpenHours() {return openHours;}
    public String getDescription() {
        return description;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public Boolean getFavorite() {return favorite; }


    // Setters
    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {
        this.address = address + " Ottawa, Ontario";
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void addDate(String date) {this.openHours.add(date);}
    public void setFavorite(Boolean favorite) { this.favorite = favorite;}
}
