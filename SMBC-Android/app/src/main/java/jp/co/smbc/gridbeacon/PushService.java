package jp.co.smbc.gridbeacon;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLException;

/**
 * <code>PushService</code> is an asynchronous thread that perform background task to get/update data in GRID server by using REST method.
 */
public class PushService extends AsyncTask<Object, Void, PushResult> {

    /**
     * Tag for logging
     */
    private final String TAG = PushService.class.getSimpleName();

    /**
     * Application context
     */
    private Context context;
    private PushResult pushResult = null;
    private HttpURLConnection conn = null;
    /**
     * Push result listener
     */
    private OnPushResult pushListener = null;

    /**
     * Boolean flag to check whether to show transaction detail if any, otherwise don't
     */
    private boolean bShowDetail = false;

    /**
     * Boolean flag to check whether to show error if any, otherwise don't
     */
    private boolean bShowError = false;

    public PushService(Context inContext) {
        context = inContext;

        // Work around pre-Froyo bugs in HTTP connection reuse.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * Attach pushresult listener
     *
     * @param inListener   OnPushResult listener
     * @param inShowDetail True to show transaction detail if any, otherwise don't
     * @param inShowError  True to show error if any, otherwise don't
     */
    public void setListener(OnPushResult inListener, boolean inShowDetail, boolean inShowError) {

        pushListener = inListener;
        bShowDetail = inShowDetail;
        bShowError = inShowError;
    }

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected PushResult doInBackground(Object... params) {
        int iResult = -1;
        PushResult pResult = null;

        try {
            if (!isCancelled()) {
                int iMode = (Integer) params[0];
                String sServerUrl = (String) params[1];

                ContentValues inputParams = (ContentValues) params[2];
                //Log.d(TAG, "PushTask(), inputParams size = " + (inputParams != null ? inputParams.size() : 0));

                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (NetworkUtility.isConnectedToInternet(cm)) {
                    //Log.d(TAG, "PushService(), requestURL =" + sServerUrl + ", iMode = " + iMode);
                    if (sServerUrl != null && !sServerUrl.trim().isEmpty()) {

                        switch (iMode) {
                            case BeaconConstants.GET:
                                conn = performDelGet(sServerUrl, "GET");
                                break;
                            case BeaconConstants.POST:
                                conn = performPutPost(sServerUrl, "POST", inputParams);
                                break;
                            case BeaconConstants.PUT:
                                conn = performPutPost(sServerUrl, "PUT", inputParams);
                                break;
                            case BeaconConstants.DELETE:
                                conn = performDelGet(sServerUrl, "DELETE");
                                break;
                            default:
                                break;
                        }

                        // read the response
                        if (conn != null) {

                            iResult = conn.getResponseCode();
                            pResult = handleRespond(conn, iResult);

                        } else {
                            Log.d(TAG, "PushTask(), Invalid HTTP response!");
                            iResult = StatusCode.FAILED_CONNECT_GRID;
                        }

                    } else {
                        Log.d(TAG, "PushTask,  sSeverUrl is empty");
                        iResult = StatusCode.INVALID_SETTING;
                    }
                } else {
                    Log.d(TAG, "PushTask, No internet connection");
                    iResult = StatusCode.FAILED_CONNECT_GRID;
                }
            } else {
                Log.d(TAG, "PushTask, Task has been canceled!");
            }

        } catch (SocketTimeoutException e) {
            Log.e(TAG, "PushTask(), SocketTimeoutException, 912 Detail: ", e);
            iResult = StatusCode.CONNECTION_TIMEOUT;
        } catch (NullPointerException e) {
            Log.e(TAG, "PushTask(), NullPointerException, 915 Detail: ", e);
            iResult = StatusCode.ERR_HTTP_IO;
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "PushTask(), UnsupportedEncodingException, 915 Detail: ", e);
            iResult = StatusCode.ERR_HTTP_IO;
        } catch (ProtocolException e) {
            Log.e(TAG, "PushTask(), ProtocolException, 914 Detail: ", e);
            iResult = StatusCode.ERR_HTTP_PROTOCOL;
        } catch (SSLException e) {
            Log.e(TAG, "PushTask(), SSLException, 913 Detail: ", e);
            iResult = StatusCode.ERR_HTTP_SSL;
        } catch (UnknownHostException e) {
            Log.e(TAG, "PushTask(), UnknownHostException, 916 Detail: ", e);
            iResult = StatusCode.ERR_HTTP_HOST;
        } catch (MalformedURLException e) {
            Log.e(TAG, "PushTask(), MalformedURLException, 916 Detail: ", e);
            iResult = StatusCode.ERR_HTTP_HOST;
        } catch (RuntimeException e) {
            Log.e(TAG, "PushTask(), RuntimeException, 917 Detail: ", e);
            iResult = StatusCode.ERR_HTTP_RUNTIME;
        } catch (IOException e) {
            Log.e(TAG, "PushTask(), IOException, try get error code again, is normal for error code 401, first Detail: ", e);
            try {
                if (conn != null) {
                    // Will return 401, because now connection has the correct internal state.
                    iResult = conn.getResponseCode();
                    pResult = handleRespond(conn, iResult);
                } else {
                    iResult = StatusCode.ERR_HTTP_IO;
                }
            } catch (IOException ioe) {
                Log.e(TAG, "PushTask(), IOException second time, Detail: ", ioe);
                if (ioe.getLocalizedMessage() != null && (ioe.getLocalizedMessage().equalsIgnoreCase("Received authentication challenge is null") || ioe.getLocalizedMessage().equalsIgnoreCase("No authentication challenges found"))) {
                    iResult = 401;
                    pResult = handleRespond(conn, iResult);
                } else {
                    iResult = StatusCode.ERR_HTTP_IO;
                }
            } catch (Exception ioe) {
                Log.e(TAG, "PushTask(), second time exception thrown. 912 Detail:", ioe);
                iResult = StatusCode.CONNECTION_TIMEOUT;
            } finally {
                if (conn != null) {

                    conn.disconnect();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "processPush(), 912 exception thrown. ", e);
            iResult = StatusCode.CONNECTION_TIMEOUT;

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        if (pResult == null) {
            pResult = new PushResult(iResult, null);
            Log.d(TAG, "PushTask, No response frm server. iResult = " + iResult);
        }

        return pResult;
    }


    private PushResult handleRespond(HttpURLConnection inConn, int inResult) {
        try {

            String sJson = "";
            if (inConn == null) {
                return null;
            }

            //Log.d(TAG, "handleRespond(), response code = " + inResult);

            //any HTTP response code of 400 or above conn.getInputStream() will throw a java.io.FileNotFoundException have to use conn.getErrorStream() to get body, so need to add checking here
            if (inResult >= 400) {

                if (inConn.getErrorStream() == null) {
                    sJson = StringHelper.convertStreamToString(inConn.getInputStream());
                } else {
                    sJson = StringHelper.convertStreamToString(inConn.getErrorStream());
                }
            } else {
                sJson = StringHelper.convertStreamToString(inConn.getInputStream());
            }

            //Log.d(TAG, "handleRespond(), executePOST(), response code = " + inResult);

            return new PushResult(inResult, sJson);

        } catch (Exception e) {
            Log.e(TAG, "handleRespond(), Exception = ", e);
            return null;
        }

    }

    @Override
    protected void onCancelled() {
        Log.d(TAG, "onCancelled(), task is cancelled");

    }

    @Override
    protected void onPostExecute(PushResult result) {
        //Log.d(TAG, "onPostExecute(), response code = " + result.getCode());
        if (isCancelled()) {
            Log.d(TAG, "onPostExecute(), task canceled! No return");
            return;
        }
        pushResult = result;
        if (pushListener != null) {
            pushListener.onPushResult(pushResult, bShowDetail, bShowError);
        }
    }

    /**
     * Force the connection to shutdown
     */

    public void forceStop() {
        if (conn != null) {
            Log.d(TAG, "forceStop(), shutdown connection");
            conn.disconnect();
        }
    }

    /**
     * Called when push service has return result
     */
    public interface OnPushResult {
        /**
         * Listener when server return result
         *
         * @param pResult     PushResult Object
         * @param bShowDetail True to show transaction detail if any, otherwise don't
         * @param bShowError  True to show error if any, otherwise don't
         */
        void onPushResult(PushResult pResult, boolean bShowDetail, boolean bShowError);

    }


    private HttpURLConnection getHttpConnection(String requestURL, String type) throws Exception {
        URL url;
        HttpURLConnection conn = null;
        try {
            Log.d(TAG, "getHttpConnection(), type = " + type);

            url = new URL(requestURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setRequestMethod(type);
            conn.setDoInput(true);
            conn.addRequestProperty("charset", "utf-8");
            conn.setRequestProperty("User-Agent", "Android");

            if (type.equals("POST") || type.equals("PUT")) {
                conn.setDoOutput(true);
                //conn.addRequestProperty("Transfer-Encoding", "chunked");
            }

        } catch (SocketTimeoutException e) {
            Log.e(TAG, "getHttpConnection(), SocketTimeoutException thrown. ", e);
        } catch (ProtocolException e) {
            Log.e(TAG, "getHttpConnection(), ProtocolException thrown. ", e);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "getHttpConnection(), IOException thrown. ", e);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getHttpConnection(), Exception thrown. ", e);
            throw e;
        }
        return conn;

    }

    private HttpURLConnection performPutPost(String requestURL, String type, ContentValues postDataParams) throws Exception {
        //Log.d(TAG, "performPutPost(), requestURL " + requestURL + ", type = " + type);
        HttpURLConnection conn = null;

        try {
            conn = getHttpConnection(requestURL, type);

            if (conn != null) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                //conn.connect();
            }

        } catch (SocketTimeoutException e) {
            Log.e(TAG, "performPost(), SocketTimeoutException thrown. ", e);
            throw e;
        } catch (ProtocolException e) {
            Log.e(TAG, "performPost(), ProtocolException ", e);
            throw e;
        } catch (MalformedURLException e) {
            Log.e(TAG, "performPost(), MalformedURLException ", e);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "performPost(), IOException ", e);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "performPost(), Exception ", e);
            throw e;
        }

