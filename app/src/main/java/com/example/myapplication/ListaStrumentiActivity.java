package com.example.myapplication;

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
        TextView textLivella = findViewById(R.id.textLivella);
        TextView textLuminosita = findViewById(R.id.textLuminosità);
        TextView textTermometro = findViewById(R.id.textTermometro);
        TextView textBarometro = findViewById(R.id.textBarometro);
        TextView textUmidita = findViewById(R.id.textUmidità);
        TextView textRumore = findViewById(R.id.textRumore);

        TextView bussolaNonSupportato = findViewById(R.id.bussolaNonSupportato);
        TextView contapassiNonSupportato = findViewById(R.id.contapassiNonSupportato);
        TextView livellaNonSupportato = findViewById(R.id.livellaNonSupportato);
        TextView luminositàNonSupportato = findViewById(R.id.luminositàNonSupportato);
        TextView luminositaNonSupportato = findViewById(R.id.luminositàNonSupportato);
        TextView termometroNonSupportato = findViewById(R.id.termometroNonSupportato);
        TextView barometroNonSupportato = findViewById(R.id.barometroNonSupportato);
        TextView umiditaNonSupportato = findViewById(R.id.umiditàNonSupportato);
        TextView rumoreNonSupportato = findViewById(R.id.rumoreNonSupportato);

        TableRow bussola = findViewById(R.id.bussola);
        TableRow contapassi = findViewById(R.id.contapassi);
        TableRow livella = findViewById(R.id.livella);
        TableRow luminosita = findViewById(R.id.luminosità);
        TableRow termometro = findViewById(R.id.termometro);
        TableRow barometro = findViewById(R.id.barometro);
        TableRow umidita = findViewById(R.id.umidità);
        TableRow rumore = findViewById(R.id.rumore);

        SensorManager manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        if (manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            textContapassi.setTypeface(textContapassi.getTypeface(), Typeface.BOLD);
        }else{
            contapassiNonSupportato.setVisibility(View.VISIBLE);
            contapassi.setEnabled(false);
        }

        if (manager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            textLuminosita.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        }else{
            luminositaNonSupportato.setVisibility(View.VISIBLE);
            luminosita.setEnabled(false);
        }

        if (manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            textTermometro.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        }else{
            termometroNonSupportato.setVisibility(View.VISIBLE);
            termometro.setEnabled(false);
        }

        if (manager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            textUmidita.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        }else{
            umiditaNonSupportato.setVisibility(View.VISIBLE);
            umidita.setEnabled(false);
        }



        bussola.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                /*Intent intent = new Intent(MainActivity.this,ListaSensoriMovimento.class);
                startActivity(intent);*/
            }
        });

        contapassi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                /*Intent intent = new Intent(MainActivity.this,ListaSensoriMovimento.class);
                startActivity(intent);*/
            }
        });

        livella.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                /*Intent intent = new Intent(MainActivity.this,ListaSensoriMovimento.class);
                startActivity(intent);*/
            }
        });

        luminosita.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                Intent intent = new Intent(ListaStrumentiActivity.this,LightActivity.class);
                startActivity(intent);
            }
        });

        termometro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                /*Intent intent = new Intent(MainActivity.this,ListaSensoriMovimento.class);
                startActivity(intent);*/
            }
        });

        barometro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                /*Intent intent = new Intent(MainActivity.this,ListaSensoriMovimento.class);
                startActivity(intent);*/
            }
        });

        umidita.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                /*Intent intent = new Intent(MainActivity.this,ListaSensoriMovimento.class);
                startActivity(intent);*/
            }
        });

        rumore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ListaStrumentiActivity.this, R.anim.button_click));
                /*Intent intent = new Intent(MainActivity.this,ListaSensoriMovimento.class);
                startActivity(intent);*/
            }
        });

    }
}