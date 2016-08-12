package jp.co.smbc.gridbeacon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;

public class MainActivity extends BaseActivity {

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

        checkDisplaySplash();
        checkBeaconRegistration();
    }

    private void checkBeaconRegistration() {
        gridUrl = sharedPreferences.getString(BeaconConstants.GRID_URL, "");
        guid = sharedPreferences.getString(BeaconConstants.GUID, "");
        hashSeed = sharedPreferences.getString(BeaconConstants.HASH_SEED, "");

        if(gridUrl.isEmpty() || guid.isEmpty() || hashSeed.isEmpty()){
            showQrCodeActivity();
        }
    }

    private void showQrCodeActivity() {
        
    }

    private void checkDisplaySplash() {
        cloudId = sharedPreferences.getString(BeaconConstants.CLOUDID, "");
        boolean registerGcm = cloudId.isEmpty();

        //show splash screen if first start or register gcm
        if(BeaconApplication.getInstance().isFirstStart() || registerGcm){
            showSplashActivity();
        }
    }

    private void showSplashActivity() {
        Intent intent = new Intent(this, SplashActivity.class);
        BeaconApplication.getInstance().setFirstStart(false);
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
