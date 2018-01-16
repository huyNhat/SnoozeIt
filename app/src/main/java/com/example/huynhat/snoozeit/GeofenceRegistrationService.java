package com.example.huynhat.snoozeit;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by huynhat on 2018-01-16.
 */

public class GeofenceRegistrationService extends IntentService {
    private static final String TAG = "GeoIntentService";


    public GeofenceRegistrationService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){

        }else {
            int transaction = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            if(transaction == Geofence.GEOFENCE_TRANSITION_ENTER){
                Toast.makeText(this, "Entering the area", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Outside of the area", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
