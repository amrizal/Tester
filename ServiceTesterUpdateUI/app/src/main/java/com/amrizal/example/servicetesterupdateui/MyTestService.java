package com.amrizal.example.servicetesterupdateui;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by amrizal.zainuddin on 16/2/2016.
 */
public class MyTestService extends IntentService {

    public static final String ACTION = "com.codepath.example.servicesdemo.MyTestService";

    public MyTestService() {
        super("MyTestService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // Fetch data passed into the intent on start
        String val = intent.getStringExtra("foo");
        // Construct an Intent tying it to the ACTION (arbitrary event namespace)
        Intent in = new Intent(ACTION);
        // Put extras into the intent as usual
        in.putExtra("resultCode", Activity.RESULT_OK);
        in.putExtra("resultValue", "My Result Value. Passed in: " + val);
        // Fire the broadcast with intent packaged
        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
        // or sendBroadcast(in) for a normal broadcast;
    }
}
