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

public class Fragment_listaPosizione extends Fragment implements View.OnClickListener {

    TableRow row1,row2, row3, row4,row5;
    TextView ok1, ok2,ok3,ok4,ok5;
    TextView nome_sensore1, nome_sensore2, nome_sensore3, nome_sensore4, nome_sensore5;
    private SensorManager manager;
    private Instrument vettoreRotazioneGioco;
    private Instrument vettoreRotazioneGeomagnetico;
    private Instrument campoMagnetico;
    private Instrument campoMagneticoNonCalibrato;
    private Instrument prossimita;

    public Fragment_listaPosizione() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_posizione, container, false);

        //testi e tableRow
        row1 =  view.findViewById(R.id.TableRow1);
        row2 =  view.findViewById(R.id.TableRow2);
        row3 =  view.findViewById(R.id.TableRow3);
        row4 =  view.findViewById(R.id.TableRow4);
        row5 =  view.findViewById(R.id.TableRow5);

        ok1 =  view.findViewById(R.id.Ok1);
        ok2 =  view.findViewById(R.id.ok2);
        ok3 =  view.findViewById(R.id.ok3);
        ok4 =  view.findViewById(R.id.ok4);
        ok5 =  view.findViewById(R.id.ok5);

        nome_sensore1 =  view.findViewById(R.id.Sensore1);
        nome_sensore2 =  view.findViewById(R.id.Sensore2);
        nome_sensore3 =  view.findViewById(R.id.Sensore3);
        nome_sensore4 =  view.findViewById(R.id.Sensore4);
        nome_sensore5 =  view.findViewById(R.id.Sensore5);

        //SensorManager
        manager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        //VERIFICHE CHE I SENSORI SONO SUPPORTATI DAL DISPOSITIVO
        //1.Vettore di Rotazione di gioco
        if (manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null){
            nome_sensore1.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row1.setEnabled(true);
            vettoreRotazioneGioco = new Instrument("VettRotGioco",Sensor.TYPE_GAME_ROTATION_VECTOR,
                    getResources().getStringArray(R.array.descrizione_4_valori),
                    " ",4);
        }else {
            ok1.setVisibility(View.VISIBLE);
            row1.setEnabled(false);
        }
        //2.Vettore di Rotazione Geomagnetico
        if (manager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) != null){
            nome_sensore2.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row2.setEnabled(true);
            vettoreRotazioneGeomagnetico = new Instrument("VettRotGeom",Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
                    getResources().getStringArray(R.array.descrizione_3_valori),
                    "μT",3);
        }else {
            ok2.setVisibility(View.VISIBLE);
            row2.setEnabled(false);
        }
        //3.Campo Magnetico
        if (manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            nome_sensore3.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row3.setEnabled(true);
            campoMagnetico = new Instrument("CampoMagnetico",Sensor.TYPE_MAGNETIC_FIELD,
                    getResources().getStringArray(R.array.descrizione_3_valori),
                    "μT",3);
        }else {
            ok3.setVisibility(View.VISIBLE);
            row3.setEnabled(false);
        }
        //4.Campo Magnetico non Calibrato
        if (manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) != null){
            nome_sensore4.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row4.setEnabled(true);
            campoMagneticoNonCalibrato = new Instrument("CampoMagneticoNonCB",Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED,
                    getResources().getStringArray(R.array.descrizione_6_valori),
                    "μT",6);
        }else {
            ok4.setVisibility(View.VISIBLE);
            row4.setEnabled(false);
        }
        //5.Prossimità
        if (manager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            nome_sensore5.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row5.setEnabled(true);
            prossimita = new Instrument("Prossimita",Sensor.TYPE_PROXIMITY,
                    new String[]{" "},
                    "cm",1);
        }else {
            ok5.setVisibility(View.VISIBLE);
            row5.setEnabled(false);
        }
        //azioni bottoni
        row1.setOnClickListener(this);
        row2.setOnClickListener(this);
        row3.setOnClickListener(this);
        row4.setOnClickListener(this);
        row5.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), GestioneSensori.class);
        switch (v.getId()) {
            case R.id.TableRow1:
                intent.putExtra("TipoSensore", vettoreRotazioneGioco);
                startActivity(intent);
                break;
            case R.id.TableRow2:
                intent.putExtra("TipoSensore", vettoreRotazioneGeomagnetico);
                startActivity(intent);
                break;
            case R.id.TableRow3:
                intent.putExtra("TipoSensore", campoMagnetico);
                startActivity(intent);
                break;
            case R.id.TableRow4:
                intent.putExtra("TipoSensore", campoMagneticoNonCalibrato);
                startActivity(intent);
                break;
            case R.id.TableRow5:
                intent.putExtra("TipoSensore", prossimita);
                startActivity(intent);
                break;
        }
    }
}