        return conn;
    }

    private HttpURLConnection performDelGet(String requestURL, String type) throws Exception {
        //Log.d(TAG, "performDelGet(), requestURL " + requestURL + ", type = " + type);

        HttpURLConnection conn;
        try {
            conn = getHttpConnection(requestURL, type);

        } catch (SocketTimeoutException e) {
            Log.e(TAG, "performDelGet(), SocketTimeoutException thrown. ", e);
            throw e;
        } catch (ProtocolException e) {
            Log.e(TAG, "performDelGet(), ProtocolException ", e);
            throw e;
        } catch (MalformedURLException e) {
            Log.e(TAG, "performDelGet(), MalformedURLException ", e);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "performDelGet(), IOException ", e);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "performDelGet(), Exception ", e);
            throw e;
        }

        return conn;
    }

    private String getPostDataString(ContentValues params) throws UnsupportedEncodingException {

        if (params == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Set<Map.Entry<String, Object>> set = params.valueSet();
        Iterator itr = set.iterator();

        //Log.d(TAG, "getPostDataString(), ContentValue Length = " + set.size());

        while (itr.hasNext()) {
            Map.Entry map = (Map.Entry) itr.next();
            String key = map.getKey().toString();
            String value = map.getValue().toString();

            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value, "UTF-8"));

            //Log.d(TAG, "getPostDataString(), Key = " + key + ", values = " + (String) (value == null ? null : value.toString()));
        }

        //Log.d(TAG, "getPostDataString(), result = " + result.toString());
        return result.toString();
    }
}

