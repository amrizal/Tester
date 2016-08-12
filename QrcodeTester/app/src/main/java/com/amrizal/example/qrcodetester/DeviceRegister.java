package com.amrizal.example.qrcodetester;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class DeviceRegister extends AsyncTask<String, Void, Bundle> {

    private static final String TAG = "DeviceRegister";
    /**
     * Phone phoneSecret for hashing purposes during registration
     */
    private final String phoneSecret = "012c2b5faf89f727";
    private final Context context;
    private final String RESULT = "result";

    private ConnectivityManager cm;
    private SharedPreferences pref;

    private RegisterCallback regListener;

    public DeviceRegister(Context nContext) {
        context = nContext;
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        pref = context.getSharedPreferences(StringRes.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void setListener(RegisterCallback nListener) {
        regListener = nListener;
    }

    private Bundle parseQuery(String query) {
        Log.d(TAG, "parseQuery(), query = " + query);
        // int iResult = -1;
        String[] params = query.split("&");
        String UserID = null;
        String GridCode = null;
        String GooURL = null;
        String Sig = null;

        Bundle result = null;

        if (params != null && params.length != 0) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] != null) {
                    String[] param = params[i].split("=");
                    if (param != null && param.length == 2) {
                        if (param[0].equals("n")) {
                            UserID = URLDecoder.decode(param[1]);
                        } else if (param[0].equals("c")) {
                            GridCode = URLDecoder.decode(param[1]);
                        } else if (param[0].equals("u")) {
                            GooURL = URLDecoder.decode(param[1]);
                        } else if (param[0].equals("s")) {
                            Sig = URLDecoder.decode(param[1]);
                        }
                    }
                }
            }
        }
        if (UserID != null && GridCode != null && GooURL != null && Sig != null) {
            String signature = Utilities.base64Encode(Utilities.createHash(phoneSecret + UserID + GridCode + GooURL));
            Log.d(TAG, "userID=" + UserID + ";sig=" + signature);

            if (signature != null && signature.equals(Sig)) {
                result = new Bundle();
                result.putString(StringRes.PREF_KEY_USERID, UserID);
                result.putString(StringRes.INFO_KEY_GRIDCODE, GridCode);
                result.putString(StringRes.PREF_KEY_GOOURL, GooURL);

                // iResult = Statuscode.SIG_VALID; // valid sig
            } else {
                Log.e(TAG, "Invalid SIG");
                // iResult = Statuscode.SIG_INVALID; // invalid sig
            }

        } else {
            Log.e(TAG, "incomplete param");
            // iResult = Statuscode.SIG_MISSING; // incomplete param
        }

        // Log.d(TAG, "result returned=" + iResult);
        return result;
    }

    private Bundle registerDevice(Bundle info) {
        String sCode = null;
        if (Utilities.isConnectedToInternet(cm)) {
            // Creating form params
            try {
                // isTag = false;

                final String gooURL = info.getString(StringRes.PREF_KEY_GOOURL);
                final String userID = info.getString(StringRes.PREF_KEY_USERID);
                final String gridCode = info.getString(StringRes.INFO_KEY_GRIDCODE);

                if (gooURL != null && !gooURL.trim().equals("")) {
                    StringBuffer url = new StringBuffer();
                    url.append(gooURL + "?cmd=reg");
                    url.append("&n=" + userID);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    final String currentTime = sdf.format(new Date(System.currentTimeMillis())).toString();

                    String temp = Utilities.hexEncode(Utilities.createHash(gridCode + currentTime));
                    final String seed = temp.substring(0, 8);

                    info.putString(StringRes.PREF_KEY_SEED, seed);
                    // Create hash for device ID

                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    if (tm != null && tm.getDeviceId() != null) {
                        Log.d(TAG, "Creating device identifier...");

                        temp = Utilities.hexEncode(Utilities.createHash(seed + tm.getDeviceId()));
                        info.putString(StringRes.PREF_KEY_DEVICEID, temp);

                        Editor editor = pref.edit();
                        editor.putString(StringRes.PREF_KEY_DEVICEID, temp);
                        editor.commit();
                    } else {
                        Resources res = context.getResources();
                        // displayMessage(res.getString(R.string.FATAL_ERROR));
                        Log.e(TAG, res.getString(R.string.FATAL_ERROR));
                        // finish();
                    }
                    final String deviceID = temp;

                    url.append("&id=" + deviceID);
                    url.append("&c=" + gridCode);
                    url.append("&p=" + currentTime);
                    url.append("&k=" + seed);

                    String version = null;
                    try {
                        version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                        version += "_a" + VERSION.RELEASE;
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (version == null) {
                        version = "";
                    }
                    url.append("&v=" + URLEncoder.encode(version));

                    String sig = phoneSecret + "reg" + userID + gridCode + currentTime + deviceID + seed + version;
                    sig = Utilities.hexEncode(Utilities.createHash(sig));
                    url.append("&s=" + sig);
                    Log.d(TAG, url.toString());
                    sCode = Utilities.executeGET(url.toString());

                    if (sCode != null) {
                        Log.d(TAG, "registerDevice return " + sCode);
                    }
                } else {
                    sCode = Statuscode.FAILED_CONNECT_GRID;
                }
            } catch (Exception e) {
                Log.e(TAG, "registerDevice(), Exception thrown. " + e);
                resetSettings();
                // finish();
            }
        } else {
            sCode = Statuscode.FAILED_CONNECT_GRID;
        }

        Log.d(TAG, "checkRegister(),codeQuery() return = " + sCode);

        return codeQuery(sCode, info);
    }

    /**
     * Return code query
     */
    private Bundle codeQuery(String sResponse, Bundle info) {
        Log.d(TAG, "response=" + sResponse);

        String sCode = null;
        if (sResponse == null || sResponse.trim().equals("") || sResponse.equals(Statuscode.FAILED_CONNECT_GRID)) {
            sCode = Statuscode.FAILED_CONNECT_GRID;
        } else {
            String[] query = sResponse.split("&");
            if (query != null) {
                for (int i = 0; i < query.length; i++) {
                    if (query[i] != null) {
                        String[] param = query[i].split("=");
                        if (param != null && param.length == 2) {
                            if (param[0].equals("code")) {
                                sCode = param[1];
                            } else if (param[0].equals(StringRes.PREF_KEY_TOUT) && info != null) {

                                info.putInt(StringRes.PREF_KEY_TOUT, Integer.parseInt(param[1]));

                                if (info.getInt(StringRes.PREF_KEY_TOUT) < 1) {
                                    info.putInt(StringRes.PREF_KEY_TOUT, IntegerRes.DEFAULT_TIMEOUT);// settingInfo.setTimeout(60*60)
                                }
                            } else if (param[0].equals(StringRes.PREF_KEY_USERNAME) && info != null) {
                                info.putString(StringRes.PREF_KEY_USERNAME, param[1]);
                            }
                        }
                    }
                }
            }
            if (sCode == null) {
                sCode = Statuscode.FAILED_CONNECT_GRID;
            }
        }
        if (info == null) {
            info = new Bundle();
        }
        info.putString(RESULT, sCode);
        return info;
    }

    /**
     * Respond message from code returned
     *
     * @param info Info to process
     */
    private void respondToCode(Bundle info) {
        // Log.d(TAG, "Respond to code, isTag=" + isTag);

        int iMessage = 0;

        if (info != null) {

            String sCode = info.getString(RESULT);

            // gooStarted = false;
            // boolean bErr = false;

            try {

                if (sCode != null && !sCode.trim().equals("")) {

                    if (sCode.equals(Statuscode.FAILED_CONNECT_GRID)) { // reg
                        iMessage = R.string.ERR_101; // cannot connect to GOO server
                        // bErr = true;
                    } else if (sCode.equals(Statuscode.FAILED_CONNECT_DB)) { // reg
                        iMessage = R.string.ERR_102; // DB error
                        // bErr = true;
                    } else if (sCode.equals(Statuscode.COMMAND_REUSE)) {
                        iMessage = R.string.ERR_110; // invalid command
                    } else if (sCode.equals(Statuscode.USERID_NOT_EXIST)) {
                        // reset();
                        // updatePrefUI();
                        iMessage = R.string.ERR_109; // user not found
                    } else if (sCode.equals(Statuscode.PHONE_ALREADY_TAGGED)) {
                        iMessage = R.string.ERR_103; // phone registered with another user
                        // bErr = true;
                    } else if (sCode.equals(Statuscode.USER_ALREADY_TAGGED)) {
                        iMessage = R.string.ERR_104; // username registered with another phone
                        // bErr = true;
                    } else if (sCode.equals(Statuscode.FAILED_TAG_USER)) {
                        iMessage = R.string.ERR_105; // DB error
                    } else if (sCode.equals(Statuscode.FAILED_UNTAG)) {
                        iMessage = R.string.ERR_108; // DB error
                    } else if (sCode.equals(Statuscode.TAG_SUCCESS)) { // successfully tagged

                        final String userID = info.getString(StringRes.PREF_KEY_USERID);
                        final String gooURL = info.getString(StringRes.PREF_KEY_GOOURL);
                        final String seed = info.getString(StringRes.PREF_KEY_SEED);
                        final int tout = info.getInt(StringRes.PREF_KEY_TOUT);
                        final String uname = info.getString(StringRes.PREF_KEY_USERNAME);

                        Editor editor = pref.edit();
                        editor.putString(StringRes.PREF_KEY_USERID, userID);
                        editor.putString(StringRes.PREF_KEY_GOOURL, gooURL);
                        editor.putString(StringRes.PREF_KEY_SEED, seed);

                        editor.putInt(StringRes.PREF_KEY_TOUT, tout * 60);// change to seconds
                        editor.putString(StringRes.PREF_KEY_USERNAME, uname);

                        Time currentTime = new Time();
                        currentTime.set(System.currentTimeMillis());
                        editor.putString(StringRes.PREF_KEY_TAG_DATE, currentTime.format2445());

                        editor.commit();

                        Log.d(TAG, "gooURL=" + gooURL + ";userID=" + userID + ";seed=" + seed + ";tout=" + tout + "; uname=" + uname);

                        // final String regID = pref.getString(StringRes.PREF_KEY_GCM_REGISTRATION_ID, null);

                        // if (uname != null && !uname.trim().equals("")) {
                        // if (regID == null || regID.trim().equals("")) {
                        // getGcmID();
                        // }
                        // }

                        // updateSwitchState(true);
                        // startCountdownTimer();
                        Log.d(TAG, "response to code, done tag");
                        regListener.OnRegisterResult(true);
                    } else if (sCode.equals(Statuscode.UNTAG_SUCCESS)) { // successfully turned off beacon
                        iMessage = R.string.notAllowed;
                    } else if (sCode.equals(Statuscode.INVALID_REG)) {
                        iMessage = R.string.invalidReg;
                    } else {
                        try {
                            Integer code = Integer.parseInt(sCode);
                            if (code == 5) {
                                iMessage = R.string.ERR_005; // gac invalid
                            } else if (code >= 901) { // reg
                                // displayMessage(res.getString(R.string.ERR_900) + "(" + code.toString() + ")");
                                iMessage = R.string.ERR_900;
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "respondToCode(), sCode is not integer");
                        }
                    }

                    if (!sCode.equals(Statuscode.TAG_SUCCESS)) {
                        Toast.makeText(context, context.getResources().getString(iMessage), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "respondToCode(), iMessage=" + context.getResources().getString(iMessage));
                        regListener.OnRegisterResult(false);
                    }

                }

            } catch (Exception e) {
                Log.e(TAG, "respondToCode(), Exception thrown. ", e);
            }
        }
    }

    /**
     * Reset shared preferences
     */
    private void resetSettings() {
        // SharedPreferences
        Log.d(TAG, "removing all settings...");

        Editor editor = pref.edit();
        editor.remove(StringRes.PREF_KEY_USERID);
        editor.remove(StringRes.PREF_KEY_GOOURL);
        editor.remove(StringRes.PREF_KEY_SEED);
        // editor.remove("intv");
        editor.remove(StringRes.PREF_KEY_TOUT);
        editor.remove(StringRes.PREF_KEY_USERNAME);
        // editor.remove(StringRes.PREF_KEY_GCM_REGISTRATION_ID);
        // editor.remove("tag_date");
        // editor.remove("con_tag_date");
        // editor.remove("con_update_date");
        editor.remove(StringRes.PREF_KEY_DEVICEID);
        editor.commit();
    }

    @Override
    protected Bundle doInBackground(String... param) {
        String query = null;
        if (param != null) {
            query = param[0];
        }

        if (query != null && !query.trim().equals("")) {
            Bundle iResult = parseQuery(query);
            if (iResult != null) { // valid sig
                return registerDevice(iResult);
                // Log.d(TAG, "register Result=" + result);
            }

        }
        Log.d(TAG, "Invalid format type result!");
        Bundle result = new Bundle();
        result.putString(RESULT, Statuscode.INVALID_REG);
        return result;

    }

    @Override
    protected void onPostExecute(Bundle result) {
        // mDisplay.append(msg + "\n");

        Log.d(TAG, "running onPostExecute()...");
        respondToCode(result);

    }

    public interface RegisterCallback {
        public void OnRegisterResult(boolean isSuccess);
    }
}
