package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class Fragment_listaAmbiente extends Fragment implements View.OnClickListener{

    TableRow row1,row2, row3, row4;
    TextView ok1, ok2,ok3,ok4;
    TextView nome_sensore1, nome_sensore2, nome_sensore3, nome_sensore4;
    private SensorManager manager;
    private Sensore temperaturaAmbiente;
    private Sensore luminosita;
    private Sensore pressione;
    private Sensore umiditaRelativa;


    public Fragment_listaAmbiente() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_ambiente, container, false);

        //testi e tableRow
        row1 =  view.findViewById(R.id.TableRow1);
        row2 =  view.findViewById(R.id.TableRow2);
        row3 =  view.findViewById(R.id.TableRow3);
        row4 =  view.findViewById(R.id.TableRow4);


        ok1 =  view.findViewById(R.id.Ok1);
        ok2 =  view.findViewById(R.id.ok2);
        ok3 =  view.findViewById(R.id.ok3);
        ok4 =  view.findViewById(R.id.ok4);

        nome_sensore1 =  view.findViewById(R.id.Sensore1);
        nome_sensore2 =  view.findViewById(R.id.Sensore2);
        nome_sensore3 =  view.findViewById(R.id.Sensore3);
        nome_sensore4 =  view.findViewById(R.id.Sensore4);


        //SensorManager
        manager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        //VERIFICHE CHE I SENSORI SONO SUPPORTATI DAL DISPOSITIVO
        //1.Temperatura Ambiente
        if (manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            nome_sensore1.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row1.setEnabled(true);
            temperaturaAmbiente = new Sensore(Sensor.TYPE_AMBIENT_TEMPERATURE,
                    new String[]{" "},
                    "°C",1);
        }else {
            ok1.setVisibility(View.VISIBLE);
            row1.setEnabled(false);
        }

        //2.Illuminazione
        if (manager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            nome_sensore2.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row2.setEnabled(true);
            luminosita = new Sensore(Sensor.TYPE_LIGHT,
                    new String[]{" "},
                    "lx",1);
        }else {
            ok2.setVisibility(View.VISIBLE);
            row2.setEnabled(false);
        }

        //3.Pressione
        if (manager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            nome_sensore3.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row3.setEnabled(true);
            pressione = new Sensore(Sensor.TYPE_PRESSURE,
                    new String[]{" "},
                    "hPa",1);
        }else {
            ok3.setVisibility(View.VISIBLE);
            row3.setEnabled(false);
        }

        //4.Umidità Relativa
        if (manager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null){
            nome_sensore4.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row4.setEnabled(true);
            umiditaRelativa = new Sensore(Sensor.TYPE_RELATIVE_HUMIDITY,
                    new String[]{" "},
                    " %",1);
        }else {
            ok4.setVisibility(View.VISIBLE);
            row4.setEnabled(false);
        }

        //azioni bottoni
        row1.setOnClickListener(this);
        row2.setOnClickListener(this);
        row3.setOnClickListener(this);
        row4.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), GestioneSensori.class);
        switch (v.getId()) {
            case R.id.TableRow1:
                intent.putExtra("TipoSensore", temperaturaAmbiente);
                startActivity(intent);
                break;
            case R.id.TableRow2:
                intent.putExtra("TipoSensore", luminosita);
                startActivity(intent);
                break;
            case R.id.TableRow3:
                intent.putExtra("TipoSensore", pressione);
                startActivity(intent);
                break;
            case R.id.TableRow4:
                intent.putExtra("TipoSensore", umiditaRelativa);
                startActivity(intent);
                break;
        }
    }
}