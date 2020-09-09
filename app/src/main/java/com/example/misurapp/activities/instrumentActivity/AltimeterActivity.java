package com.example.misurapp.activities.instrumentActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.R;
import com.example.misurapp.activities.MisurAppInstrumentBaseActivity;
import com.example.misurapp.utility.RoundOffUtility;
import com.example.misurapp.utility.SaveAndFeedback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * This class is about defining the layout, showing the value read by the sensors,
 * showing an animation and allowing you to save the read value for the altimeter instrument
 */
public class AltimeterActivity extends MisurAppInstrumentBaseActivity
        implements SensorEventListener {
    /**
     * Debug Tag
     */
    private final String TAG = this.getClass().toString();
    /**
     * SensorManager object to manage access to device's sensors.
     */
    private SensorManager mSensorManager;
    /**
     * object representing a sensor
     */
    private Sensor sensor;
    /**
     *  View containing an animation relative to the instrument activity
     */
    private ImageView imageView;
    /**
     * value read
     */
    private float value;
    /**
     * TextView to show current values
     */
    private TextView measure;
    /**
     * registered altitude value
     */
    private float altitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_altimeter);

        instrumentName = "altimeter";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        imageView = findViewById(R.id.img_animazione);
        measure = findViewById(R.id.misura);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(AltimeterActivity.this,
                    R.anim.button_click));
            SaveAndFeedback.saveAndMakeToast(dbManager,getApplicationContext(),
                    instrumentName, altitude);
        });
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        mSensorManager.unregisterListener(this);
    }

    /**
     * Refresh animation based on the value read by the sensor
     * @param event Sensor event object wich holds information such as the sensor's type,
     * the time-stamp, accuracy and of course the sensor's SensorEvent#values
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG,"onSensorChanged");
        value = event.values[0];

        altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
                value);

        if (altitude > 6000 || altitude < 0) {
            imageView.setRotation(0);
        } else {
            float angle = ((altitude * 360) / 6000);
            imageView.setRotation(angle);
        }
        measure.setText(getString(R.string.altitude_textViewContent,
                RoundOffUtility.roundOffNumber(altitude)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}