package com.stationalertapp.malaysia.activitytester;

import android.app.Application;
import android.os.Build;
import android.util.Log;

/**

 * Created by amrizal.zainuddin on 1/9/2016.
 */
public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    private boolean wentBackground = true;
    private static MyApplication instance;
    private MemoryBoss mMemoryBoss;

    public MyApplication() {
        instance = this;
    }

    public boolean isWentBackground() {
        return wentBackground;
    }

    public void setWentBackground(boolean wentBackground) {
        this.wentBackground = wentBackground;
        Log.d(TAG, "setWentBackground=" + wentBackground);
    }

    public static MyApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);
    }
}
