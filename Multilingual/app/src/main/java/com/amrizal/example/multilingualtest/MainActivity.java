package com.amrizal.example.multilingualtest;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String MALAYSIA_MALAY = "ms-rMY";
    private Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViewById(R.id.english).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en", "MY");
            }
        });

        findViewById(R.id.malay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ms", "");
            }
        });

        findViewById(R.id.indonesia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("id", "");
            }
        });

        findViewById(R.id.japan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ja", "");
            }
        });
    }

    public void setLocale(String lang, String region) {
        if(region == null || region.isEmpty()){
            myLocale = new Locale(lang);
        }else{
            myLocale = new Locale(lang, region);
        }

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }
}
