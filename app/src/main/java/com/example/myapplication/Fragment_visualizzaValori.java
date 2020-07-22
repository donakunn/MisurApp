package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class Fragment_visualizzaValori extends Fragment implements SensorEventListener{

    private SensorManager manager;
    private Sensor s;
    TextView[] valori;
    private float[] values;
    private int dim;


    //costruttore
    public Fragment_visualizzaValori() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visualizza_valori, container, false);

        //il fragment riceve i dati passati da GestioneSensori
        int tipo = getArguments().getInt("tipo");
        this.dim =getArguments().getInt("dim");
        String units = getArguments().getString("unitàMisura");
        String [] descrizione = getArguments().getStringArray("descrizione");

        //Sensor Manager
        manager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        s = manager.getDefaultSensor(tipo);

        //creazione del layout
        TableLayout layout = view.findViewById(R.id.Table);
        TableRow tableRow;
        TableRow.LayoutParams tableRowPar = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1);
        tableRowPar.setMargins(0,30,0,30);
        LinearLayout linearLayout;
        TextView titolo;
        valori = new TextView[dim];
        TextView unit;

        //nRighe rappresenta il numero delle TableRow
        int nRighe;
        if (dim > 3){
            nRighe = 2;
        }else{
            nRighe=1;
        }

        int indice =0;//indice che tiene traccia della posizione del vettore descrizione e valori

        //inserimenti dei Layout
        for (int i = 0; i < nRighe; i++) {
            tableRow = new TableRow(getActivity());

            for (int j = 0; j < 3; j++) {
                if(indice!=dim) {
                    linearLayout = new LinearLayout(getActivity());
                    linearLayout.setLayoutParams(tableRowPar);
                    linearLayout.setGravity(1);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    titolo = new TextView(getActivity());
                    titolo.setText(descrizione[indice]);
                    titolo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                    titolo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    titolo.setPadding(5, 5, 5, 5);
                    titolo.setTextSize(12);
                    titolo.setTextColor(Color.BLACK);

                    valori[indice] = new TextView(getActivity());

                    unit = new TextView(getActivity());
                    unit.setText(units);
                    unit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                    unit.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    unit.setPadding(5, 5, 5, 5);
                    unit.setTextSize(14);
                    unit.setTextColor(Color.BLACK);

                    linearLayout.addView(titolo);
                    linearLayout.addView(valori[indice]);
                    linearLayout.addView(unit);
                    indice++;

                    tableRow.addView(linearLayout);
                }
            }
            layout.addView(tableRow);
        }
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        values = event.values;

        for(int i = 0; i < dim; i++){
            valori[i].setText(String.format("%.4f", values[i]));
            valori[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
            valori[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            valori[i].setPadding(5, 5, 5, 5);
            valori[i].setTextSize(18);
            valori[i].setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onResume() {
        super.onResume();
        manager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }

    //ritorna l'array per salvare i dati
    public float[] getValues(){
        return values;
    }


}