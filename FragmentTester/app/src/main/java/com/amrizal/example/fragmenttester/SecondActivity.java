package com.amrizal.example.fragmenttester;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.first_fragment:
                intent.putExtra(EXTRA.FRAGMENT_TO_SHOW, 1);
                break;
            case R.id.second_fragment:
                intent.putExtra(EXTRA.FRAGMENT_TO_SHOW, 2);
                break;
            case R.id.third_fragment:
                intent.putExtra(EXTRA.FRAGMENT_TO_SHOW, 3);
                break;
            default:
                return;
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
