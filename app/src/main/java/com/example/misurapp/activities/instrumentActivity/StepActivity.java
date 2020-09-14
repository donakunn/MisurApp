package com.example.misurapp.activities.instrumentActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.R;
import com.example.misurapp.activities.MisurAppInstrumentBaseActivity;
import com.example.misurapp.utility.SaveAndFeedback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * This class is about defining the layout, showing the value read by the sensors,
 * showing an animation and allowing you to save the read value for the StepCounter instrument
 */
public class StepActivity extends MisurAppInstrumentBaseActivity implements SensorEventListener {
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
     * TextView to show current values
     */
    private TextView misura;
    /**
     * View containing an animation relative to the instrument activity
     */
    private ImageView imageView;
    /**
     * last value registered  before stopping the activity
     */
    private int stepsRegister = 0;
    /**
     * last displayed value before stopping activity
     */
    private int stepsShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_step);

        stepsRegister = prefs.getInt("reset", 0);

        instrumentName = "lastStepsRegister";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        imageView = findViewById(R.id.img_animazione);
        misura = findViewById(R.id.misura);

        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(StepActivity.this,
                    R.anim.button_click));

            stepsRegister = prefs.getInt("reset", 0);
            stepsShow = 0;

            editor.putInt("stepsDisplay", 0);
            editor.apply();
            setUpText(0);
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(StepActivity.this,
                    R.anim.button_click));
            SaveAndFeedback.saveAndMakeToast(dbManager, getApplicationContext(),
                    instrumentName, (float) stepsShow);
        });

    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
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
        if ((int) event.values[0] == 0) {
            Log.d(TAG, "onSensorChanged");
            stepsRegister = 0;
        }
        stepsShow = ((int) event.values[0] - stepsRegister) + prefs
                .getInt("stepsDisplay", 0);

        if (stepsShow % 2 == 0) {
            imageView.setImageResource(R.drawable.contapassi1);
        } else {
            imageView.setImageResource(R.drawable.contapassi2);
        }
        setUpText(stepsShow);

        editor.putInt("stepsDisplay", stepsShow);
        editor.apply();

        editor.putInt("reset", (int) event.values[0]);
        editor.apply();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Set TextView text with proper plural
     *
     * @param nlastStepsRegister registered steps to show
     */
    private void setUpText(int nlastStepsRegister) {
        Log.d(TAG, "Setup text on TextView");
        String lastStepsRegisterDetect = getResources().getQuantityString(R.plurals.numberOfSteps, nlastStepsRegister, nlastStepsRegister);
        misura.setText(lastStepsRegisterDetect);
    }
}