package com.amrizal.example.locationtester;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class MyLocationService extends Service {

    public MyLocationService() {
    }

    @Override
    public void onCreate() {
        sendMessage("Service::onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendMessage("Service::onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        sendMessage("Service::onBind");
        return null;
    }

    private void sendMessage(String logLine) {
        Intent intent = new Intent(BROADCAST.LOG_LINE);
        intent.putExtra(EXTRA.LOG_LINE, logLine);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        sendMessage("Service::onDestroy");
    }
}
