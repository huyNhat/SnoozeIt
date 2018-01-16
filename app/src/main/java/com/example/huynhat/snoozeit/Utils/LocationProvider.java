package com.example.huynhat.snoozeit.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by huynhat on 2018-01-14.
 */

public class LocationProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String TAG = LocationProvider.class.getSimpleName();


    public abstract interface LocationCallback {
        public void handleNewLocation(Location location);
    }

    /*
    - Define a request code to send to Google Play services
    - This code is returned in the MainActivity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationCallback mLocationCallback;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    //Constructor
    public LocationProvider(Context context, LocationCallback callback) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationCallback = callback;

        //Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000) //10 seconds, in milliseconds
                .setFastestInterval(1 * 1000);//1 second

        mContext = context;

    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location service connected");

        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(mContext);

        try{
            final Task location =  mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful() && task.getResult()!=null){
                        if(location ==null){
                            //mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback,null);
                        }
                        else {
                            mLocationCallback.handleNewLocation((Location)location.getResult());
                        }
                    }else {
                        Toast.makeText(mContext, "Unable to locate | Is your Wifi or Data ON?", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (SecurityException e){
            e.printStackTrace();
        }

        /*
        @SuppressLint("MissingPermission")
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location ==null){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
        }else {
            mLocationCallback.handleNewLocation(location);
        }
        */
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(connectionResult.hasResolution() && mContext instanceof Activity){
            try{
                Activity activity = (Activity) mContext;
                connectionResult.startResolutionForResult(activity,CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }catch (IntentSender.SendIntentException e){
                e.printStackTrace();
            }
        }else {
            Log.i(TAG,"Location services connection failed with code "+ connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationCallback.handleNewLocation(location);
    }


}
