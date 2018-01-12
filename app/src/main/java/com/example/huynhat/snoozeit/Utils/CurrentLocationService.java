package com.example.huynhat.snoozeit.Utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by huynhat on 2018-01-10.
 */

public class CurrentLocationService extends Service implements LocationListener {

    private LocationManager locationManager;
    Location location;

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longtitude;
    public static String str_receiver= "servicetutorial.service.receiver";

    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 10000;
    Intent intent;

    public CurrentLocationService (){

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(),5,notify_interval);
        intent = new Intent(str_receiver);



    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @SuppressLint("MissingPermission")
    private void fn_getLocation(){
        locationManager = (LocationManager)  getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isNetworkEnable && !isGPSEnable){
            Toast.makeText(this, "Please turn GPS ON", Toast.LENGTH_SHORT).show();
        }else {

            if(isNetworkEnable){
                location = null;
                if (locationManager!=null) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this);

                    if(location==null){
                        latitude = location.getLatitude();
                        longtitude= location.getLongitude();

                        fn_update(location);
                    }
                }
            }

            if(isGPSEnable){
                location=null;
                if(locationManager!=null){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0, this);

                    if(location!=null){
                        latitude=location.getLatitude();
                        longtitude= location.getLongitude();
                        fn_update(location);
                    }
                }

            }
        }
    }

    private void fn_update(Location location){
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longtitude", location.getLongitude());
        sendBroadcast(intent);
    }


    private class TimerTaskToGetLocation extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getLocation();
                }
            });
        }
    }
}
