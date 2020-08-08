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

public class ThermometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private ImageView termometro;
    private float valore;
    private TextView misura;
    private ImageButton salva;
    private ImageButton dati;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermometer);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);


        termometro = (ImageView) findViewById(R.id.img_animazione);
        misura = (TextView) findViewById(R.id.misura);


        salva = (ImageButton)  findViewById(R.id.salva);
        salva.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ThermometerActivity.this, R.anim.button_click));



                //feedback
                Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.salvato) , Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM| Gravity.RIGHT, 0, 0);
                toast.show();
            }
        });

        dati = (ImageButton)  findViewById(R.id.datiSalvati);
        dati.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ThermometerActivity.this, R.anim.button_click));
                Intent intent = new Intent(ThermometerActivity.this,BoyscoutDBValuesActivity.class);
                startActivity(intent);
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


        misura.setText(valore+" °C");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}