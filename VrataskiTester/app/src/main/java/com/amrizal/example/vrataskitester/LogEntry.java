package com.amrizal.example.vrataskitester;

import com.google.firebase.database.ServerValue;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amrizal.zainuddin on 24/5/2016.
 */
public class LogEntry {
    String description;
    Long timeStamp;

    public LogEntry(){

    }
    public LogEntry(String description){
        this.description = description;
    }
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
