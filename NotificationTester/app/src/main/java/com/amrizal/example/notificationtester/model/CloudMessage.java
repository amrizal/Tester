package com.amrizal.example.notificationtester.model;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by amrizal.zainuddin on 11/10/2016.
 */
public class CloudMessage {
    HashMap<String, String> data = new HashMap<>();
    String to;

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(String key, String value) {
        data.put(key, value);
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
