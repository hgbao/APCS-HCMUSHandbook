package com.hgbao.model;

import java.io.Serializable;
import java.util.Calendar;

public class Act implements Serializable{
    private String id;
    private int type;//1: Seminar - Interchange
                    //2: Contest
                    //3: Voluntary
                    //4: Announcement - result
    private String name, about;
    private String submit;
    private long dateCreated, dateDeadline, dateOccur;
    //Data for programming
    private Boolean expired = false;

    //Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getSubmit() {
        return submit;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateDeadline() {
        return dateDeadline;
    }

    public void setDateDeadline(long dateDeadline) {
        this.dateDeadline = dateDeadline;
    }

    public long getDateOccur() {
        return dateOccur;
    }

    public void setDateOccur(long dateOccur) {
        this.dateOccur = dateOccur;
    }

    public Boolean isExpired() {
        return expired;
    }

    public void setExpired() {
        this.expired = true;
    }
}
