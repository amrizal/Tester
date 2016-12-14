package com.amrizal.example.locationtester;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MyLocationService extends Service implements LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int NOTI_ID = 0;
    private static final int TRX_NOTI_ID_APPROVE = 1;
    private static final int TRX_NOTI_ID_REJECT = 2;
    private int count = 0;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    public MyLocationService() {
    }

    @Override
    public void onCreate() {
        sendMessage("Service::onCreate");

        createNotification();

        initLocationRequest();
        buildGoogleApiClient();
    }

    private void createNotification() {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder =  new NotificationCompat.Builder(context);

        Intent pIntent = new Intent(context, MainActivity.class);
        pIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        Intent rejectIntent = new Intent(pIntent);
        rejectIntent.putExtra(EXTRA.STOP_SERVICE, true);

        PendingIntent contentIntent = PendingIntent.getActivity(context, NOTI_ID, pIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent stopPIntent = PendingIntent.getActivity(context, TRX_NOTI_ID_REJECT, rejectIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        String appName = context.getString(R.string.app_name);
        builder.setAutoCancel(true)
                .setContentIntent(null)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(appName)
                .addAction(R.mipmap.ic_launcher, context.getString(R.string.show), contentIntent)
                .addAction(R.mipmap.ic_launcher, context.getString(R.string.stop_service), stopPIntent)
                .setContentText(context.getString(R.string.service_is_running));

        builder.setOngoing(true);

        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        // Trigger the notification
        notificationManager.notify(NOTI_ID, notification);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendMessage("Service::onStartCommand");

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        sendMessage("Service::onBind");
        return null;
    }

    private void sendMessage(String logLine) {
        Intent intent = new Intent(BROADCAST.LOG_LINE);
        intent.putExtra(EXTRA.LOG_LINE, "[" + (count++) + "] " + logLine);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        sendMessage("Service::onDestroy");
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        destroyNotification();
    }

    private void destroyNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    @Override
    public void onLocationChanged(Location location) {
        sendMessage("Lat:" + location.getLatitude() + ", Lon:" + location.getLongitude());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        sendMessage("Service::onConnectionFailed-"+ connectionResult.getErrorMessage());
    }

    @Override
    public void onConnected(Bundle bundle) {
        sendMessage("Service::onConnected");

        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(l != null){
            onLocationChanged(l);
        }

        startLocationUpdate();
    }

    private void startLocationUpdate() {
        sendMessage("Service::startLocationUpdate");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void initLocationRequest() {
        sendMessage("Service::initLocationRequest");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {
        sendMessage("Service::onConnectionSuspended-" + i);
    }
}
