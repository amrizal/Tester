package com.amrizal.example.alarmmanagertester;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by amrizal.zainuddin on 16/2/2016.
 */
// WakefulBroadcastReceiver ensures the device does not go back to sleep
// during the startup of the service
public class BootBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Launch the specified service when this message is received
        Intent startServiceIntent = new Intent(context, MyTestService.class);
        startWakefulService(context, startServiceIntent);
    }
}
