package com.amrizal.example.lifecycletester;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by amrizal.zainuddin on 19/9/2016.
 */
public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    static MyApplication instance;
    private int activityCount;

    public boolean isWentBackground() {
        return wentBackground;
    }

    public void setWentBackground(boolean wentBackground) {
        Log.d(TAG, "setWentBackground=" + wentBackground);
        this.wentBackground = wentBackground;
    }

    private boolean wentBackground = true;

    public synchronized static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initLifecycleCallbacks();
    }

    private void initLifecycleCallbacks() {
        ActivityLifecycleCallbacks callbacks = new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                activityCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                if(activityCount > 0){
                    activityCount--;
                }

                if(activityCount == 0){
                    setWentBackground(true);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
        registerActivityLifecycleCallbacks(callbacks);
    }
}
