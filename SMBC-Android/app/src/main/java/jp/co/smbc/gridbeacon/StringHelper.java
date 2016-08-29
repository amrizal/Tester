package jp.co.smbc.gridbeacon;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by amrizal.zainuddin on 12/8/2016.
 */
public class StringHelper {
    private static final String TAG = StringHelper.class.getSimpleName();

    /**
     * Convert input stream to string
     *
     * @param is InputStream object
     * @return Return string
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8 * 1024);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty())
                    sb.append(line);
            }
            Log.d(TAG, "reader line = " + sb.toString());
        } catch (IOException e) {
            Log.e(TAG, "convertStreamToString(), IOException = ", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "convertStreamToString(), IOException = ", e);
            }
        }

        return sb.toString();
    }
}
