package com.stationalertapp.malaysia.activitytester;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;

/**
 * Created by amrizal.zainuddin on 1/9/2016.
 */
public class MemoryBoss implements ComponentCallbacks2 {
    @Override
    public void onTrimMemory(int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            MyApplication.getInstance().setWentBackground(true);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }
}
