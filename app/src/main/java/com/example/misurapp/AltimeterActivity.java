package com.example.misurapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.utility.RoundOffUtility;
import com.example.misurapp.utility.SaveAndFeedback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AltimeterActivity extends MisurAppInstrumentBaseActivity
        implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private ImageView imageView;
    private float valore;
    private TextView misura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altimeter);

        instrumentName = "altimeter";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        imageView = findViewById(R.id.img_animazione);
        misura = findViewById(R.id.misura);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(AltimeterActivity.this, R.anim.button_click));
                SaveAndFeedback.saveAndMakeToast(dbManager,getApplicationContext(),instrumentName,valore);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        valore = event.values[0];

        float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, valore);

        if (altitude > 6000 || altitude < 0) {
            imageView.setRotation(0);
        } else {
            float angle = ((altitude * 360) / 6000);
            imageView.setRotation(angle);
        }
        misura.setText(RoundOffUtility.roundOffNumber(altitude) + " m");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}