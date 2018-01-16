package com.example.huynhat.snoozeit;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huynhat on 2018-01-15.
 */

public class Geofencing implements ResultCallback {
    private static final String TAG = Geofencing.class.getSimpleName();
    public static final float GEOFENCE_RADIUS = 50; //50 meters
    private static final long GEOFENCE_TIMEOUT = 24*60*60*1000; //24 hours

    private List<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;
    private GoogleApiClient googleApiClient;
    private Context context;
    private Geofence mGeofence;

    public Geofencing (Context context, GoogleApiClient googleApiClient){
        this.context = context;
        this.googleApiClient = googleApiClient;
        geofencePendingIntent = null;
        //geofenceList = new ArrayList<>();
        mGeofence = null;
    }


    public void registerGeofence(){
        //Check that the API client is connected and that the list has Geofences in it

        try{
            LocationServices.GeofencingApi.addGeofences(googleApiClient, getGeofencingRequest(),
                    getGeofencePendingIntent()).setResultCallback(this);
        }catch (SecurityException ex){
            Log.d(TAG, ex.getMessage());
        }
    }

    public void unRegisterAllGeofences() {

        try {
            LocationServices.GeofencingApi.removeGeofences(
                    googleApiClient,
                    // This is the same pending intent that was used in registerGeofences
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }

    public void updateGeofencesList(Place place){
        /*
        geofenceList = new ArrayList<>();
        if(places==null || places.getCount()==0){
            return;
        }
        for(Place place : places){
            String placeUID= place.getId();
            double placeLat= place.getLatLng().latitude;
            double placeLong = place.getLatLng().longitude;

            //Building Geofence Object
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeUID)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(placeLat, placeLong,GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            //add it the the list
            geofenceList.add(geofence);

        }
        */

        String placeUID= place.getId();
        double placeLat= place.getLatLng().latitude;
        double placeLong = place.getLatLng().longitude;

        //Building Geofence Object
        mGeofence = new Geofence.Builder()
                .setRequestId(placeUID)
                .setExpirationDuration(GEOFENCE_TIMEOUT)
                .setCircularRegion(placeLat, placeLong,GEOFENCE_RADIUS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();


    }

    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        //builder.addGeofences(geofenceList);
        builder.addGeofence(mGeofence);

        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(){
        if(geofencePendingIntent !=null){
            return geofencePendingIntent;
        }

        Intent intent = new Intent(context, GeofenceRegistrationService.class);
        geofencePendingIntent = PendingIntent.getService(context, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }


    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG, String.format("Error adding/removing geofence : %s",
                result.getStatus().toString()));
    }
}
