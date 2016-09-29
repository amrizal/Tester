package com.amrizal.example.notificationtester;

import android.app.Activity;
import android.app.Application;

/**
 * Created by amrizal.zainuddin on 29/9/2016.
 */
public class NotificationApplication extends Application {
    static  NotificationApplication instance;
    Activity lastActiveActivity;

    public Activity getLastActiveActivity() {
        return lastActiveActivity;
    }

    public void setLastActiveActivity(Activity lastActiveActivity) {
        this.lastActiveActivity = lastActiveActivity;
    }

    public static synchronized NotificationApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        ActivityLifeCycleHandler handler = new ActivityLifeCycleHandler();
        registerActivityLifecycleCallbacks(handler);
    }
}
