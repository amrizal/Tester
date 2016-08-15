package jp.co.smbc.gridbeacon;

import android.Manifest;
import android.app.Activity;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import jp.co.smbc.gridbeacon.zxing.CaptureActivity;
import jp.co.smbc.gridbeacon.zxing.Intents;

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
    boolean showSplash = true;
    private View cover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showViewCover();

        if(!checkPlayServices())
            return;

        if(!checkPermission())
            return;

        if(!checkDisplaySplash())
            return;

        if(!checkBeaconRegistration())
            return;

        hideViewCover();
    }

    private void hideViewCover() {
        mTabHost.setVisibility(View.VISIBLE);
        getSupportActionBar().show();
        cover.setVisibility(View.GONE);
    }

    private void showViewCover() {
        mTabHost.setVisibility(View.GONE);
        getSupportActionBar().hide();
        cover.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_CANCELED){
            if(requestCode == SplashActivity.SPLASH_ACTIVITY_CODE ||
                    requestCode == IntegerRes.ACTIVITY_CODE_REG_QR){
                //exit the app if the user press the back button while these activity are shown
                finish();
            }
        }
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
        //Intent intent = new Intent(this, CaptureActivity.class);
        Intent intent = new Intent(Intents.Scan.ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(StringRes.ZXING_KEY_SCAN_MODE, StringRes.ZXING_MODE_QR_CODE);
        intent.putExtra(StringRes.ACTIVITY_KEY_REQUEST_CODE, IntegerRes.ACTIVITY_CODE_REG_QR);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        intent.putExtra(Intents.Scan.WIDTH, width * 5/7);
        intent.putExtra(Intents.Scan.HEIGHT, width * 5/7);

        startActivityForResult(intent, IntegerRes.ACTIVITY_CODE_REG_QR);
    }

    private boolean checkDisplaySplash() {
        cloudId = sharedPreferences.getString(BeaconConstants.CLOUDID, "");
        boolean registerGcm = cloudId.isEmpty();

        if(registerGcm || showSplash ){
            showSplash = false;
            showSplashActivity();
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showSplashActivity() {
        Intent intent = new Intent(this, SplashActivity.class);
        startActivityForResult(intent, SplashActivity.SPLASH_ACTIVITY_CODE);
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        setupActionBar(getString(R.string.app_name));

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        Bundle b = new Bundle();
        b.putString("key", "Authentication");
        mTabHost.addTab(mTabHost.newTabSpec("authentication").setIndicator(getString(R.string.tab_authentication)),
                ItemFragment.class, b);

        b = new Bundle();
        b.putString("key", "Configuration");
        mTabHost.addTab(mTabHost.newTabSpec("configuration").setIndicator(getString(R.string.tab_configuration)),
                ItemFragment.class, b);

        cover = findViewById(R.id.cover);
    }
}
