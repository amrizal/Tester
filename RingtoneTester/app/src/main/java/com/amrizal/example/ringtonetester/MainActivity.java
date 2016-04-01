package com.amrizal.example.ringtonetester;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private String ringtoneURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        ringtoneURI = sharedPreferences.getString(Constants.PREFERENCE_RINGTONE_URI, "");
        if(ringtoneURI.length() == 0){
            ringtoneURI = String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        }

        TextView ringtonePath = (TextView)findViewById(R.id.ringtone_uri);
        ringtonePath.setText(ringtoneURI);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.select_ringtone:
                break;
            case R.id.play_ringtone:
                startPlayingRingtone();
                findViewById(R.id.stop_ringtone).setEnabled(true);
                findViewById(R.id.play_ringtone).setEnabled(false);
                break;
            case R.id.stop_ringtone:
                stopPlayingRingtone();
                findViewById(R.id.play_ringtone).setEnabled(true);
                findViewById(R.id.stop_ringtone).setEnabled(false);
                break;
            default:
                break;
        }
    }

    void startPlayingRingtone(){
        Intent startIntent = new Intent(this, RingtonePlayingService.class);
        startIntent.putExtra(RingtonePlayingService.RINGTONE_URI, ringtoneURI);
        startService(startIntent);
    }

    void stopPlayingRingtone(){
        Intent stopIntent = new Intent(this, RingtonePlayingService.class);
        stopService(stopIntent);
    }
}
