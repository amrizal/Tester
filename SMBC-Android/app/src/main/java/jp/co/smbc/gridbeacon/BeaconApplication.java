package jp.co.smbc.gridbeacon;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by amrizal.zainuddin on 12/8/2016.
 */
public class BeaconApplication extends Application {
    private static BeaconApplication beaconApplication;

    boolean firstStart = true;
    public boolean isFirstStart() {
        return firstStart;
    }

    public void setFirstStart(boolean firstStart) {
        this.firstStart = firstStart;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        beaconApplication = this;
    }

    public static synchronized BeaconApplication getInstance(){
        return beaconApplication;
    }
}
