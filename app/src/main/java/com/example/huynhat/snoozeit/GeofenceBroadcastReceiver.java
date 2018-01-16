package com.example.huynhat.snoozeit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by huynhat on 2018-01-15.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get the Geofence event from the intent sent through
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            Log.e(TAG,String.format("Error: %d", geofencingEvent.getErrorCode()));
        }

        //Get the transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        //Check which transition type has triggered this event
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            Toast.makeText(context, "You are close to your destination", Toast.LENGTH_SHORT).show();
        }else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            Toast.makeText(context, "You have missed your destination", Toast.LENGTH_SHORT).show();
        }else {
            return;
        }

        sendNotification(context, geofenceTransition);

    }

    private void sendNotification(Context context, int transitionType){
        //Create an explicit content Intent that starts the main Activity
        Intent notitfictionIntent = new Intent(context, MainActivity.class);
        //Construct a task class
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notitfictionIntent);

        //Get a PendingIntent
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //Get a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER){
            builder.setContentTitle("You are really close to your destination");
        }else if ( transitionType == Geofence.GEOFENCE_TRANSITION_EXIT){
            builder.setContentTitle("You have missed your destination");
        }
        builder.setContentTitle("Touch to launch SnoozeIt App");
        builder.setContentIntent(notificationPendingIntent);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }
}
