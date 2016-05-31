package com.amrizal.example.deolivieratester;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
 * Created by amrizal.zainuddin on 31/5/2016.
 */
public class LogEntry {

    public LogEntry() {
    }

    public LogEntry(String user, String description) {
        this.user = user;
        this.description = description;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    String user;
    String description;
    Long timeStamp;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getTimeStamp() {
        return ServerValue.TIMESTAMP;
    }

    @JsonIgnore
    public Long getTimeStampLong(){
        return this.timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
