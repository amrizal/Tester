package com.amrizal.example.serviceupdateuitester;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by amrizal.zainuddin on 8/3/2016.
 */
public class MyService extends Service {
    private static final String TAG = MyService.class.getSimpleName();

    public static final String MYSERVICE_RESULT = "com.amrizal.example.serviceupdateuitester.MyService.REQUEST_PROCESSED";
    public static final String MYSERVICE_MESSAGE = "com.amrizal.example.serviceupdateuitester.MyService.REQUEST_MESSAGE";
    private LocalBroadcastManager broadcaster;
    private final Handler handler = new Handler();
    SharedPreferences sharedPreferences;
    int counter = 0;

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 1000); // 5 seconds
        }
    };

    private void DisplayLoggingInfo() {
        counter++;
        sendResult(String.valueOf(counter));
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");

        broadcaster = LocalBroadcastManager.getInstance(this);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");

        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

        sharedPreferences.edit().putBoolean(Constants.PREFERENCE_SERVICE_RUNNING, true).commit();

        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        sharedPreferences.edit().putBoolean(Constants.PREFERENCE_SERVICE_RUNNING, false).commit();

        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();

        handler.removeCallbacks(sendUpdatesToUI);
        Log.d(TAG, "onDestroy");
    }

    public void sendResult(String message) {
        Intent intent = new Intent(MYSERVICE_RESULT);
        if(message != null)
            intent.putExtra(MYSERVICE_MESSAGE, message);
        broadcaster.sendBroadcast(intent);


        if(counter % 10  == 0){
            final Intent activity = new Intent(this, AlertActivity.class);
            activity.putExtras(intent.getExtras());
            activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            activity.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(activity);
        }
    }
}
