package com.amrizal.example.alarmmanagertester;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by amrizal.zainuddin on 16/2/2016.
 */
public class MyTestService extends IntentService {
    public MyTestService() {
        super("MyTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.i("MyTestService", "Service running");

        // Release the wake lock within onHandleIntent so the device can go back to sleep after the service is launched:
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }
}
