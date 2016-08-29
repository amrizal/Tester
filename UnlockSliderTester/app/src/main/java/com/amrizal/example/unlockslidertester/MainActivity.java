package com.amrizal.example.unlockslidertester;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((SlideButton) findViewById(R.id.unlockButton)).setSlideButtonListener(new SlideButton.SlideButtonListener() {
            @Override
            public void handleSlide() {
                unlockScreen();
            }
        });
    }

    private void unlockScreen() {
        Log.d(TAG, "Screen unlocked");
    }
}
