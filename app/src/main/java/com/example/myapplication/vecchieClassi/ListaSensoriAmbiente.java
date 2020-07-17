package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ListaSensoriAmbiente extends AppCompatActivity {

    TextView ok1, ok2,ok3,ok4;
    Button prova1,prova2,prova3,prova4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_sensori_ambiente);

        ok1 = (TextView) findViewById(R.id.Ok1);
        prova1 = (Button) findViewById(R.id.test1);

        ok2 = (TextView) findViewById(R.id.ok2);
        prova2 = (Button) findViewById(R.id.test2);

        ok3 = (TextView) findViewById(R.id.ok3);
        prova3 = (Button) findViewById(R.id.test3);

        ok4 = (TextView) findViewById(R.id.ok4);
        prova4 = (Button) findViewById(R.id.test4);

        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //1.Temperatura Ambiente
        if (manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            ok1.setText("Supportato");
            prova1.setEnabled(true);
        }else {
            ok1.setText("Non Supportato");
            prova1.setEnabled(false);
        }

        //2.Illuminazione
        if (manager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            ok2.setText("Supportato");
            prova2.setEnabled(true);
        }else {
            ok2.setText("Non Supportato");
            prova2.setEnabled(false);
        }

        //3.Pressione
        if (manager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            ok3.setText("Supportato");
            prova3.setEnabled(true);
        }else {
            ok3.setText("Non Supportato");
            prova3.setEnabled(false);
        }

        //4.Umidità Relativa
        if (manager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null){
            ok4.setText("Supportato");
            prova4.setEnabled(true);
        }else {
            ok4.setText("Non Supportato");
            prova4.setEnabled(false);
        }

        prova1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriAmbiente.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Temperatura Ambiente");
                intent.putExtra("TipoSensore", Sensor.TYPE_AMBIENT_TEMPERATURE);
                startActivity(intent);
            }
        });

        prova2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriAmbiente.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Illuminazione");
                intent.putExtra("TipoSensore", Sensor.TYPE_LIGHT);
                startActivity(intent);
            }
        });

        prova3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriAmbiente.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Pressione");
                intent.putExtra("TipoSensore", Sensor.TYPE_PRESSURE);
                startActivity(intent);
            }
        });

        prova4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriAmbiente.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Umidità Relativa");
                intent.putExtra("TipoSensore", Sensor.TYPE_RELATIVE_HUMIDITY);
                startActivity(intent);
            }
        });

    }
}