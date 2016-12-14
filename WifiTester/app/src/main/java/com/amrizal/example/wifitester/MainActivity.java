package com.amrizal.example.wifitester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private WifiManager wifiManager;
    private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                Date date = new Date(System.currentTimeMillis());
                //SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd hh:mm aa");
                lastUpdated.setText("Last updated: " + date.toString());

                List<ScanResult> mScanResults = wifiManager.getScanResults();
                for(ScanResult result:mScanResults){
                    Log.d(TAG, "SSID: " + result.SSID + ", BSID: " + result.BSSID + ", Capabilities: " + result.capabilities);
                }

                adapter.setData(mScanResults);
                adapter.notifyDataSetChanged();
            }
        }
    };
    private ScanResultAdapter adapter;
    private TextView lastUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastUpdated = (TextView) findViewById(R.id.last_updated);

        adapter = new ScanResultAdapter(this);
        ListView listView = (ListView) findViewById(R.id.list_item);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(wifiManager == null){
            wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiManager.startScan();
        }

        registerReceiver(wifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiScanReceiver);
    }
}
