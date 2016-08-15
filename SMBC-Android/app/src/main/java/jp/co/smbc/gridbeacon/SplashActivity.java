package jp.co.smbc.gridbeacon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int MSG_SPLASH_TIMEOUT = 1001;
    private static final long SPLASH_TIMEOUT = 1000;//show for 1 second
    public static final int SPLASH_ACTIVITY_CODE = 1002;
    // Set Duration of the Splash Screen=
    SharedPreferences sharedPreferences;
    private String cloudId;
    BroadcastReceiver receiver;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove the Title Bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        TextView textView = (TextView) findViewById(R.id.version);
        textView.setText(BuildConfig.VERSION_NAME);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        initReceiver();
        initHandler();
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_SPLASH_TIMEOUT:
                        startMainActivity();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void initReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(BeaconService.ACTION_REGISTER_GCM.equals(intent.getAction())){
                    if(intent.getIntExtra(BeaconConstants.EVENT_RESULT, BeaconConstants.EVENT_FAILED) == BeaconConstants.EVENT_SUCCESS){
                        cloudId = intent.getStringExtra(BeaconConstants.CLOUDID);
                        sharedPreferences.edit().putString(BeaconConstants.CLOUDID, cloudId).apply();
                        startMainActivity();
                    }else{
                        String errorMessage = intent.getStringExtra(BeaconConstants.FAILURE_MESSAGE);
                        Log.d(TAG, errorMessage);
                    }
                }
            }
        };
    }

    private void startMainActivity() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        // Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
    }

    private void initGcm() {
        cloudId = sharedPreferences.getString(BeaconConstants.CLOUDID, "");

        if(cloudId.isEmpty()){
            BeaconService.registerGcm(this, BeaconUtility.SENDER_ID);
            return;
        }else{
            //show for about 1 second
            Message msg = handler.obtainMessage(MSG_SPLASH_TIMEOUT);
            handler.sendMessageDelayed(msg, SPLASH_TIMEOUT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver();
        initGcm();
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BeaconService.ACTION_REGISTER_GCM);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }
}
