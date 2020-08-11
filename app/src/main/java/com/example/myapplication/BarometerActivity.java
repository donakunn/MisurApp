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

import com.example.myapplication.db.DbManager;

public class BarometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private ImageView imageView;
    private float valore;
    private TextView misura;
    private ImageButton salva;
    private ImageButton dati;
    private float angle;
    private static final String sensorUsed="barometer";
    private DbManager dbManager = new DbManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        setContentView(R.layout.activity_barometer);

        imageView = (ImageView) findViewById(R.id.img_animazione);
        misura = (TextView) findViewById(R.id.misura);


        salva = (ImageButton)  findViewById(R.id.salva);
        salva.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(BarometerActivity.this, R.anim.button_click));
                dbManager.saveRegisteredValues(sensorUsed,valore);


                //feedback
                Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.salvato) , Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 300);
                toast.show();
            }
        });

        dati = (ImageButton)  findViewById(R.id.datiSalvati);
        dati.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(BarometerActivity.this, R.anim.button_click));
                Intent intent = new Intent(BarometerActivity.this,BoyscoutDBValuesActivity.class);
                intent.putExtra("sensorName",sensorUsed);
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

        if (valore < 970 || valore >1050){
            imageView.setRotation(0);
        }else{
            angle = (((valore - 1010)*360)/80);
            imageView.setRotation((int) angle);
        }
        misura.setText(valore+" hPa");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}