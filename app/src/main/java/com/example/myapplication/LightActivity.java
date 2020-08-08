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

public class LightActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private ImageView lampadina;
    private float valore;
    private TextView misura;
    private ImageButton salva;
    private ImageButton dati;
    private static final String sensorUsed="Sensor.TYPE_LIGHT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        setContentView(R.layout.activity_light);
        lampadina = (ImageView) findViewById(R.id.img_lampadina);
        misura = (TextView) findViewById(R.id.misura);
        salva = (ImageButton)  findViewById(R.id.salva);
        salva.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(LightActivity.this, R.anim.button_click));
                DbManager dbManager = new DbManager(getApplicationContext());
                dbManager.open();
                dbManager.insertIntoTable(sensorUsed,valore);
                dbManager.close();
                //check Context e open e close

                //feedback
                Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.salvato) , Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 300);
                toast.show();
            }
        });

        dati = (ImageButton)  findViewById(R.id.datiSalvati);
        dati.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(LightActivity.this, R.anim.button_click));
                Intent intent = new Intent(LightActivity.this,BoyscoutDBValuesActivity.class);
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

        misura.setText(valore+" lx");

        if (valore < 10){
            lampadina.setImageResource(R.drawable.img_lampadina0);
        }
        if (valore > 10 && valore < 100){
            lampadina.setImageResource(R.drawable.img_lampadina1);
        }
        if (valore > 100 && valore < 500){
            lampadina.setImageResource(R.drawable.img_lampadina2);
        }
        if (valore > 500 && valore < 10000){
            lampadina.setImageResource(R.drawable.img_lampadina3);
        }
        if (valore > 10000 && valore < 50000){
            lampadina.setImageResource(R.drawable.img_lampadina4);
        }
        if (valore > 50000 && valore < 100000){
            lampadina.setImageResource(R.drawable.img_lampadina5);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}