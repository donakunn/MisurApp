package com.example.misurapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;

public class ListaStrumentiActivity extends AppCompatActivity {
    String [] listItems;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_strumenti);
        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();

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

        //luminosità
        if (manager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            textLuminosita.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        }else{
            luminositaNonSupportato.setVisibility(View.VISIBLE);
            luminosita.setEnabled(false);
        }

        //temperatura
        if (manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            textTermometro.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        }else{
            termometroNonSupportato.setVisibility(View.VISIBLE);
            termometro.setEnabled(false);
        }

        //umidità
        if (manager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            textUmidita.setTypeface(textTermometro.getTypeface(), Typeface.BOLD);
        }else{
            umiditaNonSupportato.setVisibility(View.VISIBLE);
            umidita.setEnabled(false);
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

        umidita.setOnClickListener(new View.OnClickListener() {
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


    private void setAppLocale(String localCode){
        Resources res = getResources();
        DisplayMetrics dm =res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            conf.setLocale(new Locale(localCode.toLowerCase()));
        }else{
            conf.locale = new Locale(localCode.toLowerCase());
        }
        res.updateConfiguration(conf, dm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Gestisci i clic sugli elementi della barra delle azioni qui.
        La barra delle azioni gestirà automaticamente i clic sul pulsante Home / Up button,
        a condizione che specifichi un'attività genitore in AndroidManifest.xml.*/
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cambio_lingua) {
            listItems = new String[] {getResources().getString(R.string.lingua_inglese), getResources().getString(R.string.lingua_spagnola), getResources().getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ListaStrumentiActivity.this);
            mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = getIntent();


                    switch (which){

                        case 0:
                            setAppLocale("en");
                            finish();
                            startActivity(intent);
                            editor.putBoolean("flagMain", true);
                            editor.apply();
                            break;

                        case 1:
                            setAppLocale("es");
                            finish();
                            startActivity(intent);
                            editor.putBoolean("flagMain", true);
                            editor.apply();
                            break;

                        case 2:
                            setAppLocale("it");
                            finish();
                            startActivity(intent);
                            editor.putBoolean("flagMain", true);
                            editor.apply();
                            break;
                    }

                }
            });

            mBuilder.setNeutralButton(getResources().getString(R.string.dialog_annulla), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
            return true;
        }

        if (id == R.id.action_backup) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        if(prefs.getBoolean("flagStrumenti", false)){
            editor.putBoolean("flagStrumenti", false);
            editor.apply();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

}