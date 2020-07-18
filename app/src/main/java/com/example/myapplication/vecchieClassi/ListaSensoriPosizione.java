package com.example.myapplication.vecchieClassi;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.GestioneSensori;
import com.example.myapplication.R;

public class ListaSensoriPosizione extends AppCompatActivity {

    TextView ok1, ok2,ok3,ok4,ok5;
    Button prova1,prova2,prova3,prova4,prova5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_sensori_posizione);

        ok1 = (TextView) findViewById(R.id.Ok1);
        prova1 = (Button) findViewById(R.id.test1);

        ok2 = (TextView) findViewById(R.id.ok2);
        prova2 = (Button) findViewById(R.id.test2);

        ok3 = (TextView) findViewById(R.id.ok3);
        prova3 = (Button) findViewById(R.id.test3);

        ok4 = (TextView) findViewById(R.id.ok4);
        prova4 = (Button) findViewById(R.id.test4);

        ok5 = (TextView) findViewById(R.id.ok5);
        prova5 = (Button) findViewById(R.id.test5);

        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        //1.Vettore di Rotazione di gioco
        if (manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null){
            ok1.setText("Supportato");
            prova1.setEnabled(true);
        }else {
            ok1.setText("Non Supportato");
            prova1.setEnabled(false);
        }

        //2.Vettore di Rotazione Geomagnetico
        if (manager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) != null){
            ok2.setText("Supportato");
            prova2.setEnabled(true);
        }else {
            ok2.setText("Non Supportato");
            prova2.setEnabled(false);
        }

        //3.Campo Magnetico
        if (manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            ok3.setText("Supportato");
            prova3.setEnabled(true);
        }else {
            ok3.setText("Non Supportato");
            prova3.setEnabled(false);
        }

        //4.Campo Magnetico non Calibrato
        if (manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) != null){
            ok4.setText("Supportato");
            prova4.setEnabled(true);
        }else {
            ok4.setText("Non Supportato");
            prova4.setEnabled(false);
        }

        //5.Prossimità
        if (manager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            ok5.setText("Supportato");
            prova5.setEnabled(true);
        }else {
            ok5.setText("Non Supportato");
            prova5.setEnabled(false);
        }

        prova1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriPosizione.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Vettore di Rotazione di gioco");
                intent.putExtra("TipoSensore", Sensor.TYPE_GAME_ROTATION_VECTOR);
                startActivity(intent);
            }
        });

        prova2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriPosizione.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Vettore di Rotazione Geomagnetico");
                intent.putExtra("TipoSensore", Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
                startActivity(intent);
            }
        });

        prova3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriPosizione.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Campo Magnetico");
                intent.putExtra("TipoSensore", Sensor.TYPE_MAGNETIC_FIELD);
                startActivity(intent);
            }
        });

        prova4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriPosizione.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Campo Magnetico non Calibrato");
                intent.putExtra("TipoSensore", Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
                startActivity(intent);
            }
        });

        prova5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriPosizione.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Prossimità");
                intent.putExtra("TipoSensore", Sensor.TYPE_PROXIMITY);
                startActivity(intent);
            }
        });
    }
}