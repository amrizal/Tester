package jp.co.smbc.gridbeacon;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BeaconService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_REGISTER_GCM = "jp.co.smbc.gridbeacon.action.ACTION_REGISTER_GCM";
    private static final String ACTION_BAZ = "jp.co.smbc.gridbeacon.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "jp.co.smbc.gridbeacon.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "jp.co.smbc.gridbeacon.extra.PARAM2";
    private static final String EXTRA_SENDERID = "jp.co.smbc.gridbeacon.extra.SENDERID";

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

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BeaconService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
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
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    private void handleActionFoo() {

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
