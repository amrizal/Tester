package com.amrizal.example.notificationtester;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by amrizal.zainuddin on 27/9/2016.
 */
public class CloudIdService extends FirebaseInstanceIdService {
    private static final String TAG = CloudIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        Intent intent = new Intent(ACTION.FIREBASE_TOKEN_UPDATED);
        intent.putExtra(EXTRA.CLOUD_TOKEN, refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
