package com.amrizal.example.intentservicetester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String param1 = intent.getStringExtra(MyConstants.PARAM_1);
                String param2 = intent.getStringExtra(MyConstants.PARAM_2);

                String output = "Received: Action [" + intent.getAction() + "] Param 1 [" + param1 + "] Param 2 [" + param2 + "]";
                Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
            }
        };

        findViewById(R.id.foo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String param1 = String.valueOf(((EditText)findViewById(R.id.param_1)).getText());
                String param2 = String.valueOf(((EditText)findViewById(R.id.param_2)).getText());

                MyIntentService.startActionFoo(view.getContext(), param1, param2);
            }
        });

        findViewById(R.id.baz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String param1 = String.valueOf(((EditText)findViewById(R.id.param_1)).getText());
                String param2 = String.valueOf(((EditText)findViewById(R.id.param_2)).getText());

                MyIntentService.startActionBaz(view.getContext(), param1, param2);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter fooFilter = new IntentFilter(MyConstants.FOO_EVENT);
        IntentFilter bazFilter = new IntentFilter(MyConstants.BAZ_EVENT);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, fooFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, bazFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
