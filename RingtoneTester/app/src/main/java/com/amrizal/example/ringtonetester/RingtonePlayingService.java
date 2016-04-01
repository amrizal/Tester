package com.amrizal.example.ringtonetester;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by amrizal.zainuddin on 1/4/2016.
 */
public class RingtonePlayingService extends Service {
    public static final String RINGTONE_URI = "ringtone-uri";
    Ringtone ringtone;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri ringtoneURI = Uri.parse(intent.getStringExtra(RINGTONE_URI));
        this.ringtone = RingtoneManager.getRingtone(this, ringtoneURI);
        ringtone.play();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        ringtone.stop();
    }
}
