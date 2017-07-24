package com.hgbao.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Entertainment implements Serializable{
    private String id;
    //Required attributes
    private int type;//1: breakfast - lunch vendor
                    //2: cafe - restaurant
                    //3: other
    private String name, address, description;
    private byte[] avatar;
    //Other attributes
    private float rating;
    private int vote;
    private String website;
    private ArrayList<byte[]> list_photo;
    //Map attributes
    private double longitude;
    private double latitude;

    //Contructor
    public Entertainment(){
        list_photo = new ArrayList<>();
    }

    //Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public ArrayList<byte[]> getList_photo() {
        return list_photo;
    }

    public void addList_photo(byte[] photo) {
        this.list_photo.add(photo);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}


