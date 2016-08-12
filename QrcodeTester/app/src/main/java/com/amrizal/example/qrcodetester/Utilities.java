package com.amrizal.example.qrcodetester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Utilities class
 * 
 * @author Hoon Sin
 * 
 */

public class Utilities {
    /** App tag **/
    public static final String appTAG = "GRID Utilities";

    // Set connection timeout to 5 seconds
    public static int timeoutConnection = 5000;

    // Set socket idling timeout to 10 seconds
    public static int timeoutSocket = 10000;

    /** Create http client **/
    public static DefaultHttpClient createHttpClient() {
        // Create HTTP client
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
        httpClient.setParams(httpParams);
        return httpClient;
    }

    /** Check for internet connection **/
    public static Boolean isConnectedToInternet(ConnectivityManager cm) {
        Boolean isConnectedToInternet = false;
        if (cm != null) {
            NetworkInfo[] networks = cm.getAllNetworkInfo();
            if (networks != null) {
                int i = networks.length - 1;
                while (i > -1) {
                    NetworkInfo networkInfo = networks[i];
                    if (networkInfo != null && networkInfo.isConnected()) {
                        isConnectedToInternet = true;
                        break;
                    }
                    i--;
                }
            }
        }
        return isConnectedToInternet;
    }

    /** Called to create hex string from SHA-1 hash **/
    public static String hexEncode(byte[] aInput) {
        StringBuilder result = new StringBuilder();
        for (int idx = 0; idx < aInput.length; ++idx) {
            byte b = aInput[idx];
            result.append(Integer.toHexString((b & 0XF0) >> 4));
            result.append(Integer.toHexString(b & 0X0F));
        }
        return result.toString().toUpperCase();
    }

    /** Called to create base64 string from SHA-1 hash **/
    public static String base64Encode(byte[] aInput) {
        String base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        StringBuilder result = new StringBuilder();
        int srcLen = aInput.length;
        // tackle the source in 3's as conveniently 4 Base64 nibbles fit into 3 bytes
        for (int i = 0; i < srcLen; i += 3) {
            int input = -1;
            int len = -1;
            if (i + 2 < srcLen) {
                len = 3;
                input = ((aInput[i] & 0xff) << 16) | ((aInput[i + 1] & 0xff) << 8) | (aInput[i + 2] & 0xff);
            } else if (i + 1 < srcLen) {
                len = 2;
                input = ((aInput[i] & 0xff) << 8) | (aInput[i + 1] & 0xff);
            } else if (i < srcLen) {
                len = 1;
                input = aInput[i] & 0xff;
            }
            for (int nib = 0; nib < len + 1; nib++) {
                int index = 8 * len - (nib + 1) * 6;
                if (index >= 0) {
                    result.append(base64.charAt(input >> index & 0x3f));
                } else {
                    index = -1 * index;
                    result.append(base64.charAt(input << index & 0x3f));
                }
            }
            if (len < 3) {
                if (len == 2) {
                    result.append("=");
                } else if (len == 1) {
                    result.append("==");
                }
                break;
            }
        }
        return result.toString();
    }

    /** Called to create a hash **/
    public static byte[] createHash(String input) {
        byte[] sha1 = new byte[20];
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            try {
                messageDigest.update(input.getBytes("UTF-8"), 0, input.length());
                sha1 = messageDigest.digest();
            } catch (UnsupportedEncodingException e) {
                Log.e(appTAG, "Cannot create hash. Detail: " + e.getMessage());
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(appTAG, "Cannot create hash. Detail: " + e.getMessage());
        }
        return sha1;
    }

    public static String executeGET(String url) {
        DefaultHttpClient httpClient = createHttpClient();
        HttpGet httpget = new HttpGet(url);
        String sCode = null;
        try {
            // perform a http post request
            HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = null;
            // read the response
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                entity = response.getEntity();
            }
            if (entity != null) {
                InputStream is = entity.getContent();
                if (is != null) {
                    sCode = convertStreamToString(is);
                    sCode.trim();

                    /*
                     * BufferedReader reader = new BufferedReader(new InputStreamReader(is),1); sCode = reader.readLine(); is.close();
                     */
                } else {
                    Log.e(appTAG, "Invalid HTTP response!");
                    return ("code=101");
                }
            } else {
                Log.e(appTAG, "Invalid HTTP response!");
                return ("code=101");
            }
        } catch (ClientProtocolException e) {
            Log.e(appTAG, "Cannot execute HTTP GET. Detail: " + e.getMessage());
            return ("code=101");
        } catch (IOException e) {
            Log.e(appTAG, "Cannot execute HTTP GET. Detail: " + e.getMessage());
            return ("code=101");
        } catch (Exception e) {
            Log.e(appTAG, "error: ", e);
            return ("code=101");
        }
        httpClient.getConnectionManager().shutdown();
        return sCode;
    }

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
                if (!line.trim().equals("")) {
                    sb.append(line);
                }
            }
            Log.d(appTAG, "reader line = " + sb.toString());
        } catch (IOException e) {
            Log.e(appTAG, "IOException :", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(appTAG, "IOException :", e);
            }
        }

        return sb.toString();
    }

    public static final float getAngle(float center_x, float center_y, float post_x, float post_y) {
        float tmpv_x = post_x - center_x;
        float tmpv_y = post_y - center_y;
        float d = (float) Math.sqrt(tmpv_x * tmpv_x + tmpv_y * tmpv_y);
        float cos = tmpv_x / d;
        float angle = (float) Math.toDegrees(Math.acos(cos));

        angle = (tmpv_y < 0) ? angle * -1 : angle;

        return angle;
    }
}
