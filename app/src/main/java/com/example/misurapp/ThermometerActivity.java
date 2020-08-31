package com.example.misurapp;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class ThermometerActivity extends MisurAppInstrumentBaseActivity implements
        SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private ImageView termometro;
    private float valore;
    private TextView misura;

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
        misura = findViewById(R.id.misura);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ThermometerActivity.this, R.anim.button_click));
                SaveAndFeedback.saveAndMakeToast(dbManager,getApplicationContext(), instrumentName,valore);
            }
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
        valore = event.values[0];

        if (valore<= -10){
            termometro.setImageResource(R.drawable.termometro0);
        }

        if (valore > -10 && valore <= -5){
            termometro.setImageResource(R.drawable.termometro1);
        }

        if (valore > -5 && valore <= 0){
            termometro.setImageResource(R.drawable.termometro2);
        }

        if (valore > 0 && valore <= 5){
            termometro.setImageResource(R.drawable.termometro3);
        }

        if (valore > 5 && valore <= 10){
            termometro.setImageResource(R.drawable.termometro4);
        }

        if (valore > 10 && valore <= 15){
            termometro.setImageResource(R.drawable.termometro5);
        }

        if (valore > 15 && valore <= 20){
            termometro.setImageResource(R.drawable.termometro6);
        }

        if (valore > 20 && valore <= 25){
            termometro.setImageResource(R.drawable.termometro7);
        }

        if (valore > 25 && valore <= 30){
            termometro.setImageResource(R.drawable.termometro8);
        }

        if (valore > 30){
            termometro.setImageResource(R.drawable.termometro9);
        }

        misura.setText(RoundOffUtility.roundOffNumber(valore) +" °C");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}