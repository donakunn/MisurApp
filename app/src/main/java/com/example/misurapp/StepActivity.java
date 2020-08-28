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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misurapp.db.DbManager;
import com.example.misurapp.utility.SaveAndFeedback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class StepActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor sensor;
    private TextView misura;
    private ImageView imageView;
    private static final String sensorUsed="lastStepsRegister";
    private DbManager dbManager = new DbManager(this);
    private int stepsRegister = 0;
    private int stepsShow;
    String [] listItems;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    SharedPreferences lastStepRegister;
    SharedPreferences.Editor editorLastStepRegister;

    SharedPreferences stepDisplay;
    SharedPreferences.Editor editorStepDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        lastStepRegister = getSharedPreferences("reset", MODE_PRIVATE);
        editorLastStepRegister = lastStepRegister.edit();
        stepsRegister = lastStepRegister.getInt("reset",0);

        stepDisplay = getSharedPreferences("stepsDisplay", MODE_PRIVATE);
        editorStepDisplay = stepDisplay.edit();

        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        imageView = findViewById(R.id.img_animazione);
        misura =  findViewById(R.id.misura);


        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(StepActivity.this, R.anim.button_click));

                stepsRegister = lastStepRegister.getInt("reset",0);
                stepsShow = 0;

                editorStepDisplay.putInt("stepsDisplay",0);
                editorStepDisplay.apply();
                setUpText(0);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(StepActivity.this, R.anim.button_click));
                SaveAndFeedback.saveAndMakeToast(dbManager,getApplicationContext(),sensorUsed,(float)stepsShow);


                //feedback
                Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.salvato) , Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 300);
                toast.show();
            }
        });

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

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
    }


    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if((int) event.values[0] == 0){
            stepsRegister = 0;
        }
        stepsShow =  ((int) event.values[0] - stepsRegister) + stepDisplay.getInt("stepsDisplay", 0);

        if (stepsShow % 2 == 0) {
            imageView.setImageResource(R.drawable.contapassi1);
        } else {
            imageView.setImageResource(R.drawable.contapassi2);
        }
        setUpText(stepsShow);

        editorStepDisplay.putInt("stepsDisplay",stepsShow);
        editorStepDisplay.apply();

        editorLastStepRegister.putInt("reset",(int) event.values[0]);
        editorLastStepRegister.apply();

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
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(StepActivity.this);
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
            Intent intent = new Intent(StepActivity.this,BoyscoutDBValuesActivity.class);
            intent.putExtra("sensorName",sensorUsed);
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

    //plurals
    private void setUpText(int nlastStepsRegister) {
        String lastStepsRegisterDetect = getResources().getQuantityString(R.plurals.numberOfSteps, nlastStepsRegister, nlastStepsRegister);
        misura.setText(lastStepsRegisterDetect);
    }
}