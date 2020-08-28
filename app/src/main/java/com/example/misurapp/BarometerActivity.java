package com.example.misurapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.misurapp.db.DbManager;
import com.example.misurapp.utility.SaveAndFeedback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class BarometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private ImageView imageView;
    private float valore;
    private TextView misura;
    private float angle;
    private static final String instrumentName = "barometer";
    private DbManager dbManager = new DbManager(this);
    String [] listItems;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barometer);
        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        imageView = (ImageView) findViewById(R.id.img_animazione);
        misura = (TextView) findViewById(R.id.misura);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(BarometerActivity.this, R.anim.button_click));
                SaveAndFeedback.saveAndMakeToast(dbManager,getApplicationContext(), instrumentName,valore);
            }
        });

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        if(prefs.getBoolean("flagStrumento", false)){
            editor.putBoolean("flagStrumento", false);
            editor.apply();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        valore = event.values[0];

        if (valore < 970 || valore > 1050) {
            imageView.setRotation(0);
        } else {
            angle = (((valore - 1010) * 360) / 80);
            imageView.setRotation((int) angle);
        }
        misura.setText(valore + " hPa");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(BarometerActivity.this);
            mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = getIntent();

                    switch (which){

                        case 0:
                            setAppLocale("en");
                            finish();
                            startActivity(intent);
                            editor.putBoolean("flagStrumenti", true);
                            editor.putBoolean("flagMain", true);
                            editor.apply();
                            break;

                        case 1:
                            setAppLocale("es");
                            finish();
                            startActivity(intent);
                            editor.putBoolean("flagStrumenti", true);
                            editor.putBoolean("flagMain", true);
                            editor.apply();
                            break;

                        case 2:
                            setAppLocale("it");
                            finish();
                            startActivity(intent);
                            editor.putBoolean("flagStrumenti", true);
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

        if (id == R.id.action_archivio) {
            Intent intent = new Intent(BarometerActivity.this,BoyscoutDBValuesActivity.class);
            intent.putExtra("sensorName", instrumentName);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem condividi = menu.findItem(R.id.action_archivio);
        condividi.setVisible(true);
        return true;
    }
}