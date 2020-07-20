package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.os.Bundle;

public class GestioneSensori extends AppCompatActivity  {
    //il DbManager verrà usato probabilmente in un altro fragment che gestirà il salvataggio del database risiedente sempre in questa activity
    /*private DbManager dbManager;//attributo
            dbManager = new DbManager(this);//on create
            dbManager.open();*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestione_sensori);

        Bundle datipassati = getIntent().getExtras();
        int tipo = datipassati.getInt("TipoSensore");

        String units = getUnits(tipo); //unità di misura
        int dim = getNumValori(tipo); //dimensione array per la creazione del layout
        String [] descrizione = getDescrizioneValori(tipo); //stringhe finalizzate alla descrizione dei valori

        Bundle bundle = new Bundle();//serve per passare i valori nel vettore nel fragment per visualizzarli

        bundle.putInt("dim",dim);
        bundle.putStringArray("descrizione",descrizione);
        bundle.putString("unitàMisura", units);
        bundle.putInt("tipo",tipo);
        Fragment_visualizzaValori fragInfo = new Fragment_visualizzaValori();
        fragInfo.setArguments(bundle);

        //richiamo del fragment
        getSupportFragmentManager().beginTransaction().add(R.id.Fragment_visualizzaValori, fragInfo).commit();

    }


    private String getUnits(int type) {
        String units;
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                units = "m/s\u00B2";
                break;
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                units = "m/s\u00B2";
                break;
            case Sensor.TYPE_GRAVITY:
                units = "m/s\u00B2";
                break;
            case Sensor.TYPE_GYROSCOPE:
                units = "rad/s";
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                units = "rad/s";
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                units = "m/s";
                break;
            case Sensor.TYPE_STEP_COUNTER:
                units = "steps";
                break;
            //Sensori posizione
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                units = "μT";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                units = "μT";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                units = "μT";
                break;
            case Sensor.TYPE_PROXIMITY:
                units = "cm";
                break;
            //sensori ambiente
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                units = "°C";
                break;
            case Sensor.TYPE_LIGHT:
                units = "lx";
                break;
            case Sensor.TYPE_PRESSURE:
                units = "hPa";
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                units = " %";
                break;
            default:
                units = " ";
                break;
        }
        return units;
    }

    private int getNumValori(int type) {
        int numValori = 0;

        switch (type) {
            //sensori movimento
            case Sensor.TYPE_ACCELEROMETER:
                numValori = 3;
                break;
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                numValori = 6;
                break;
            case Sensor.TYPE_GRAVITY:
                numValori = 3;
                break;
            case Sensor.TYPE_GYROSCOPE:
                numValori = 3;
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                numValori = 6;
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                numValori = 3;
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                numValori = 5;
                break;
            case Sensor.TYPE_STEP_COUNTER:
                numValori = 1;
                break;
            //Sensori posizione
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                numValori = 4;
                break;
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                numValori = 3;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                numValori = 3;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                numValori = 6;
                break;
            case Sensor.TYPE_PROXIMITY:
                numValori = 1;
                break;
            //sensori ambiente
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                numValori = 1;
                break;
            case Sensor.TYPE_LIGHT: //TOO CHECK
                numValori = 1;
                break;
            case Sensor.TYPE_PRESSURE:
                numValori = 1;
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                numValori = 1;
                break;

        }
        return numValori;
    }

    private String[] getDescrizioneValori(int type){
        int numValori = getNumValori(type);
        String[] descrizione = new String[numValori];
        switch (type) {
            //sensori movimento
            case Sensor.TYPE_ACCELEROMETER:
                descrizione = getResources().getStringArray(R.array.descrizione_3_valori);
                break;
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                descrizione = getResources().getStringArray(R.array.descrizione_6_valori);
                break;
            case Sensor.TYPE_GRAVITY:
                descrizione = getResources().getStringArray(R.array.descrizione_3_valori);
                break;
            case Sensor.TYPE_GYROSCOPE:
                descrizione = getResources().getStringArray(R.array.descrizione_3_valori);
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                descrizione = getResources().getStringArray(R.array.descrizione_6_valori);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                descrizione = getResources().getStringArray(R.array.descrizione_3_valori);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                descrizione = getResources().getStringArray(R.array.descrizione_5_valori);
                break;
            //Sensori posizione
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                descrizione = getResources().getStringArray(R.array.descrizione_4_valori);
                break;
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                descrizione = getResources().getStringArray(R.array.descrizione_3_valori);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                descrizione = getResources().getStringArray(R.array.descrizione_3_valori);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                descrizione = getResources().getStringArray(R.array.descrizione_6_valori);
                break;
            default: //include tutti i sensori con 1 solo valore che non hanno descrizione
                descrizione[0] = " ";
                break;
        }
        return descrizione;
    }
}