package com.amrizal.example.serviceupdateuitester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by amrizal.zainuddin on 29/3/2016.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        if(sharedPreferences.getBoolean(Constants.PREFERENCE_AUTOSTART, false)){
            Intent startServiceIntent = new Intent(context, MyService.class);
            context.startService(startServiceIntent);
        }
    }
}
