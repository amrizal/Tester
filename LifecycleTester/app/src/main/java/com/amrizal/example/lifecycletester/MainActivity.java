package com.amrizal.example.lifecycletester;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TIME_STATE = "time_state";
    private long lastStateTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        if(savedInstanceState!= null){
            lastStateTime = savedInstanceState.getLong("time_state", 0);
        }

        Log.d(TAG, "lastStateTime=" + lastStateTime);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_second_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SecondActivity.class);
                startActivityForResult(intent, REQUEST.SECOND_ACTIVITY);
            }
        });

        findViewById(R.id.button_third_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ThirdActivity.class);
                startActivityForResult(intent, REQUEST.THIRD_ACTIVITY);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQUEST.SECOND_ACTIVITY:
                Log.d(TAG, "Second activity returned");
                break;
            case REQUEST.THIRD_ACTIVITY:
                Log.d(TAG, "Third activity returned");
                break;
            default:
                break;
        }
    }

    private class REQUEST {
        public static final int SECOND_ACTIVITY = 1000;
        public static final int THIRD_ACTIVITY = 1001;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        //Save your data to be restored here
        outState.putLong(TIME_STATE, System.currentTimeMillis());
        super.onSaveInstanceState(outState);
    }
}
