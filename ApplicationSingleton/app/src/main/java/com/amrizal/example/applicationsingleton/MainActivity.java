package com.amrizal.example.applicationsingleton;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.SimpleTimeZone;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

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

        String queryResult = "[{\"name\":\"max\",\"add\":\"\",\"lat\":3.161883,\"lng\":101.720512},{\"name\":\"ATM 1\",\"add\":\"No. 1 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.160513,\"lng\":101.712367},{\"name\":\"ATM 2\",\"add\":\"No. 2 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.148853,\"lng\":101.70996},{\"name\":\"ATM 3\",\"add\":\"No. 3 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.158038,\"lng\":101.712605},{\"name\":\"ATM 4\",\"add\":\"No. 4 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.148878,\"lng\":101.705809},{\"name\":\"ATM 5\",\"add\":\"No. 5 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.150649,\"lng\":101.71275},{\"name\":\"ATM 6\",\"add\":\"No. 6 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.159244,\"lng\":101.719742},{\"name\":\"ATM 7\",\"add\":\"No. 7 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.152817,\"lng\":101.702775},{\"name\":\"ATM 8\",\"add\":\"No. 8 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.157779,\"lng\":101.715228},{\"name\":\"ATM 9\",\"add\":\"No. 9 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.154314,\"lng\":101.719366},{\"name\":\"ATM 10\",\"add\":\"No. 10 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.145767,\"lng\":101.708119},{\"name\":\"ATM 11\",\"add\":\"No. 11 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.149007,\"lng\":101.702701},{\"name\":\"ATM 12\",\"add\":\"No. 12 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.145357,\"lng\":101.717594},{\"name\":\"ATM 13\",\"add\":\"No. 13 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.152031,\"lng\":101.715908},{\"name\":\"ATM 14\",\"add\":\"No. 14 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.154123,\"lng\":101.719181},{\"name\":\"ATM 15\",\"add\":\"No. 15 ロード SS2/75, Petaling Jaya, 47100 Selangor\",\"lat\":3.158937,\"lng\":101.703091},{\"name\":\"min\",\"add\":\"\",\"lat\":3.143897,\"lng\":101.702498}]";
        Gson gson = new Gson();
        Type type = new TypeToken<List<AtmLocation>>() {}.getType();
        List<AtmLocation> locList = gson.fromJson(queryResult, type);
        for (AtmLocation atmLocation:locList){
            Log.d(TAG, atmLocation.getAtmName() + "," + atmLocation.getAddress() + "," + atmLocation.getLatitude() + "," + atmLocation.getLongitude());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem toggleItem = menu.findItem(R.id.myswitch);
        View toggleView =  MenuItemCompat.getActionView(toggleItem);
        SwitchCompat toggle = (SwitchCompat) toggleView.findViewById(R.id.switchForActionBar);
        toggle.setOnCheckedChangeListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.something:
                Snackbar.make(findViewById(R.id.myCoordinatorLayout), R.string.snack_time,
                        Snackbar.LENGTH_SHORT)
                        .setAction(R.string.undo_string, new MyUndoListener())
                        .show();
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "Switch state to " + isChecked);

        if(isChecked)
            openProgDialog();
        else
            closeProgDialog();
    }

    private class MyUndoListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Undone!");
        }
    }
}
