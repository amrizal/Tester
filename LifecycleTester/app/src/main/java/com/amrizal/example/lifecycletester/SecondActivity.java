package com.amrizal.example.lifecycletester;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SecondActivity extends AppCompatActivity {

    private String TAG = SecondActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_second);
        findViewById(R.id.button_third_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ThirdActivity.class);
                startActivityForResult(intent, REQUEST.THIRD_ACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST.THIRD_ACTIVITY:
                Log.d(TAG, "Third activity returned");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

    }

    private class REQUEST {
        public static final int THIRD_ACTIVITY = 1003;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //not being called on rotate because of Activity's manifest node
        //  android:configChanges="keyboardHidden|orientation|screenSize"
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }
}
