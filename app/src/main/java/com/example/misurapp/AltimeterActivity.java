package com.example.misurapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misurapp.db.DbManager;
import com.example.misurapp.db.InstrumentsDBSchema;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class AltimeterActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private ImageView imageView;
    private float valore;
    private TextView misura;
    private float angle;
    private float altitude;
    private static final String sensorUsed = "altimeter";
    private DbManager dbManager = new DbManager(this);
    String [] listItems;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altimeter);
        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);


        imageView = (ImageView) findViewById(R.id.img_animazione);
        misura = (TextView) findViewById(R.id.misura);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(AltimeterActivity.this, R.anim.button_click));
                SaveAndFeedback.saveAndMakeToast(dbManager,getApplicationContext(),sensorUsed,valore);
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

        altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, valore);

        if (altitude > 6000 || altitude < 0) {
            imageView.setRotation(0);
        } else {
            angle = ((altitude * 360) / 6000);
            imageView.setRotation(angle);
        }
        misura.setText(altitude + " m");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



}