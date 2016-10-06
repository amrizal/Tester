package com.stationalertapp.malaysia.activitytester;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MyIntentService extends IntentService {
    private static final String TAG = MyIntentService.class.getSimpleName();
    public static final String DELAY = "com.stationalertapp.malaysia.activitytester.DELAY";

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        long wait = intent.getLongExtra(DELAY, 0);
        long tick = System.currentTimeMillis();
        while(true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

            if((System.currentTimeMillis() - tick) > wait){
                break;
            }
        }

        if(MyApplication.getInstance().isScreenOn(this)){
            Log.d(TAG, "Screen is ON");
        }else{
            Log.d(TAG, "Screen is OFF");
        }
    }
}
