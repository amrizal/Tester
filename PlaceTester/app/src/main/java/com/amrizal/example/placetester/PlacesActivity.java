package com.amrizal.example.placetester;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

public class PlacesActivity extends AppCompatActivity {

    private static final String TAG = PlacesActivity.class.getSimpleName();
    private TextView actionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        setupActionBar();
        searchPlace();
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {

            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View mCustomView = layoutInflater.inflate(R.layout.custom_action_bar, null);

            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(mCustomView);

            Toolbar parent = (Toolbar) mCustomView.getParent();//first get parent toolbar of current action bar
            parent.setContentInsetsAbsolute(16, 0);// set padding programmatically to 0dp

            actionBarTitle = (TextView) findViewById(R.id.action_bar_title);
            findViewById(R.id.search_area).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchPlace();
                }
            });

            findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void searchPlace() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, REQUEST.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                actionBarTitle.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private class REQUEST {
        public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1000;
    }
}
