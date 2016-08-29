package jp.co.smbc.gridbeacon;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import jp.co.smbc.gridbeacon.zxing.CaptureActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BeaconService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_PROCESS_QR_CONTENT = "jp.co.smbc.gridbeacon.action.PROCESS_QR_CONTENT";
    public static final String ACTION_REGISTER_GCM = "jp.co.smbc.gridbeacon.action.ACTION_REGISTER_GCM";
    public static final String ACTION_REGISTER_BEACON = "jp.co.smbc.gridbeacon.action.ACTION_REGISTER_BEACON";


    private static final String EXTRA_SENDERID = "jp.co.smbc.gridbeacon.extra.SENDERID";
    private static final String EXTRA_GUID = "jp.co.smbc.gridbeacon.extra.GUID";
    private static final String EXTRA_URL = "jp.co.smbc.gridbeacon.extra.URL";
    private static final String EXTRA_VDID = "jp.co.smbc.gridbeacon.extra.VDID";
    private static final String EXTRA_CLOUDID = "jp.co.smbc.gridbeacon.extra.CLOUDID";
    private static final String EXTRA_QR_CONTENT = "jp.co.smbc.gridbeacon.extra.QR_CONTENT";

    public BeaconService() {
        super("BeaconService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void registerGcm(Context context, String senderId) {
        Intent intent = new Intent(context, BeaconService.class);
        intent.setAction(ACTION_REGISTER_GCM);
        intent.putExtra(EXTRA_SENDERID, senderId);
        context.startService(intent);
    }

    public static void processQrContent(Context context, String content) {
        Intent intent = new Intent(context, BeaconService.class);
        intent.setAction(ACTION_PROCESS_QR_CONTENT);
        intent.putExtra(EXTRA_QR_CONTENT, content);
        context.startService(intent);
    }

    public static void registerBeacon(Context context, String gid, String vdid, String cloudid, String url) {
        Intent intent = new Intent(context, BeaconService.class);
        intent.setAction(ACTION_REGISTER_BEACON);
        intent.putExtra(EXTRA_GUID, gid);
        intent.putExtra(EXTRA_VDID, vdid);
        intent.putExtra(EXTRA_CLOUDID, cloudid);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REGISTER_GCM.equals(action)) {
                final String senderId = intent.getStringExtra(EXTRA_SENDERID);
                RegisterGcmEvent event = new RegisterGcmEvent(this, ACTION_REGISTER_GCM, senderId);
                event.handleEvent();
            }else if (ACTION_PROCESS_QR_CONTENT.equals(action)) {
                final String content = intent.getStringExtra(EXTRA_QR_CONTENT);
                ProcessQrContentEvent event = new ProcessQrContentEvent(this, ACTION_PROCESS_QR_CONTENT, content);
                event.HandleEvent();
            }else if(ACTION_REGISTER_BEACON.equals(action)){
                final String gid = intent.getStringExtra(EXTRA_GUID);
                final String url = intent.getStringExtra(EXTRA_URL);
                final String vdid = intent.getStringExtra(EXTRA_VDID);
                final String cloudid = intent.getStringExtra(EXTRA_CLOUDID);

                RegisterBeaconEvent event = new RegisterBeaconEvent(this, ACTION_REGISTER_BEACON, gid, vdid, cloudid, url);
                event.handleEvent();
            }
        }
    }
}
