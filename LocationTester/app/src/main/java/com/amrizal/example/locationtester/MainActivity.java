package com.amrizal.example.locationtester;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST=2905;
    private static final String[] LOCATION_PERMISSION={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final String TAG = MainActivity.class.getSimpleName();

    ListView listView;
    List<String> logList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private BroadcastReceiver receiver;
    private LocationManager locationManager;
    private boolean locationNotEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceivedBroadcast(context, intent);
            }
        };

        initView();
    }

    private void onReceivedBroadcast(Context context, Intent intent) {
        switch (intent.getAction()){
            case BROADCAST.LOG_LINE:
                String logLine = intent.getStringExtra(EXTRA.LOG_LINE);
                addLog(logLine);
                break;
            default:
                break;
        }
    }

    private void initView() {
        findViewById(R.id.start_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLog("Starting service..");
                startService(new Intent(getBaseContext(), MyLocationService.class));
            }
        });

        findViewById(R.id.stop_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLog("Stopping service..");
                stopService(new Intent(getBaseContext(), MyLocationService.class));
            }
        });

        findViewById(R.id.clear_logs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new ArrayAdapter<String>(this, R.layout.log_item, logList);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    private void addLog(String logLine) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.SSS");
        String dateString=dateFormat.format(new Date());

        logList.add(0, dateString + " >> " + logLine);
        while(logList.size() > 100){
            logList.remove(logList.size()-1);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!servicesAvailable()) {
            finish();
        }

        if(!verifyLocationEnabled()){
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST.LOG_LINE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

        Intent intent = getIntent();
        if(intent != null && intent.getBooleanExtra(EXTRA.STOP_SERVICE, false)){
            addLog("Stopping service..");
            stopService(new Intent(getBaseContext(), MyLocationService.class));
            return;
        }
    }

    private boolean verifyLocationEnabled() {
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            locationNotEnabled = true;
            buildAlertMessageNoGps();
            return false;
        }

        if(!canAccessLocation()) {
            locationNotEnabled = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(LOCATION_PERMISSION, LOCATION_REQUEST);
            }
            return false;
        }

        return true;
    }

    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(permission));
        }

        return true;
    }

    private void buildAlertMessageNoGps() {
        Log.d(TAG, "buildAlertMessageNoGps");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.gps_disable_query))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private boolean servicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }
}
