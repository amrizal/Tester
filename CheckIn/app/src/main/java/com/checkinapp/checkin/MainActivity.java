package com.checkinapp.checkin;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkinapp.checkin.model.NearbyPlace;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LocationListener
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AddressResultReceiver mResultReceiver;
    private LocationManager locationManager;
    private String provider;
    private TextView gpsContent;
    private TextView gpsNotFound;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView locationContent;
    private TextView locationNotFound;
    private TextView lastUpdatedContent;
    private String mAddressOutput;
    private TextView placesNotFound;
    private ArrayList<NearbyPlace> places = new ArrayList<>();
    private PlaceAdapter placeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initLocation();
        buildGoogleApiClient();
    }

    private void initLocation() {
        mResultReceiver = new AddressResultReceiver(new Handler());
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            gpsNotFound.setVisibility(View.VISIBLE);
            gpsContent.setVisibility(View.GONE);
        }
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gpsContent = (TextView)findViewById(R.id.gps_content);
        gpsNotFound = (TextView)findViewById(R.id.gps_not_found);
        findViewById(R.id.share_gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareGps();
            }
        });
        findViewById(R.id.show_in_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInMap();
            }
        });

        locationContent = (TextView)findViewById(R.id.location_content);
        locationNotFound = (TextView)findViewById(R.id.location_not_found);
        findViewById(R.id.share_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLocation();
            }
        });

        placesNotFound = (TextView)findViewById(R.id.places_not_found);

        lastUpdatedContent = (TextView)findViewById(R.id.last_updated_content);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        placeAdapter = new PlaceAdapter(this, places);
        ListView list = (ListView)findViewById(R.id.list_place);
        list.setAdapter(placeAdapter);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void shareLocation() {
        String message = "http://maps.google.com/maps?q=loc:"+mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+" ("+ getResources().getString(R.string.im_here)+")";

        Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
        intent2.setType("text/plain");
        intent2.putExtra(Intent.EXTRA_TEXT, message );
        startActivity(Intent.createChooser(intent2, "Share via"));
    }

    private void showInMap() {
        String uri = "http://maps.google.com/maps?q=loc:"+mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+" ("+ getResources().getString(R.string.im_here)+")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    private void shareGps() {
        //String message = String.format(Locale.ENGLISH, "geo:%f,%f", mLastLocation.getLatitude(), mLastLocation.getLongitude());
        String message = "http://maps.google.com/maps?q=loc:"+mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+" ("+ getResources().getString(R.string.im_here)+")";

        Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
        intent2.setType("text/plain");
        intent2.putExtra(Intent.EXTRA_TEXT, message );
        startActivity(Intent.createChooser(intent2, "Share via"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                return;
            }

            startIntentService();
        }
    }

    private void listNearbyPlace() {
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                places.clear();
                for (PlaceLikelihood place : likelyPlaces) {
                    NearbyPlace nearbyPlace = new NearbyPlace();
                    nearbyPlace.setId(place.getPlace().getId());
                    nearbyPlace.setName(String.valueOf(place.getPlace().getName()));
                    nearbyPlace.setDescription(String.valueOf(place.getPlace().getAddress()));
                    places.add(nearbyPlace);
                }
                likelyPlaces.release();
                placeAdapter.notifyDataSetChanged();

                if(places.size() > 0){
                    placesNotFound.setVisibility(View.GONE);
                }
                else
                    placesNotFound.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        lastUpdatedContent.setText("(" + mydate + ")");

        String gps = String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());
        gpsContent.setText(gps);

        gpsContent.setVisibility(View.VISIBLE);
        gpsNotFound.setVisibility(View.GONE);

        startIntentService();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            if(resultCode == 0){
                locationContent.setText(mAddressOutput);
                locationContent.setVisibility(View.VISIBLE);
                locationNotFound.setVisibility(View.GONE);
                listNearbyPlace();
            }else{
                locationContent.setVisibility(View.GONE);
                locationNotFound.setVisibility(View.VISIBLE);
            }
        }
    }
}
