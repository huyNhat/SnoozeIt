package com.example.huynhat.snoozeit;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.Random;

public class Second_Activity extends AppCompatActivity {

    private DecoView dynamicDistance;
    private int mSeries1Index;
    private final float mSeriesMax= 50f;
    private Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            update();
            mHandler.postDelayed(this, 3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_);

        dynamicDistance = (DecoView) findViewById(R.id.dynamicDistance);
        dynamicDistance.configureAngles(300,0);//horseshoe shape


        createDataSeries1();
        mHandler.post(runnable);


    }

    private void createDataSeries1(){
        final SeriesItem seriesItem = new SeriesItem.Builder(
                Color.parseColor("#4CAF50"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

            mSeries1Index = dynamicDistance.addSeries(seriesItem);
    }

    private void update(){
        final Random random = new Random();
        int newPostition = random.nextInt((int)mSeriesMax);

        dynamicDistance.addEvent(new DecoEvent.Builder(newPostition).setIndex(mSeries1Index)
                                                                    .setDuration(3000).build());
    }


}
