package com.amrizal.example.qrcodetester;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class DeviceUnregister {

	private static final String TAG = "DeviceUnregister";
	private final Context context;
	private final String merchantSecret = "21a6f2a0116120bc";
	private final String mid = "MY_MYNIC_1";
	private final String ip = "0.0.0.0";
	private UnregisterCallback unregListener = null;
	private ConnectivityManager cm;
	private SharedPreferences pref;

	public DeviceUnregister(Context inContext) {
		context = inContext;
		pref = context.getSharedPreferences(StringRes.PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public void setListener(UnregisterCallback inListener) {
		unregListener = inListener;
	}

	/**
	 * Call to unregister device
	 */
	public void unregisterDevice() {
		cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (!Utilities.isConnectedToInternet(cm)) {
			// cannot connect to GOO server
			Toast.makeText(context, context.getResources().getString(R.string.ERR_101), Toast.LENGTH_LONG).show();
			unregListener.OnUnregisterResult(Statuscode.FAILED_CONNECT_GRID);
			return;
		}

		Runnable newThread = new Runnable() {
			public void run() {
				try {

					Looper.prepare();
					Bundle info = getInfo();

					// Creating form params
					if (info != null) {
						String unregResult = unregisterDevice(info);
						Log.d(TAG, "-----Unregister device----- result = " + unregResult);

						// if it is not connection error, just proceed to reset settings
						if (!unregResult.equals(Statuscode.FAILED_CONNECT_GRID)) {
							Log.d(TAG, "-----Unregister device proceed-----");
							resetSettings();
							unregListener.OnUnregisterResult(unregResult);
						} else {
							Toast.makeText(context, context.getResources().getString(R.string.ERR_101), Toast.LENGTH_LONG).show();
							unregListener.OnUnregisterResult(Statuscode.FAILED_CONNECT_GRID);
						}
					} else {
						Log.d(TAG, "-----No settings. Proceed to reset settings-----");
						resetSettings();
						unregListener.OnUnregisterResult(Statuscode.USERID_NOT_EXIST);
					}
					Looper.loop();
				} catch (Exception e) {
					Log.e(TAG, "unregDevice(), Exception thrown. ", e);
					Toast.makeText(context, context.getResources().getString(R.string.ERR_101), Toast.LENGTH_LONG).show();
					unregListener.OnUnregisterResult(Statuscode.FAILED_CONNECT_GRID);
				}
			}
		};
		new Thread(newThread).start();
	}

	/**
	 * Connect to GRID to unregister device
	 *
	 * @param info registration details saved in shared preference, may obtain using getInfo()
	 * @return result code
	 */
	private String unregisterDevice(Bundle info) {
		String sCode = null;
		if (Utilities.isConnectedToInternet(cm)) {
			// Creating form params
			try {
				final String gooURL = info.getString(StringRes.PREF_KEY_GOOURL);
				final String uname = info.getString(StringRes.PREF_KEY_USERNAME);

				if (gooURL != null && !gooURL.trim().equals("")) {
					StringBuffer url = new StringBuffer();
					url.append(gooURL + "?cmd=reset");
					url.append("&uname=" + URLEncoder.encode(Scrambler.encrypt(merchantSecret, uname), "UTF-8"));

					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
					final String mts = sdf.format(new Date(System.currentTimeMillis())).toString();

					url.append("&mid=" + mid);
					url.append("&mts=" + mts);

					url.append("&ip=" + ip);

					String sig = merchantSecret + "reset" + Scrambler.encrypt(merchantSecret, uname) + mid + mts + ip;
					sig = Utilities.hexEncode(Utilities.createHash(sig));
					url.append("&sig=" + URLEncoder.encode(sig, "UTF-8"));
					Log.d(TAG, url.toString());
					sCode = Utilities.executeGET(url.toString());
				} else {
					sCode = Statuscode.URL_MISSING;
				}
			} catch (Exception e) {
				Log.e(TAG, "unregisterDevice(), Exception thrown. " + e);
			}
		} else {
			sCode = Statuscode.FAILED_CONNECT_GRID;
		}

		Log.d(TAG, "unregisterDevice() return = " + sCode);

		return sCode;
	}

	private Bundle getInfo() {
		Bundle bundle = null;

		String gooURL = pref.getString(StringRes.PREF_KEY_GOOURL, null);
		String uname = pref.getString(StringRes.PREF_KEY_USERNAME, null);

		Log.d(TAG, "gooURL = " + gooURL + " , uname = " + uname);
		// return bundle only when none of the info is null or empty string
		if (gooURL != null && !gooURL.equals("") && uname != null && !uname.equals("")) {
			bundle = new Bundle();
			bundle.putString(StringRes.PREF_KEY_GOOURL, gooURL);
			bundle.putString(StringRes.PREF_KEY_USERNAME, uname);
		}

		return bundle;
	}

	/**
	 * Reset shared preferences
	 */
	private void resetSettings() {
		// SharedPreferences
		Log.d(TAG, "removing all settings...");

		SharedPreferences sharedPreferences = context.getSharedPreferences(StringRes.PREFERENCE_NAME, 0);
		Editor editor = sharedPreferences.edit();
		editor.remove(StringRes.PREF_KEY_USERID);
		editor.remove(StringRes.PREF_KEY_GOOURL);
		editor.remove(StringRes.PREF_KEY_SEED);
		// editor.remove("intv");
		editor.remove(StringRes.PREF_KEY_TOUT);
		editor.remove(StringRes.PREF_KEY_USERNAME);
		// editor.remove(StringRes.PREF_KEY_GCM_REGISTRATION_ID);
		editor.remove(StringRes.PREF_KEY_TAG_DATE);
		// editor.remove("con_tag_date");
		// editor.remove("con_update_date");
		editor.remove(StringRes.PREF_KEY_DEVICEID);
		editor.remove(StringRes.PREF_KEY_START_TIME);
		editor.commit();
	}

	/**
	 * Return unregister result
	 */
	public interface UnregisterCallback {
		public void OnUnregisterResult(String sResultCode);
	}

}
