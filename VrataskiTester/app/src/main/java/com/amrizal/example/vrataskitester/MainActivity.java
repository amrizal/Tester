package com.amrizal.example.vrataskitester;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();
    }

    private void initFirebase() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance();
        myRef.push().setValue(new LogEntry("Hello, The time is " + dateTimeInstance.format(Calendar.getInstance().getTime())));

        long MillisInHour = 60*60*1000;
        //Query query = myRef.orderByChild("timeStamp").startAt(System.currentTimeMillis()).endAt(System.currentTimeMillis() - MillisInHour);
        Query query = myRef.orderByChild("timeStamp").endAt(System.currentTimeMillis()).startAt(System.currentTimeMillis() - MillisInHour);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    LogEntry logEntry = postSnapshot.getValue(LogEntry.class);
                    Log.d(TAG, "Value is: " + logEntry.getDescription());
                    //Log.d(TAG, "Value is: " + postSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    LogEntry logEntry = postSnapshot.getValue(LogEntry.class);
                    Log.d(TAG, "Value is: " + logEntry.getDescription());
                    //Log.d(TAG, "Value is: " + postSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });*/
    }
}
