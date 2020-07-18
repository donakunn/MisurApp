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

public class ListaSensoriMovimento extends AppCompatActivity {

    //TextView sensore1,sensore2,sensore3,sensore4,sensore5,sensore6,sensore7,sensore8;
    TextView ok1, ok2,ok3,ok4,ok5,ok6,ok7,ok8;
    Button prova1,prova2,prova3,prova4,prova5,prova6,prova7,prova8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_sensori_movimento);

        //sensore1 = (TextView) findViewById(R.id.sensore1);
        ok1 = (TextView) findViewById(R.id.Ok1);
        prova1 = (Button) findViewById(R.id.Test1);

        //sensore2 = (TextView) findViewById(R.id.sensore2);
        ok2 = (TextView) findViewById(R.id.ok2);
        prova2 = (Button) findViewById(R.id.test2);

        //sensore3 = (TextView) findViewById(R.id.sensore3);
        ok3 = (TextView) findViewById(R.id.ok3);
        prova3 = (Button) findViewById(R.id.test3);

        //sensore4 = (TextView) findViewById(R.id.sensore4);
        ok4 = (TextView) findViewById(R.id.ok4);
        prova4 = (Button) findViewById(R.id.test4);

        //sensore5 = (TextView) findViewById(R.id.sensore5);
        ok5 = (TextView) findViewById(R.id.ok5);
        prova5 = (Button) findViewById(R.id.test5);

        //sensore6 = (TextView) findViewById(R.id.sensore6);
        ok6 = (TextView) findViewById(R.id.ok6);
        prova6 = (Button) findViewById(R.id.test6);

        //sensore7 = (TextView) findViewById(R.id.sensore7);
        ok7 = (TextView) findViewById(R.id.ok7);
        prova7 = (Button) findViewById(R.id.test7);

       // sensore8 = (TextView) findViewById(R.id.sensore8);
        ok8 = (TextView) findViewById(R.id.ok8);
        prova8 = (Button) findViewById(R.id.test8);




        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //List<Sensor> list;

        /* list = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            for(int i = 0; i < list.size(); i++) {
                System.out.println("Nome: " + i + " "+ list.get(i).getName());
            }*/

        //1.Accelerometro
        if (manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            ok1.setText("Supportato");
            prova1.setEnabled(true);
        }else {
            ok1.setText("Non Supportato");
            prova1.setEnabled(false);
        }

        //2.Accelerometro non calibrato
       if (manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED) != null){
            ok2.setText("Supportato");
            prova2.setEnabled(true);
        }else {
            ok2.setText("Non Supportato");
            prova2.setEnabled(false);
        }

        //3.Gravità
        if (manager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
            ok3.setText("Supportato");
            prova3.setEnabled(true);
        }else {
            ok3.setText("Non Supportato");
            prova3.setEnabled(false);
        }

        //4.Giroscopio
        if (manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            ok4.setText("Supportato");
            prova4.setEnabled(true);
        }else {
            ok4.setText("Non Supportato");
            prova4.setEnabled(false);
        }

        //5.Giroscopio non calibrato
        if (manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED) != null){
            ok5.setText("Supportato");
            prova5.setEnabled(true);
        }else {
            ok5.setText("Non Supportato");
            prova5.setEnabled(false);
        }

        //6.Accelerazione lineare
        if (manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            ok6.setText("Supportato");
            prova6.setEnabled(true);
        }else {
            ok6.setText("Non Supportato");
            prova6.setEnabled(false);
        }

        //7.Vettore di rotazione
        if (manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null){
            ok7.setText("Supportato");
            prova7.setEnabled(true);
        }else {
            ok7.setText("Non Supportato");
            prova7.setEnabled(false);
        }

        //8.Conta passi
        if (manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            ok8.setText("Supportato");
            prova8.setEnabled(true);
        }else {
            ok8.setText("Non Supportato");
            prova8.setEnabled(false);
        }

        prova1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriMovimento.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Accelerometro");
                intent.putExtra("TipoSensore", Sensor.TYPE_ACCELEROMETER);
                startActivity(intent);
            }
        });

        prova2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriMovimento.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Accelerometro non calibrato");
                intent.putExtra("TipoSensore", Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
                startActivity(intent);
            }
        });

        prova3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriMovimento.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Gravita'");
                intent.putExtra("TipoSensore", Sensor.TYPE_GRAVITY);
                startActivity(intent);
            }
        });

        prova4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriMovimento.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Giroscopio");
                intent.putExtra("TipoSensore", Sensor.TYPE_GYROSCOPE);
                startActivity(intent);
            }
        });

        prova5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriMovimento.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Giroscopio non calibrato");
                intent.putExtra("TipoSensore", Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
                startActivity(intent);
            }
        });

        prova6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriMovimento.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Accelerazione lineare");
                intent.putExtra("TipoSensore", Sensor.TYPE_LINEAR_ACCELERATION);
                startActivity(intent);
            }
        });

        prova7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriMovimento.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Vettore di rotazione");
                intent.putExtra("TipoSensore", Sensor.TYPE_ROTATION_VECTOR);
                startActivity(intent);
            }
        });

        prova8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaSensoriMovimento.this, GestioneSensori.class);
                intent.putExtra("NomeSensore","Conta passi");
                intent.putExtra("TipoSensore", Sensor.TYPE_STEP_COUNTER);
                startActivity(intent);
            }
        });
    }
}