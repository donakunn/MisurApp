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

public class AltimeterActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private ImageView imageView;
    private float valore;
    private TextView misura;
    private ImageButton salva;
    private ImageButton dati;
    private float angle;
    private float altitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altimeter);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);


        imageView = (ImageView) findViewById(R.id.img_animazione);
        misura = (TextView) findViewById(R.id.misura);

        salva = (ImageButton)  findViewById(R.id.salva);
        salva.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(AltimeterActivity.this, R.anim.button_click));



                //feedback
                Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.salvato) , Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM| Gravity.RIGHT, 0, 0);
                toast.show();
            }
        });

        dati = (ImageButton)  findViewById(R.id.datiSalvati);
        dati.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(AltimeterActivity.this, R.anim.button_click));
                Intent intent = new Intent(AltimeterActivity.this,BoyscoutDBValuesActivity.class);
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

        altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,valore);

        if(altitude>6000 || altitude < 0){
            imageView.setRotation(0);
        }else{
            angle = ((altitude*360)/6000);
            imageView.setRotation(angle);
        }
        misura.setText(altitude+" m");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}