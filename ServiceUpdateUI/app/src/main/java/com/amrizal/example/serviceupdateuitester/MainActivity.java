package com.amrizal.example.serviceupdateuitester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private BroadcastReceiver receiver;
    private TextView counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counter = (TextView) findViewById(R.id.counter);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(MyService.MYSERVICE_MESSAGE);
                counter.setText(message);
            }
        };

        Button buttonStart = (Button) findViewById(R.id.button_start);
        buttonStart.setOnClickListener(this);

        Button buttonStop = (Button) findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(MyService.MYSERVICE_RESULT)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem toggleItem = menu.findItem(R.id.myswitch);
        View toggleView =  MenuItemCompat.getActionView(toggleItem);
        ToggleButton toggleButton = (ToggleButton)toggleView.findViewById(R.id.switchForActionBar);
        toggleButton.setOnCheckedChangeListener(this);
        return true;
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_start:
                //start the service from here //MyService is your service class name
                startService(new Intent(this, MyService.class));
                break;
            case R.id.button_stop:
                //Stop the running service from here//MyService is your service class name
                //Service will only stop if it is already running.
                stopService(new Intent(this, MyService.class));
                break;
            default:
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.switchForActionBar) {
            if(isChecked)
                startService(new Intent(this, MyService.class));
            else
                stopService(new Intent(this, MyService.class));
        }
    }
}
