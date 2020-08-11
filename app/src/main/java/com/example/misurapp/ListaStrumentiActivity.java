package com.example.misurapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TableRow;
import android.widget.TextView;

public class ListaStrumentiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_strumenti);

        TextView textBussola = findViewById(R.id.textBussola);
        TextView textContapassi = findViewById(R.id.textContapassi);
        TextView textFotometro = findViewById(R.id.textFotometro);
        TextView textTermometro = findViewById(R.id.textTermometro);
        TextView textBarometro = findViewById(R.id.textBarometro);
        TextView textIgrometro = findViewById(R.id.textIgrometro);
        TextView textAltimetro = findViewById(R.id.textAltimetro);

        TextView bussolaNonSupportato = findViewById(R.id.bussolaNonSupportato);
        TextView contapassiNonSupportato = findViewById(R.id.contapassiNonSupportato);
        TextView fotometroNonSupportato = findViewById(R.id.fotometroNonSupportato);
        TextView termometroNonSupportato = findViewById(R.id.termometroNonSupportato);
        TextView barometroNonSupportato = findViewById(R.id.barometroNonSupportato);
        TextView igrometroNonSupportato = findViewById(R.id.igrometroNonSupportato);
        TextView altimetroNonSupportato = findViewById(R.id.altimetroNonSupportato);

        TableRow bussola = findViewById(R.id.bussola);
        TableRow contapassi = findViewById(R.id.contapassi);
        TableRow fotometro = findViewById(R.id.fotometro);
        TableRow termometro = findViewById(R.id.termometro);
        TableRow barometro = findViewById(R.id.barometro);
        TableRow igrometro = findViewById(R.id.igrometro);
        TableRow altimetro = findViewById(R.id.altimetro);

        SensorManager manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        //contapassi
        if (manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            textContapassi.setTypeface(textContapassi.getTypeface(), Typeface.BOLD);
        }else{
            contapassiNonSupportato.setVisibility(View.VISIBLE);
            contapassi.setEnabled(false);
        }

        //bussola
        if (manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)){
                bussolaNonSupportato.setVisibility(View.VISIBLE);
                bussola.setEnabled(false);
            }else{
                textBussola.setTypeface(textBussola.getTypeface(), Typeface.BOLD);
            }
        }else{
            textBussola.setTypeface(textBussola.getTypeface(), Typeface.BOLD);
        }

        //fotometro
        if (manager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            textFotometro.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        }else{
            fotometroNonSupportato.setVisibility(View.VISIBLE);
            fotometro.setEnabled(false);
        }

        //temperatura
        if (manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            textTermometro.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        }else{
            termometroNonSupportato.setVisibility(View.VISIBLE);
            termometro.setEnabled(false);
        }

        //igrometro
        if (manager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            textIgrometro.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        }else{
            igrometroNonSupportato.setVisibility(View.VISIBLE);
            igrometro.setEnabled(false);
        }

        //pressione - altimetro
        if (manager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            textBarometro.setTypeface(textBarometro.getTypeface(), Typeface.BOLD);
            textAltimetro.setTypeface(textAltimetro.getTypeface(), Typeface.BOLD);
        }else{
            barometroNonSupportato.setVisibility(View.VISIBLE);
            barometro.setEnabled(false);
            altimetroNonSupportato.setVisibility(View.VISIBLE);
            altimetro.setEnabled(false);
        }



        bussola.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                Intent intent = new Intent(ListaStrumentiActivity.this,CompassActivity.class);
                startActivity(intent);
            }
        });

        contapassi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                Intent intent = new Intent(ListaStrumentiActivity.this,StepActivity.class);
                startActivity(intent);
            }
        });


        fotometro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                Intent intent = new Intent(ListaStrumentiActivity.this,LightActivity.class);
                startActivity(intent);
            }
        });

        termometro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                Intent intent = new Intent(ListaStrumentiActivity.this,ThermometerActivity.class);
                startActivity(intent);
            }
        });

        barometro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                Intent intent = new Intent(ListaStrumentiActivity.this,BarometerActivity.class);
                startActivity(intent);
            }
        });

        igrometro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                Intent intent = new Intent(ListaStrumentiActivity.this,HygrometerActivity.class);
                startActivity(intent);
            }
        });

        altimetro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                Intent intent = new Intent(ListaStrumentiActivity.this,AltimeterActivity.class);
                startActivity(intent);
            }
        });

    }
}