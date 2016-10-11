package com.amrizal.example.notificationtester;

import android.app.Activity;
import android.app.Application;

/**
 * Created by amrizal.zainuddin on 29/9/2016.
 */
public class NotificationApplication extends Application {
    static  NotificationApplication instance;

    public static synchronized NotificationApplication getInstance(){
        return instance;
    }

    public String getLastLocalClassName() {
        return lastLocalClassName;
    }

    public void setLastLocalClassName(String lastLocalClassName) {
        this.lastLocalClassName = lastLocalClassName;
    }

    private String lastLocalClassName;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        ActivityLifeCycleHandler handler = new ActivityLifeCycleHandler();
        registerActivityLifecycleCallbacks(handler);
    }
}
