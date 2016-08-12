package jp.co.smbc.gridbeacon;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by amrizal.zainuddin on 12/8/2016.
 */
public class RegisterGcmEvent {

    Context context;
    String senderId;
    String eventName;
    private String token;

    public RegisterGcmEvent(Context context, String eventName, String senderId) {
        this.context = context;
        this.senderId = senderId;
        this.eventName = eventName;
    }

    public void handleEvent() {
        Intent intent = new Intent(eventName);
        intent.putExtra(BeaconConstants.EVENT_RESULT, BeaconConstants.EVENT_FAILED);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!BeaconUtility.isConnectedToInternet(cm)) {
            intent.putExtra(BeaconConstants.FAILURE_MESSAGE, context.getString(R.string.no_internet_connection));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            return;
        }

        try {
            token = InstanceID.getInstance(context).getToken(BeaconConstants.SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            intent.putExtra(BeaconConstants.EVENT_RESULT, BeaconConstants.EVENT_SUCCESS);
            intent.putExtra(BeaconConstants.CLOUDID, token);

            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } catch (IOException e) {
            intent.putExtra(BeaconConstants.FAILURE_MESSAGE, context.getString(R.string.failed_to_create_gcm_token));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
