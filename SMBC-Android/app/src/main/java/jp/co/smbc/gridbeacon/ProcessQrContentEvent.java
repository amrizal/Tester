package jp.co.smbc.gridbeacon;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by amrizal.zainuddin on 12/8/2016.
 */
public class ProcessQrContentEvent {

    Context context;
    String eventName;
    String content;

    public ProcessQrContentEvent(Context context, String eventName, String content) {
        this.content = content;
        this.eventName = eventName;
        this.context = context;
    }

    public void HandleEvent(){
        QrResult result = new QrResult(content);
        Intent intent = new Intent(eventName);
        intent.putExtra(BeaconConstants.EVENT_RESULT, BeaconConstants.EVENT_FAILED);
        if(result.isValid()){
            intent.putExtra(BeaconConstants.EVENT_RESULT, BeaconConstants.EVENT_SUCCESS);
            intent.putExtra(BeaconConstants.GID, result.getGid());
            intent.putExtra(BeaconConstants.VDID, result.getVdid());
            intent.putExtra(BeaconConstants.URL, result.getUrl());
            intent.putExtra(BeaconConstants.S, result.getS());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }
}
