package com.amrizal.example.notificationtester;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final int NOTI_ID = 0;
    private static final String NOTIFICATION_TEXT = "This is a notification";
    private static final String EXTRA_DECISION = "extra_decision";
    private static final String SECOND = "Second";
    private static final String THIRD = "Third";
    private static final String DISMISS = "Dismiss";
    private static final int TRX_NOTI_ID = 0;
    private static final int TRX_NOTI_ID_SECOND = 1;
    private static final int TRX_NOTI_ID_THIRD = 2;
    private static final int TRX_NOTI_ID_DISMISS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        processIntent();
    }

    private void processIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }

        String decision = intent.getStringExtra(EXTRA_DECISION);
        if(decision == null){
            return;
        }

        intent.removeExtra(EXTRA_DECISION);
        if(decision.equals(SECOND)){
            Intent startIntent = new Intent(this, SecondActivity.class);
            startActivityForResult(startIntent, 0);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_main);
    }

    public void showNotification(View view){
        showDefaultNotification();
    }

    private void showDefaultNotification() {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder =  new NotificationCompat.Builder(context);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            Intent pIntent = new Intent(context, DismissActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, pIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(NOTIFICATION_TEXT);
        }else{
            Intent pintent = new Intent(context, MainActivity.class);

            Intent secondIntent = new Intent(pintent);
            secondIntent.putExtra(MainActivity.EXTRA_DECISION, SECOND);

            Intent thirdIntent = new Intent(context, ThirdActivity.class);

            Intent dismissIntent = new Intent(context, DismissActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, TRX_NOTI_ID, pintent, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent secondPIntent = PendingIntent.getActivity(context, TRX_NOTI_ID_SECOND, secondIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent thirdPIntent = PendingIntent.getActivity(context, TRX_NOTI_ID_THIRD, thirdIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent dismissPIntent = PendingIntent.getActivity(context, TRX_NOTI_ID_DISMISS, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(NOTIFICATION_TEXT)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(NOTIFICATION_TEXT))
                    .addAction(R.mipmap.ic_launcher, SECOND, secondPIntent)
                    .addAction(R.mipmap.ic_launcher, THIRD, thirdPIntent)
                    .addAction(R.mipmap.ic_launcher, DISMISS, dismissPIntent);
        }

        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        // Trigger the notification
        notificationManager.notify(NOTI_ID, notification);
    }

    public void showBackgroundNotification(View view){

    }

    public void showDelayedNotification(View view){

    }

    public void showRestoredNotification(View view){

    }
}
