package com.amrizal.example.serviceupdateuitester;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AlertActivity extends AppCompatActivity {

    PowerManager.WakeLock wakeLock;
    Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();

        Button dismiss = (Button)findViewById(R.id.button_dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != ringtone && ringtone.isPlaying()){
                   ringtone.stop();
                }

                wakeLock.release();
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

            //loop through to select which alarm to play
            Integer[] alarmTypes = {RingtoneManager.TYPE_ALARM, RingtoneManager.TYPE_RINGTONE, RingtoneManager.TYPE_NOTIFICATION};
            Uri notification = null;
            for(Integer alarmType:alarmTypes){
                notification = RingtoneManager.getDefaultUri(alarmType);
                if(null != notification)
                    break;
            }

            //interrupt any playing music
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ringtone.play();

            //doesn't interrupt any playing music
            //MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
            //mp.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
