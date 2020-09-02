package com.example.misurapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.db.DbManager;
import com.example.misurapp.utility.SaveAndFeedback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StepActivity extends MisurAppInstrumentBaseActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private TextView misura;
    private ImageView imageView;
    private DbManager dbManager = new DbManager(this);
    private int stepsRegister = 0;
    private int stepsShow;

    SharedPreferences lastStepRegister;
    SharedPreferences.Editor editorLastStepRegister;

    SharedPreferences stepDisplay;
    SharedPreferences.Editor editorStepDisplay;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        lastStepRegister = getSharedPreferences("reset", MODE_PRIVATE);
        editorLastStepRegister = lastStepRegister.edit();
        stepsRegister = lastStepRegister.getInt("reset",0);

        stepDisplay = getSharedPreferences("stepsDisplay", MODE_PRIVATE);
        editorStepDisplay = stepDisplay.edit();

        instrumentName ="lastStepsRegister";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        imageView = findViewById(R.id.img_animazione);
        misura =  findViewById(R.id.misura);


        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(StepActivity.this,
                    R.anim.button_click));

            stepsRegister = lastStepRegister.getInt("reset",0);
            stepsShow = 0;

            editorStepDisplay.putInt("stepsDisplay",0);
            editorStepDisplay.apply();
            setUpText(0);
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(StepActivity.this,
                    R.anim.button_click));
            SaveAndFeedback.saveAndMakeToast(dbManager,getApplicationContext(),
                    instrumentName,(float)stepsShow);


            //feedback
            Toast toast = Toast.makeText(getApplicationContext(),getResources()
                    .getString(R.string.salvato) , Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 300);
            toast.show();
        });

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    protected void onPause() {
        super.onPause();
    }


    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if((int) event.values[0] == 0){
            stepsRegister = 0;
        }
        stepsShow =  ((int) event.values[0] - stepsRegister) + stepDisplay
                .getInt("stepsDisplay", 0);

        if (stepsShow % 2 == 0) {
            imageView.setImageResource(R.drawable.contapassi1);
        } else {
            imageView.setImageResource(R.drawable.contapassi2);
        }
        setUpText(stepsShow);

        editorStepDisplay.putInt("stepsDisplay",stepsShow);
        editorStepDisplay.apply();

        editorLastStepRegister.putInt("reset",(int) event.values[0]);
        editorLastStepRegister.apply();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //plurals
    private void setUpText(int nlastStepsRegister) {
        String lastStepsRegisterDetect = getResources().getQuantityString(R.plurals.numberOfSteps, nlastStepsRegister, nlastStepsRegister);
        misura.setText(lastStepsRegisterDetect);
    }
}