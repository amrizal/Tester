package com.amrizal.example.firebasetester.volley;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by amrizal.zainuddin on 18/10/2016.
 */
public class VolleyHelper {

    private static final String TAG = VolleyHelper.class.getSimpleName();
    private static VolleyHelper instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    private VolleyHelper(Context context) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        if (requestQueue != null && imageLoader == null) {
            imageLoader = new ImageLoader(requestQueue,
                    new LruBitmapCache());
        }
    }

    public static synchronized VolleyHelper getInstance(Context context){
        if (null == instance)
            instance = new VolleyHelper(context);
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
