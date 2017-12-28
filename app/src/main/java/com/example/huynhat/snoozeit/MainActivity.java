package com.example.huynhat.snoozeit;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huynhat.snoozeit.Models.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIAGLOG_REQUEST = 9001;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //Setting bounds
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168),
                                                                    new LatLng(71, 136));

    //Widgets
    private AutoCompleteTextView searchInput;
    private ImageView mGPS;

    //Variables
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15;
    private PlaceAutocompleteAdapter autocompleteAdapter;
    private GoogleApiClient googleApiClient;
    private PlaceInfo mPlace;

    private double currentLongtiude;
    private double currentLattitude;
    private double toLongtitude;
    private double toLattiude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isServicesOK()) {
            setContentView(R.layout.activity_main);
        }

        searchInput = (AutoCompleteTextView) findViewById(R.id.inputSearch);
        mGPS =(ImageView) findViewById(R.id.locateMe);

        getLocationPermission();

    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                //Initialize the map
                initMap();

            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }


    private void init(){
        googleApiClient = new GoogleApiClient.Builder(this)
                                            .addApi(Places.GEO_DATA_API)
                                            .addApi(Places.PLACE_DETECTION_API)
                                            .enableAutoManage(this, this)
                                            .build();
        //OnClick on Item
        searchInput.setOnItemClickListener(autoCompleteListener);

        //Adapter from GoogleAPI Client
        autocompleteAdapter = new PlaceAutocompleteAdapter(this, googleApiClient,LAT_LNG_BOUNDS,null);

        //Set the adpater to the AutocomplteTextview
        searchInput.setAdapter(autocompleteAdapter);

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //Invoke method for searching
                    geoLocate();

                }

                return false;
            }
        });//end of searchInput
        //hideSoftKeyboard();



        mGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceCurrentLocation();
            }
        });
    }

    private void geoLocate(){

        searchInput.clearFocus();
        hideSoftKeyboard();

        String searchStr= searchInput.getText().toString();

        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchStr,1);
        }catch (IOException e){
            e.printStackTrace();
        }

        if(list.size() >0){
            Address address = list.get(0);

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                    DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //Initialize the map
                    initMap();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        customToast("Welcome to Snooze It!");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceCurrentLocation();
            //Set a blue dot to mark that location


            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }


    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
    }

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(MainActivity.this,available,ERROR_DIAGLOG_REQUEST);

            dialog.show();
        }else {
            customToast("Can't make map requests");
        }

        return false;
    }


    public void getDeviceCurrentLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful() && task.getResult() != null){
                        //Location is found
                        Location currentLocation = (Location) task.getResult();

                        currentLattitude = currentLocation.getLatitude();
                        currentLongtiude = currentLocation.getLongitude();


                        //Move & zoon camera to current location
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                DEFAULT_ZOOM, "My location");

                    }else {
                        //Not found
                        customToast("Unable to locate");
                    }
                }
            });

        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        //Moving the cam
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        if(!title.equals("My location")){
            MarkerOptions marker = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(marker);
        }
        hideSoftKeyboard();

    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Google Places API Auto Complete Suggestions
     */

    private AdapterView.OnItemClickListener autoCompleteListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = autocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.
                    getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){

                places.release();
                return;
            }
            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setAttributions(place.getAttributions().toString());
                mPlace.setId(place.getId());
                mPlace.setLatLng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setUriWebsite(place.getWebsiteUri());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());

            } catch (Exception e) {
                e.getMessage();
            }

            toLattiude = place.getLatLng().latitude;
            toLongtitude= place.getLatLng().longitude;

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,place.getViewport().getCenter().longitude)
                    ,DEFAULT_ZOOM, mPlace.getName());

            double distance =calDistance();

            places.release();

            Intent intent = new Intent(MainActivity.this, Second_Activity.class);
            intent.putExtra("Distance",distance);
            startActivity(intent);

        }
    };


    private double calDistance(){
        LatLng from = new LatLng(currentLattitude, currentLongtiude);
        LatLng to = new LatLng(toLattiude, toLongtitude);

        double distance =  SphericalUtil.computeDistanceBetween(from, to);

        customToast("Distance is "+distance);

        return distance;


    }

    private void customToast (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
