package com.example.huynhat.snoozeit.Utils;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;

/**
 * Uses Google Play API for obtaining device locations
 * Created by alejandro.tkachuk
 * alejandro@calculistik.com
 * www.calculistik.com Mobile Development
 *
 * Ref: https://stackoverflow.com/questions/28535703/best-way-to-get-user-gps-location-in-background-in-android
 */

public class Whereabouts {

    public static final Whereabouts instance = new Whereabouts();

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;

    //private Workable<GPSPoint> workable;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    private Whereabouts(){
        this.locationRequest = new LocationRequest();
        this.locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        this.locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(this.locationRequest);
        this.locationSettingsRequest = builder.build();

        this.locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location currentLocation = locationResult.getLastLocation();

                //GPSPoint gpsPoint = new

            }
        };
    }



}
