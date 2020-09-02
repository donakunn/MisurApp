package com.example.misurapp;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class ThermometerActivity extends MisurAppInstrumentBaseActivity implements
        SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private ImageView termometro;
    private float value;
    private TextView measure;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermometer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instrumentName ="thermometer";

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);


        termometro = findViewById(R.id.img_animazione);
        measure = findViewById(R.id.misura);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(ThermometerActivity.this,
                    R.anim.button_click));
            SaveAndFeedback.saveAndMakeToast(dbManager,getApplicationContext(),
                    instrumentName, value);
        });
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        if(prefs.getBoolean("flagStrumento", false)){
            editor.putBoolean("flagStrumento", false);
            editor.apply();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        value = event.values[0];

        if (value <= -10){
            termometro.setImageResource(R.drawable.termometro0);
        }

        if (value > -10 && value <= -5){
            termometro.setImageResource(R.drawable.termometro1);
        }

        if (value > -5 && value <= 0){
            termometro.setImageResource(R.drawable.termometro2);
        }

        if (value > 0 && value <= 5){
            termometro.setImageResource(R.drawable.termometro3);
        }

        if (value > 5 && value <= 10){
            termometro.setImageResource(R.drawable.termometro4);
        }

        if (value > 10 && value <= 15){
            termometro.setImageResource(R.drawable.termometro5);
        }

        if (value > 15 && value <= 20){
            termometro.setImageResource(R.drawable.termometro6);
        }

        if (value > 20 && value <= 25){
            termometro.setImageResource(R.drawable.termometro7);
        }

        if (value > 25 && value <= 30){
            termometro.setImageResource(R.drawable.termometro8);
        }

        if (value > 30){
            termometro.setImageResource(R.drawable.termometro9);
        }

        measure.setText(getString(R.string.thermometer_textViewContent,
                RoundOffUtility.roundOffNumber(value)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}