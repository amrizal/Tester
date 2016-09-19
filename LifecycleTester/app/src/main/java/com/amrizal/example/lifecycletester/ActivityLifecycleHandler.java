package com.amrizal.example.lifecycletester;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by amrizal.zainuddin on 19/9/2016.
 */
public class ActivityLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    private int activityCount;

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
            MyApplication.getInstance().setWentBackground(true);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
