package com.amrizal.example.serviceupdateuitester;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

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
        if(null != intent){
            String count = "The value is " + intent.getStringExtra(MyService.MYSERVICE_MESSAGE);
            TextView text = (TextView) findViewById(R.id.alert_message);
            text.setText(count);
        }
    }
}
