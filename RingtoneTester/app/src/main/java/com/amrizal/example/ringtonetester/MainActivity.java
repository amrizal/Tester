package com.amrizal.example.ringtonetester;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int RINGTONE_PICKER = 123;
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
                chooseRingtone();
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

    private void chooseRingtone() {
        Intent intent =  new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getResources().getString(R.string.app_name));
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 1l);
        //don't show silent
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        startActivityForResult(intent, RINGTONE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case RINGTONE_PICKER:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if(null != uri){
                        ringtoneURI = String.valueOf(uri);
                        sharedPreferences.edit().putString(Constants.PREFERENCE_RINGTONE_URI, ringtoneURI).commit();
                        TextView ringtonePath = (TextView)findViewById(R.id.ringtone_uri);
                        ringtonePath.setText(ringtoneURI);
                    }
                }
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
