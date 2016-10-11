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
        Intent intent = new Intent(ACTION.FIREBASE_TOKEN_UPDATED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
