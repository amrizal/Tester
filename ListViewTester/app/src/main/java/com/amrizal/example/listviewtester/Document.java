package com.amrizal.example.listviewtester;

/**
 * Created by amrizal.zainuddin on 15/3/2016.
 */
public class Document {

    public static final String ACTIVE = "ACTIVE";

    int id = -1;
    String title = "";
    String description = "";
    String status = "";
    boolean enable = false;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
