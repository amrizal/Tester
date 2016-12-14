package com.amrizal.example.locationtester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    List<String> logList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
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

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST.LOG_LINE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }
}
