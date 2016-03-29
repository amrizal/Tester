package com.amrizal.example.serviceupdateuitester;

import android.app.Application;

/**
 * Created by amrizal.zainuddin on 29/3/2016.
 */
public class MyApplication extends Application {
    public boolean isServiceRunning() {
        return serviceRunning;
    }

    public void setServiceRunning(boolean serviceRunning) {
        this.serviceRunning = serviceRunning;
    }

    boolean serviceRunning = false;
}
