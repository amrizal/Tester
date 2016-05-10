package com.stationalertapp.malaysia.firebasetester;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ACCOUNT_CODE = 1601;
    private static final int AUTHORIZATION_CODE = 1993;
    private static final String SCOPE = "email";
    private Firebase myFirebaseRef;
    TextView textView;
    private AccountManager accountManager;
    private AuthPreferences authPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.message_content);

        accountManager = AccountManager.get(this);

        authPreferences = new AuthPreferences(this);
        if (authPreferences.getUser() != null
                && authPreferences.getToken() != null) {
            doCoolAuthenticatedStuff();
        } else {
            chooseAccount();
        }
    }

    private void doCoolAuthenticatedStuff() {
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        myFirebaseRef.authWithOAuthToken("google", authPreferences.getToken(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {

            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {

            }
        });
        myFirebaseRef.child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textView.setText((CharSequence) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void chooseAccount() {
        // use https://github.com/frakbot/Android-AccountChooser for
        // compatibility with older devices
        Intent intent = AccountManager.newChooseAccountIntent(null, null,
                new String[] { "com.google" }, false, null, null, null, null);
        startActivityForResult(intent, ACCOUNT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == AUTHORIZATION_CODE) {
                requestToken();
            } else if (requestCode == ACCOUNT_CODE) {
                String accountName = data
                        .getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                authPreferences.setUser(accountName);

                // invalidate old tokens which might be cached. we want a fresh
                // one, which is guaranteed to work
                invalidateToken();

                requestToken();
            }
        }
    }

    private void invalidateToken() {
        AccountManager accountManager = AccountManager.get(this);
        accountManager.invalidateAuthToken("com.google", authPreferences.getToken());

        authPreferences.setToken(null);
    }

    private void requestToken() {
        Account userAccount = null;
        String user = authPreferences.getUser();
        for (Account account : accountManager.getAccountsByType("com.google")) {
            if (account.name.equals(user)) {
                userAccount = account;

                break;
            }
        }

        accountManager.getAuthToken(userAccount, "oauth2:" + SCOPE, null, this,
                new OnTokenAcquired(), null);
    }

    void OnClick(View view){
        switch (view.getId()){
            case R.id.btn_send_message:
                String message = DateFormat.getDateTimeInstance().format(new Date());
                myFirebaseRef.child("message").setValue(message);
                break;
            case R.id.btn_get_message:
                break;
            case R.id.btn_invalidate_account:
                invalidateToken();
                break;
            default:
                break;
        }
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();

                Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, AUTHORIZATION_CODE);
                } else {
                    String token = bundle
                            .getString(AccountManager.KEY_AUTHTOKEN);

                    authPreferences.setToken(token);

                    doCoolAuthenticatedStuff();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
