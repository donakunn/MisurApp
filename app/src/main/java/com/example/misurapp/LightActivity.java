package com.example.misurapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.utility.RoundOffUtility;
import com.example.misurapp.utility.SaveAndFeedback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LightActivity extends MisurAppInstrumentBaseActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private ImageView lampadina;
    private float value;
    private TextView measure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        instrumentName ="photometer";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        lampadina = findViewById(R.id.img_lampadina);
        measure = findViewById(R.id.misura);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(LightActivity.this,
                    R.anim.button_click));
            SaveAndFeedback.saveAndMakeToast(dbManager,getApplicationContext(),
                    instrumentName, value);
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
        value = event.values[0];

        measure.setText(getString(R.string.light_textViewContent,
                RoundOffUtility.roundOffNumber(value)));

        if (value < 10){
            lampadina.setImageResource(R.drawable.img_lampadina0);
        }
        if (value > 10 && value < 100){
            lampadina.setImageResource(R.drawable.img_lampadina1);
        }
        if (value > 100 && value < 500){
            lampadina.setImageResource(R.drawable.img_lampadina2);
        }
        if (value > 500 && value < 10000){
            lampadina.setImageResource(R.drawable.img_lampadina3);
        }
        if (value > 10000 && value < 50000){
            lampadina.setImageResource(R.drawable.img_lampadina4);
        }
        if (value > 50000 && value < 100000){
            lampadina.setImageResource(R.drawable.img_lampadina5);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}