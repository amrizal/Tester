package jp.co.smbc.gridbeacon;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by amrizal.zainuddin on 12/8/2016.
 */
public class BeaconUtility {
    private static final String TAG = BeaconUtility.class.getSimpleName();

    public static final String SENDER_ID = "942045107961";
    /**
     * Merchant secret for hashing purposes during registration
     */
    public final static String GOO_SECRET = "8d3bd4d1762914f8";

    /**
     * Merchant default timestamp for validation of return code from server
     */
    public final static String GOO_MTS = "30";
    public static final int GOO_SUCCESS = 200;

    public static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return sdf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * Check whether the given timestamp is more than the given limit
     *
     * @param mts        The limit time stamp
     * @param sTimeStamp The timestamp to be check
     * @return
     */
    public static boolean isTimestampValid(String mts, String sTimeStamp) {
        int tsLimit = 0;
        boolean tsValid = false;
        long expiryStart = 0, expiryEnd = 0;
        try {
            if (mts != null && !mts.isEmpty()) {
                tsLimit = Integer.parseInt(mts);
            }

            // Log.d(TAG, "mtsValid: mts = " + sTimeStamp);
            //Log.d(TAG, "mtsValid: tsLimit = " + tsLimit);

            if (!sTimeStamp.trim().isEmpty()) {
                // change mts (GMT) to Date
                SimpleDateFormat inDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                Date mercDate = inDate.parse(sTimeStamp);

                // get local time in GMT
                SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
                SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

                Date localGMTDate = dateFormatLocal.parse(dateFormatGmt.format(new Date()));

                // Log.d(TAG, "local time in GMT " + localGMTDate);
                // Log.d(TAG, "merc time in GMT " + mercDate);

                long current = localGMTDate.getTime();
                expiryStart = mercDate.getTime() - (tsLimit * 60 * 1000); // 60*1000
                expiryEnd = mercDate.getTime() + (tsLimit * 60 * 1000); // 60*1000
                // is one
                // minute

                // Log.d(TAG, "mtsValid: current time = " + current);
                // Log.d(TAG, "mtsValid: expiry time = " + expiry);

                if (current >= expiryStart && current <= expiryEnd) {
                    tsValid = true;
                }

                //Log.d(TAG, "mtsValid = " + tsValid);
            }
        } catch (Exception e) {
            tsValid = false;
            Log.e(TAG, "function = mtsValid(); desc = exception thrown;", e);
        }
        return tsValid;
    }
}
