package jp.co.smbc.gridbeacon;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

/**
 * Created by amrizal.zainuddin on 12/8/2016.
 */
public class RegisterBeaconEvent implements PushService.OnPushResult{
    Context context;
    String eventName;
    String gid;
    String vdid;
    String cloudid;
    String url;
    private String ts;
    private String version;

    public RegisterBeaconEvent(Context context, String eventName, String gid, String vdid, String cloudid, String url) {
        this.context = context;
        this.eventName = eventName;
        this.gid = gid;
        this.url = url;
        this.vdid = vdid;
        this.cloudid = cloudid;
    }

    public void handleEvent() {
        registerDevice();
    }

    public void registerDevice() {
        int iCode = -1;
        PushResult pResult = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Intent intent = new Intent(eventName);

        if (!NetworkUtility.isConnectedToInternet(cm)) {
            intent.putExtra(BeaconConstants.EVENT_RESULT, StatusCode.FAILED_CONNECT_GRID);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            return;
        }

        if (url.isEmpty()) {
            intent.putExtra(BeaconConstants.EVENT_RESULT, StatusCode.INVALID_SETTING);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            return;
        }

        // Create hash for device ID
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null || tm.getDeviceId() == null) {
            intent.putExtra(BeaconConstants.EVENT_RESULT, StatusCode.FAILED_GET_DEVICEID);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            return;
        }

        ts = BeaconUtility.getCurrentTimestamp();
        version = BuildConfig.VERSION_NAME;
        version += "_a" + Build.VERSION.RELEASE;
        String deviceId = Crypto.generateSIG(BeaconUtility.GOO_SECRET, tm.getDeviceId() + gid + ts);
        String signature = Crypto.generateSIG(BeaconUtility.GOO_SECRET, gid + vdid + deviceId + cloudid + ts + version);

        StringBuffer serverUrl = new StringBuffer();
        serverUrl.append(url + "/spapi/beacon/reg/qr");
        serverUrl.append("/" + gid);
        serverUrl.append("/" + vdid);

        ContentValues values = new ContentValues();
        values.put("gid", gid);
        values.put("vdid", vdid);
        values.put("did", deviceId);
        values.put("cloudid", cloudid);
        values.put("ver", version);
        values.put("ts", ts);
        values.put("s", signature);

        PushService pushService = new PushService(context);
        pushService.setListener(this, false, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            pushService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BeaconConstants.POST, serverUrl.toString(), values);
        } else {
            pushService.execute(BeaconConstants.POST, serverUrl.toString(), values);
        }
    }

    @Override
    public void onPushResult(PushResult pResult, boolean bShowDetail, boolean bShowError) {
        handleResult(pResult);
    }

    /**
     * Validate whether the return result is from GRID server and not expired
     *
     * @param pResult PushResult object
     */
    private void handleResult(PushResult pResult) {
        JsonResult jResult = null;
        int iCode = -1;
        Intent intent = new Intent(BeaconService.ACTION_REGISTER_BEACON);
        if (pResult != null) {
            jResult = pResult.getJResult();

            if (jResult != null) {
                if (BeaconUtility.isTimestampValid(BeaconUtility.GOO_MTS, jResult.getTimestamp())) {
                    String sig = null;
                    if (pResult.getCode() == 404 || pResult.getCode() == 422 || pResult.getCode() == 500) { // extra
                        //Log.d(TAG, "handleResult(), error param = " + jResult.getError());
                        sig = Crypto.generateSIG(BeaconUtility.GOO_SECRET, pResult.getCode() + jResult.getError() + jResult.getTimestamp());
                    } else {
                        sig = Crypto.generateSIG(BeaconUtility.GOO_SECRET, pResult.getCode()
                                + jResult.getEncSeed()
                                + jResult.getHashSeed()
                                + jResult.getVersion()
                                + jResult.getMts()
                                + jResult.getTimestamp());
                        //sig = Crypto.generateSIG(BeaconCode.GOO_SECRET, pResult.getCode() + jResult.getGuID() + jResult.getTimestamp());
                    }

                    if (sig.equals(jResult.getSignature())) {
                        iCode = pResult.getCode();
                    } else {
                        iCode = StatusCode.SIG_INVALID;
                    }
                } else {
                    iCode = StatusCode.TS_EXPIRED;
                }
            } else {
                iCode = pResult.getCode();
            }

            intent.putExtra(BeaconConstants.EVENT_RESULT, iCode);
            intent.putExtra(BeaconConstants.HASH_SEED, jResult.getHashSeed());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }
}
