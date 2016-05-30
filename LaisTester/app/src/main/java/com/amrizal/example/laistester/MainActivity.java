package com.amrizal.example.laistester;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
    private TextView mStatusTextView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private Query query;
    private int googlePlayVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initFirebase();
    }

    private void initView() {
        mStatusTextView = (TextView) findViewById(R.id.status_text);
        findViewById(R.id.add_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");

                DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance();
                myRef.push().setValue(new LogEntry(user.getUid(), "Hello, The time is " + dateTimeInstance.format(Calendar.getInstance().getTime())));
            }
        });
    }

    private void initFirebase() {
        // ...
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            onFirebaseConnected();
                        }
                    }
                });
    }

    private void onFirebaseConnected() {
        findViewById(R.id.add_entry).setEnabled(true);
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        verifyFirebaseConnection();

        DatabaseReference myRef = database.getReference("message");

        long MillisInHour = 60*60*1000;
        //Query query = myRef.orderByChild("timeStamp").startAt(System.currentTimeMillis()).endAt(System.currentTimeMillis() - MillisInHour);
        query = myRef.orderByChild("timeStamp").endAt(System.currentTimeMillis()).startAt(System.currentTimeMillis() - MillisInHour);
        query.addValueEventListener(new ValueEventListener() {
        //query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String list = "";
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    LogEntry logEntry = postSnapshot.getValue(LogEntry.class);
                    if(list.length() > 0)
                        list += "\n";

                    list += logEntry.getDescription();
                    //Log.d(TAG, "Value is: " + logEntry.getDescription());
                    //Log.d(TAG, "Value is: " + postSnapshot.getValue());
                }
                mStatusTextView.setText(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
        //query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LogEntry logEntry = dataSnapshot.getValue(LogEntry.class);
                Log.d(TAG, "Previous Id: " + s + ", Value: " + logEntry.getDescription());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void verifyFirebaseConnection() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == true) {
                //    Snackbar.make(view, getResources().getString(R.string.firebase_connected), Snackbar.LENGTH_SHORT).show();
                }
                else{
                //    Snackbar.make(view, getResources().getString(R.string.firebase_not_connected), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
