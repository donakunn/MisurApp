package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class GestioneSensori extends AppCompatActivity  {


    /*private DbManager dbManager;//attributo
            dbManager = new DbManager(this);//on create
            dbManager.open();*/

    ImageButton salva;
    private float[] valoriSalvati; //valori da mettere nel db

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestione_sensori);

        Bundle datipassati = getIntent().getExtras();
        Sensore sensoreDaMostrare = datipassati.getParcelable("TipoSensore");

        /*String units = getUnits(tipo); //unità di misura
        int dim = getNumValori(tipo); //dimensione array per la creazione del layout
        String [] descrizione = getDescrizioneValori(tipo); //stringhe finalizzate alla descrizione dei valori
        */
        Bundle bundle = new Bundle();//serve per passare i valori nel vettore nel fragment per visualizzarli

        bundle.putInt("dim",sensoreDaMostrare.getDimensione());
        bundle.putStringArray("descrizione",sensoreDaMostrare.getDescrizione());
        bundle.putString("unitàMisura", sensoreDaMostrare.getUnitàDiMisura());
        bundle.putInt("tipo",sensoreDaMostrare.getTipo());
        final Fragment_visualizzaValori visualizzaValori = new Fragment_visualizzaValori();
        visualizzaValori.setArguments(bundle);

        //richiamo dei fragment
        getSupportFragmentManager().beginTransaction().add(R.id.Fragment_visualizzaValori, visualizzaValori).commit();

        Fragment_lista_salvataggi listaSalvataggi = new Fragment_lista_salvataggi();
        getSupportFragmentManager().beginTransaction().add(R.id.Fragment_lista_salvataggi, listaSalvataggi).commit();

        //bottone salva
        salva =  (ImageButton) findViewById(R.id.Salva);
        salva.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                valoriSalvati = visualizzaValori.getValues();//valoriSalvati è l'array da aggiungere al db


                //stampa per test da cancellare
                for(int i=0; i<valoriSalvati.length;i++){
                    System.out.println(valoriSalvati[i]);
                }
            }
        });
        //qui va il resto del codice che aggiunge l'array al db...
        //fragment_lista_salvataggi si occuperà di leggere le query e visualizzarle
        //ho dato solo una bozza di come dovrebbe visualizzarli in quanto le righe verranno create e aggiunte dinamicamente

    }


    /*private String getUnits(int type) {
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
            case Sensor.TYPE_LIGHT:
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
    }*/
}