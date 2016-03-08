package com.amrizal.example.serviceupdateuitester;

import android.app.Service;
import android.content.Intent;
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
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");

        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();

        handler.removeCallbacks(sendUpdatesToUI);
        Log.d(TAG, "onDestroy");
    }

    public void sendResult(String message) {
        Intent intent = new Intent(MYSERVICE_RESULT);
        if(message != null)
            intent.putExtra(MYSERVICE_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }
}
