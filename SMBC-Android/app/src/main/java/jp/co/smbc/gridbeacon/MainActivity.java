package jp.co.smbc.gridbeacon;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import jp.co.smbc.gridbeacon.zxing.CaptureActivity;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_MULTIPLE = 100;
    private FragmentTabHost mTabHost;
    SharedPreferences sharedPreferences;
    String cloudId;
    private String gridUrl;
    private String guid;
    private String hashSeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!checkPlayServices())
            return;

        if(!checkPermission())
            return;

        if(!checkDisplaySplash())
            return;

        if(!checkBeaconRegistration())
            return;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it doesn't, display a dialog that allows users to download the APK from the Google Play Store or enable it in the device's
     * system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface arg0) {
                        finish();
                    }
                });
                dialog.show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private boolean checkPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_MULTIPLE);
            return false;
        }
    }

    private boolean checkBeaconRegistration() {
        gridUrl = sharedPreferences.getString(BeaconConstants.GRID_URL, "");
        guid = sharedPreferences.getString(BeaconConstants.GUID, "");
        hashSeed = sharedPreferences.getString(BeaconConstants.HASH_SEED, "");

        if(gridUrl.isEmpty() || guid.isEmpty() || hashSeed.isEmpty()){
            showQrCodeActivity();
            return false;
        }

        return true;
    }

    private void showQrCodeActivity() {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);
    }

    private boolean checkDisplaySplash() {
        cloudId = sharedPreferences.getString(BeaconConstants.CLOUDID, "");
        boolean registerGcm = cloudId.isEmpty();

        //show splash screen if first start or register gcm
        if(BeaconApplication.getInstance().isFirstStart() || registerGcm){
            BeaconApplication.getInstance().setFirstStart(false);
            showSplashActivity();
            return false;
        }

        return true;
    }

    private void showSplashActivity() {
        Intent intent = new Intent(this, SplashActivity.class);
        //BeaconApplication.getInstance().setFirstStart(false);
        startActivity(intent);
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        setupActionBar(getString(R.string.app_name));

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        Bundle b = new Bundle();
        b.putString("key", "Simple");
        mTabHost.addTab(mTabHost.newTabSpec("simple").setIndicator("Simple"),
                ItemFragment.class, b);
        //
        b = new Bundle();
        System.out.print("hello git");
        b.putString("key", "Contacts");
        mTabHost.addTab(mTabHost.newTabSpec("contacts")
                .setIndicator("Contacts"), ItemFragment.class, b);
        b = new Bundle();
        b.putString("key", "Custom");
        mTabHost.addTab(mTabHost.newTabSpec("custom").setIndicator("Custom"),
                ItemFragment.class, b);
    }
}
