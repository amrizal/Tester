package com.amrizal.example.locationtester;

import android.Manifest;
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
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MyLocationService extends Service implements LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private int count = 0;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    public MyLocationService() {
    }

    @Override
    public void onCreate() {
        sendMessage("Service::onCreate");

        initLocationRequest();
        buildGoogleApiClient();
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
