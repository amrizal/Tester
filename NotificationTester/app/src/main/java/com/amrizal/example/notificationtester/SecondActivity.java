package com.amrizal.example.notificationtester;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = SecondActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        findViewById(R.id.show_third).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ThirdActivity.class);
                startActivityForResult(intent, REQUEST.THIRD_ACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult-" + requestCode + ", " + resultCode);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent intent = new Intent();
        intent.putExtra(EXTRA.IS_BACK_BUTTON, true);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
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

    private class REQUEST {
        public static final int THIRD_ACTIVITY = 1000;
    }
}
