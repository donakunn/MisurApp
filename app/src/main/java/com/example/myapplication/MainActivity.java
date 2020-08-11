package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button client = findViewById(R.id.Boyscout);
        Button server = (Button)findViewById(R.id.Caposcout);


        client.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_click));
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                //Intent intent = new Intent(MainActivity.this,ListaStrumentiActivity.class);
                //startActivity(intent);
            }
        });

        server.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_click));
                Intent intent = new Intent(MainActivity.this,DatabaseCaposcout.class);
                startActivity(intent);
            }
        });

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }//fine onCreate();
}