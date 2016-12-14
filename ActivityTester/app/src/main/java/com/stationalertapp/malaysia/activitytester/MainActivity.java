package com.stationalertapp.malaysia.activitytester;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String DESTINATION_PHONE_NUMBER = "+60193562080";
    private static final String TAG = MainActivity.class.getSimpleName();
    EditText editText;

    List<TestClass> testClassList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.edit_text);

        setupActionBar(getString(R.string.app_name));

        /* This is the important part */
        startService(new Intent(MyService.class.getName()));
    }

    private void test() {
        Long tick = System.currentTimeMillis();
        test1();
        Log.d(TAG, "test1 took " + (System.currentTimeMillis() - tick) + "ms");

        tick = System.currentTimeMillis();
        test2();
        Log.d(TAG, "test2 took " + (System.currentTimeMillis() - tick) + "ms");
    }

    private void test2() {
        List<TestClass> list = new ArrayList<>();
        for(int i=0; i<10000; i++){
            list.add(new TestClass(String.valueOf(i)));
        }
    }

    private void test1() {
        for(int i=0; i<10000; i++){
            testClassList.add(new TestClass((String.valueOf(i))));
        }
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
                break;
            case R.id.check_screen:
                checkScreen();
                break;
            default:
                break;
        }
    }

    private void checkScreen() {
        Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra(MyIntentService.DELAY, 30000L);//wait for 30s
        startService(intent);
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    private class TestClass {

        private final String s;

        public TestClass(String s) {
            this.s = s;
        }
    }

    private class RESULT {
        public static final int CHECK_SCREEN = 1000;
    }
}
