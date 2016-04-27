package com.stationalertapp.malaysia.activitytester;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String DESTINATION_PHONE_NUMBER = "+60193562080";
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.edit_text);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.show_activity2:
                showActivity2();
                break;
            case R.id.share_text:
                shareText();
                break;
            case R.id.sms_text:
                smsText();
                break;
            case R.id.whatsapp_text:
                whatsappText();
            default:
                break;
        }
    }

    private void whatsappText() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, editText.getText());
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }

    private void smsText() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(DESTINATION_PHONE_NUMBER, null, String.valueOf(editText.getText()), null, null);
    }

    private void shareText() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, editText.getText());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void showActivity2() {
        final Intent intent = new Intent(this, Main2Activity.class);
        String value = String.valueOf(editText.getText());
        intent.putExtra(Main2Activity.VALUE, value);
        startActivity(intent);
    }
}
