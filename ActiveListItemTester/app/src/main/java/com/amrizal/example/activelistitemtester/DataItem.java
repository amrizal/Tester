package com.amrizal.example.activelistitemtester;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amrizal.zainuddin on 7/11/2016.
 */
public class DataItem {
    private long id;
    List<String> col = new ArrayList<>();

    public DataItem(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void add(String s){
        col.add(s);
    }

    public String get(int index){
        return col.get(index);
    }
}
