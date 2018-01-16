package com.example.huynhat.snoozeit;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huynhat.snoozeit.Utils.CurrentLocationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.charts.SeriesLabel;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.Random;

public class SnoozeActivity extends AppCompatActivity  {

    private DecoView dynamicDistance;
    private int mBackIndex, mSeries1Index;

    private float mSeriesMax;//Maximum value for each data series

    private ImageView mAndroidImageView;
    private AnimatorSet mRotateAnim;


    // Location classes
    private boolean mTrackingLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private LocationCallback mLocationCallback;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */



    private double curLatitude, toLattiude;
    private double curLongtiude, toLongtitude;

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEdit;

    private BroadcastReceiver broadcastReceiver;


    //FusedLocationProviderClient mFusedLocationClient;
    /*
    private Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            update();
            mHandler.postDelayed(this, 3000);
        }
    };
    */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        dynamicDistance = (DecoView) findViewById(R.id.dynamicArcView);
        dynamicDistance.configureAngles(300, 0);//horseshoe shape

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            mSeriesMax = (float) bundle.getDouble("Distance");
            toLattiude = (double) bundle.getDouble("toLat");
            toLongtitude =(double) bundle.getDouble("toLong");
        }


        createBackSeries();
        createDataSeries1();
        //mHandler.post(runnable);

        createEvents();


    }

    private void createBackSeries() {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        mBackIndex = dynamicDistance.addSeries(seriesItem);
    }

    private void createDataSeries1() {
        final SeriesItem seriesItem = new SeriesItem.Builder(
                Color.parseColor("#4CAF50"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float v) {

            }
        });

        final TextView textTogo = (TextView) findViewById(R.id.textRemaining);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textTogo.setText(String.format("%.1f meter to your destination", seriesItem.getMaxValue() - currentPosition));

            }

            @Override
            public void onSeriesItemDisplayProgress(float v) {

            }
        });


        mSeries1Index = dynamicDistance.addSeries(seriesItem);
    }


    private void createEvents() {
        dynamicDistance.executeReset();

        dynamicDistance.addEvent(new DecoEvent.Builder(mSeriesMax)
                .setIndex(mBackIndex)
                .setDuration(3000)
                .setDelay(1250)
                .build());

        dynamicDistance.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(mSeries1Index)
                .setDuration(2000)
                .setDelay(1250)
                .build());

        dynamicDistance.addEvent(new DecoEvent.Builder(0)
                .setIndex(mSeries1Index)
                .setDelay(3250)
                .build());

    }

    private void update() {
        final Random random = new Random();
        int newPostition = random.nextInt((int) mSeriesMax);

        dynamicDistance.addEvent(new DecoEvent.Builder(newPostition).setIndex(mSeries1Index)
                .setDuration(3000).build());
    }



}
