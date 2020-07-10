package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GestioneSensori extends Activity implements SensorEventListener, View.OnClickListener {
    private SensorManager mSensorManager;
    private Sensor sensore;
    private float[] valori;
    Button salvataggio;
    private DbManager dbManager;
    //private DbManager dbHelper = new DbManager (this);
    TextView nomeSensor, valore0, valore1, valore2, valore3, valore4, valore5;
    String nomeSensore;
    int tipo;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometro);

        Bundle datipassati = getIntent().getExtras();
        nomeSensore = datipassati.getString("NomeSensore");
        tipo = datipassati.getInt("TipoSensore");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensore = mSensorManager.getDefaultSensor(tipo);

        nomeSensor = (TextView) findViewById(R.id.NomeSensore);
        nomeSensor.setText(nomeSensore);

        valore0 = (TextView) findViewById(R.id.ValoreX);
        valore1 = (TextView) findViewById(R.id.ValoreY);
        valore2 = (TextView) findViewById(R.id.ValoreZ);
        valore3 = (TextView) findViewById(R.id.ValoreX2);
        valore4 = (TextView) findViewById(R.id.ValoreY2);
        valore5 = (TextView) findViewById(R.id.ValoreZ2);
        salvataggio = (Button) findViewById(R.id.Salva);

        dbManager = new DbManager(this);
        dbManager.open();
        salvataggio.setOnClickListener(this);


    }
    @Override
    public final void onAccuracyChanged(Sensor arg0, int arg1) { //Sensor sensor, int accuracy
// Do something here if sensor accuracy changes.

    }
    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Do something with this sensor value.
    // Many sensors return 3 values, one for each axis.
        valori = event.values;//array che contiene i valori del sensore di tipo x

        switch(nomeSensore) {
            //Sensori Movimento
            case "Accelerometro":
                    //modifica del valore delle textView
                valore0.setText("Valore X: "+valori[0] + " m/s^2");
                valore1.setText("Valore Y: "+valori[1]+ " m/s^2");
                valore2.setText("Valore Z: "+valori[2]+ " m/s^2");
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
            case "Accelerometro non calibrato":
                valore0.setText("Valore X (senza alcuna compensazione di polarizzazione): "+valori[0]+ " m/s^2");
                valore1.setText("Valore Y (senza alcuna compensazione di polarizzazione): "+valori[1]+ " m/s^2");
                valore2.setText("Valore Z (senza alcuna compensazione di polarizzazione): "+valori[2]+ " m/s^2");
                valore3.setText("Valore X (con compensazione della polarizzazione stimata): "+valori[3]+ " m/s^2");
                valore4.setText("Valore Y (con compensazione della polarizzazione stimata): "+valori[4]+ " m/s^2");
                valore5.setText("Valore Z (con compensazione della polarizzazione stimata): "+valori[5]+ " m/s^2");
                break;
            case "Gravita'":
                valore0.setText("Valore X: "+valori[0]+ " m/s^2");
                valore1.setText("Valore Y: "+valori[1]+ " m/s^2");
                valore2.setText("Valore Z: "+valori[2]+ " m/s^2");
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
            case "Giroscopio":
                valore0.setText("Valore X: "+valori[0] + " rad/s");
                valore1.setText("Valore Y: "+valori[1]+ " rad/s");
                valore2.setText("Valore Z: "+valori[2]+ " rad/s");
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
            case "Giroscopio non calibrato":
                valore0.setText("Valore X (senza compensazione della deriva): "+valori[0]+ " rad/s");
                valore1.setText("Valore Y (senza compensazione della deriva): "+valori[1]+ " rad/s");
                valore2.setText("Valore Z (senza compensazione della deriva): "+valori[2]+ " rad/s");
                valore3.setText("Valore X (deriva stimata): "+valori[3]);
                valore4.setText("Valore Y (deriva stimata): "+valori[4]);
                valore5.setText("Valore Z (deriva stimata): "+valori[5]);
                break;
            case "Accelerazione lineare":
                valore0.setText("Valore X: "+valori[0]+ " m/s^2");
                valore1.setText("Valore Y: "+valori[1]+ " m/s^2");
                valore2.setText("Valore Z: "+valori[2]+ " m/s^2");
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
            case "Vettore di rotazione":
                valore0.setText("Valore X: "+valori[0]);
                valore1.setText("Valore Y: "+valori[1]);
                valore2.setText("Valore Z: "+valori[2]);
                valore3.setText("Componente scalare del vettore di rotazione: "+valori[3]);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
            case "Conta passi":
                valore0.setText("Numero di passi: "+valori[0]);
                valore1.setVisibility(View.INVISIBLE);
                valore2.setVisibility(View.INVISIBLE);
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
                //Sensori Posizione
            case "Vettore di Rotazione di gioco":
                valore0.setText("Valore X: "+valori[0]);
                valore1.setText("Valore Y: "+valori[1]);
                valore2.setText("Valore Z: "+valori[2]);
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
            case "Vettore di Rotazione Geomagnetico":
                valore0.setText("Valore X: "+valori[0]);
                valore1.setText("Valore Y: "+valori[1]);
                valore2.setText("Valore Z: "+valori[2]);
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
            case "Campo Magnetico":
                valore0.setText("Valore X: "+valori[0] + " microtesla");
                valore1.setText("Valore Y: "+valori[1] + " microtesla");
                valore2.setText("Valore Z: "+valori[2] + " microtesla");
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
            case "Campo Magnetico non Calibrato":
                valore0.setText("Valore X (senza calibrazione del ferro duro): "+valori[0] + " microtesla");
                valore1.setText("Valore Y (senza calibrazione del ferro duro): "+valori[1] + " microtesla");
                valore2.setText("Valore Z (senza calibrazione del ferro duro): "+valori[2] + " microtesla");
                valore3.setText("Valore X (stima della polarizzazione del ferro): "+valori[3] + " microtesla");
                valore4.setText("Valore Y (stima della polarizzazione del ferro): "+valori[4] + " microtesla");
                valore5.setText("Valore Z (stima della polarizzazione del ferro): "+valori[5] + " microtesla");
                break;
            case "Prossimità":
                valore0.setText("Distanza dall'oggetto: "+valori[0] + " cm");
                valore1.setVisibility(View.INVISIBLE);
                valore2.setVisibility(View.INVISIBLE);
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
                //Sensori Ambiente
            case "Temperatura Ambiente":
                valore0.setText(" "+valori[0] + " °C");
                valore1.setVisibility(View.INVISIBLE);
                valore2.setVisibility(View.INVISIBLE);
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
            case "Illuminazione":
                valore0.setText(" "+valori[0]+" lx");
                valore1.setVisibility(View.INVISIBLE);
                valore2.setVisibility(View.INVISIBLE);
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
            case "Pressione":
                valore0.setText(" "+valori[0]);
                valore1.setVisibility(View.INVISIBLE);
                valore2.setVisibility(View.INVISIBLE);
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
            case "Umidità Relativa":
                valore0.setText(" "+valori[0] + " %");
                valore1.setVisibility(View.INVISIBLE);
                valore2.setVisibility(View.INVISIBLE);
                valore3.setVisibility(View.INVISIBLE);
                valore4.setVisibility(View.INVISIBLE);
                valore5.setVisibility(View.INVISIBLE);
                break;
        }



    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, sensore, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onClick(View v) {
        dbManager.createTabella(nomeSensore, valori);
        //dbManager.close(); DA SPOSTARE
    }
}