package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StepActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private TextView misura;
    private ImageView imageView;
    private Integer steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        imageView = findViewById(R.id.img_animazione);
        misura =  findViewById(R.id.misura);

        ImageButton salva = findViewById(R.id.salva);
        salva.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(StepActivity.this, R.anim.button_click));


                //feedback
                Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.salvato) , Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM| Gravity.END, 0, 0);
                toast.show();
            }
        });

        ImageButton dati = findViewById(R.id.datiSalvati);
        dati.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(StepActivity.this, R.anim.button_click));
                Intent intent = new Intent(StepActivity.this,BoyscoutDBValuesActivity.class);
                startActivity(intent);
            }
        });

    }
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        steps = (int) event.values[0];

        if (steps % 2 == 0) {
            imageView.setImageResource(R.drawable.contapassi1);
        } else {
            imageView.setImageResource(R.drawable.contapassi2);
        }
        misura.setText(steps.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}