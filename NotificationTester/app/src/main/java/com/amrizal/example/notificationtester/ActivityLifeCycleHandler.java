package com.amrizal.example.notificationtester;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by amrizal.zainuddin on 29/9/2016.
 */
public class ActivityLifeCycleHandler implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = ActivityLifeCycleHandler.class.getSimpleName();

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        NotificationApplication.getInstance().setLastLocalClassName(activity.getLocalClassName());
        activity.getLocalClassName();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "onActivityResumed-" + activity.getLocalClassName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "onActivityPaused-" + activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "onActivityStopped-" + activity.getLocalClassName());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        Log.d(TAG, "onActivitySaveInstanceState-" + activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "onActivityDestroyed-" + activity.getLocalClassName());
    }
}
