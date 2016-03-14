package com.amrizal.example.applicationsingleton;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by amrizal.zainuddin on 8/3/2016.
 */
public class MyApplication extends Application{

    private static MyApplication singleton;

    public MyApplication getInstance(){
        return singleton;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
