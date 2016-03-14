package com.amrizal.example.applicationsingleton;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progDialog;
    private final Handler mHandler = new Handler();
    boolean showProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage(getResources().getString(R.string.processing));
        progDialog.setCancelable(false);

        MyApplication mApplication = (MyApplication)getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myswitch:
                showProgress = !showProgress;
                item.setChecked(showProgress);

                if(showProgress)
                    openProgDialog();
                else
                    closeProgDialog();

                break;
            default:
                break;
        }
        return true;
    }



    /**
     * Opens progress dialog box
     */
    private void openProgDialog() {
        Runnable uiThread = new Runnable() {
            public void run() {
                // Looper.prepare();
                if (progDialog != null) {
                    Log.d(TAG, "Open progress dialog");
                    progDialog.show();
                }
                // Looper.loop();
            }
        };
        mHandler.post(uiThread);
    }

    /**
     * Close progress dialog box
     *
     * @return UI thread
     */
    private void closeProgDialog() {
        Runnable uiThread = new Runnable() {
            public void run() {
                if (progDialog != null) {
                    if (progDialog.isShowing()) {
                        Log.d(TAG, "Close progress dialog");
                        progDialog.dismiss();
                    }
                }

            }
        };
        mHandler.post(uiThread);
    }
}
