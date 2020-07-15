package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView categorie;
    Button movimento, posizione, ambiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categorie = (TextView) findViewById(R.id.testo);
        movimento = (Button)findViewById(R.id.ListaMovimento);
        posizione = (Button)findViewById(R.id.ListaPosizione);
        ambiente = (Button)findViewById(R.id.ListaAmbiente);

        movimento.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListaSensoriMovimento.class);
                startActivity(intent);
            }
        });

        posizione.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListaSensoriPosizione.class);
                startActivity(intent);
            }
        });

        ambiente.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListaSensoriAmbiente.class);
                startActivity(intent);
            }
        });
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);


    }
}