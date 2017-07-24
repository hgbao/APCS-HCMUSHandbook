package com.hgbao.model;

import java.io.Serializable;
import java.util.Calendar;

public class Scholarship implements Serializable{
    private String id;
    private int type;//1: Support
                    //2: Long term
                    //3: Studying abroad
                    //4: Annoucement - Result
    private String name, about;
    private String submitEmail, submitSubject;
    private long dateCreated, dateDeadline, dateReceive;
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

    public long getDateReceive() {
        return dateReceive;
    }

    public void setDateReceive(long dateReceive) {
        this.dateReceive = dateReceive;
    }

    public String getSubmitEmail() {
        return submitEmail;
    }

    public void setSubmitEmail(String submitEmail) {
        this.submitEmail = submitEmail;
    }

    public String getSubmitSubject() {
        return submitSubject;
    }

    public void setSubmitSubject(String submitSubject) {
        this.submitSubject = submitSubject;
    }

    public void setExpired(){
        expired = true;
    }

    public boolean isExpired(){
        return expired;
    }
}
