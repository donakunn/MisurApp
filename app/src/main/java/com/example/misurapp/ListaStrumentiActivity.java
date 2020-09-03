package com.example.misurapp;

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

import androidx.appcompat.widget.Toolbar;

/**
 * This activity ini
 */
public class ListaStrumentiActivity extends MisurAppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_strumenti);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textBussola = findViewById(R.id.textBussola);
        TextView textContapassi = findViewById(R.id.textContapassi);
        TextView textLuminosita = findViewById(R.id.textLuminosità);
        TextView textTermometro = findViewById(R.id.textTermometro);
        TextView textBarometro = findViewById(R.id.textBarometro);
        TextView textUmidita = findViewById(R.id.textUmidità);
        TextView textAltimetro = findViewById(R.id.textAltimetro);

        TextView bussolaNonSupportato = findViewById(R.id.bussolaNonSupportato);
        TextView contapassiNonSupportato = findViewById(R.id.contapassiNonSupportato);
        TextView luminositaNonSupportato = findViewById(R.id.luminositàNonSupportato);
        TextView termometroNonSupportato = findViewById(R.id.termometroNonSupportato);
        TextView barometroNonSupportato = findViewById(R.id.barometroNonSupportato);
        TextView umiditaNonSupportato = findViewById(R.id.umiditàNonSupportato);
        TextView altimetroNonSupportato = findViewById(R.id.altimetroNonSupportato);

        TableRow bussola = findViewById(R.id.bussola);
        TableRow contapassi = findViewById(R.id.contapassi);
        TableRow luminosita = findViewById(R.id.luminosità);
        TableRow termometro = findViewById(R.id.termometro);
        TableRow barometro = findViewById(R.id.barometro);
        TableRow umidita = findViewById(R.id.umidità);
        TableRow altimetro = findViewById(R.id.altimetro);

        SensorManager manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        //contapassi
        if (manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            textContapassi.setTypeface(textContapassi.getTypeface(), Typeface.BOLD);
        } else {
            contapassiNonSupportato.setVisibility(View.VISIBLE);
            contapassi.setEnabled(false);
        }

        //bussola
        if (manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) ||
                    (manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                bussolaNonSupportato.setVisibility(View.VISIBLE);
                bussola.setEnabled(false);
            } else {
                textBussola.setTypeface(textBussola.getTypeface(), Typeface.BOLD);
            }
        } else {
            textBussola.setTypeface(textBussola.getTypeface(), Typeface.BOLD);
        }

        //luminosità
        if (manager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            textLuminosita.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        } else {
            luminositaNonSupportato.setVisibility(View.VISIBLE);
            luminosita.setEnabled(false);
        }

        //temperatura
        if (manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            textTermometro.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        } else {
            termometroNonSupportato.setVisibility(View.VISIBLE);
            termometro.setEnabled(false);
        }

        //umidità
        if (manager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            textUmidita.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        } else {
            umiditaNonSupportato.setVisibility(View.VISIBLE);
            umidita.setEnabled(false);
        }

        //pressione - altimetro
        if (manager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            textBarometro.setTypeface(textBarometro.getTypeface(), Typeface.BOLD);
            textAltimetro.setTypeface(textAltimetro.getTypeface(), Typeface.BOLD);
        } else {
            barometroNonSupportato.setVisibility(View.VISIBLE);
            barometro.setEnabled(false);
            altimetroNonSupportato.setVisibility(View.VISIBLE);
            altimetro.setEnabled(false);
        }


        bussola.setOnClickListener(v -> onClickOperation(v,CompassActivity.class));

        contapassi.setOnClickListener(v -> onClickOperation(v,StepActivity.class));

        luminosita.setOnClickListener(v -> onClickOperation(v,LightActivity.class));

        termometro.setOnClickListener(v -> onClickOperation(v,ThermometerActivity.class));

        barometro.setOnClickListener(v -> onClickOperation(v,BarometerActivity.class));

        umidita.setOnClickListener(v -> onClickOperation(v,HygrometerActivity.class));

        altimetro.setOnClickListener(v -> onClickOperation(v,AltimeterActivity.class));
    }

    private void onClickOperation(View v, Class<?> nextActivityClass) {
        v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this,
                R.anim.button_click));
        Intent intent = new Intent(ListaStrumentiActivity.this, nextActivityClass);
        startActivity(intent);
    }
}