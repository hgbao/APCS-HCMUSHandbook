package com.hgbao.model;

import java.io.Serializable;

public class EPhoto implements Serializable{
    private String eid;
    private byte[] photo;
    //Getters and setters

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
