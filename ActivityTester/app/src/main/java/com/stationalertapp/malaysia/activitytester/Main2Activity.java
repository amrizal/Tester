package com.stationalertapp.malaysia.activitytester;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    public static final String VALUE = "valueText";
    private TextView valueText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        valueText = (TextView)findViewById(R.id.value);

        Button dismiss = (Button)findViewById(R.id.button_dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if(null == intent)
            return;

        String value = intent.getStringExtra(VALUE);
        valueText.setText(value);
    }
}
