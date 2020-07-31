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

public class Fragment_listaMovimento extends Fragment implements View.OnClickListener {

    TableRow row1,row2, row3, row4,row5,row6, row7, row8;
    TextView ok1, ok2,ok3,ok4,ok5,ok6,ok7,ok8;
    TextView nome_sensore1, nome_sensore2, nome_sensore3, nome_sensore4, nome_sensore5, nome_sensore6, nome_sensore7, nome_sensore8;
    private Instrument accelerometro;
    private Instrument accelerometroNonCalibrato;
    private Instrument gravita;
    private Instrument giroscopio;
    private Instrument giroscopioNonCalibrato;
    private Instrument accelerazioneLineare;
    private Instrument vettoreRotazione;
    private Instrument contaPassi;
    private SensorManager manager;

    public Fragment_listaMovimento() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_movimento, container, false);
        //testi e tableRow
        row1 =  view.findViewById(R.id.TableRow1);
        row2 =  view.findViewById(R.id.TableRow2);
        row3 =  view.findViewById(R.id.TableRow3);
        row4 =  view.findViewById(R.id.TableRow4);
        row5 =  view.findViewById(R.id.TableRow5);
        row6 =  view.findViewById(R.id.TableRow6);
        row7 =  view.findViewById(R.id.TableRow7);
        row8 =  view.findViewById(R.id.TableRow8);

        ok1 =  view.findViewById(R.id.Ok1);
        ok2 =  view.findViewById(R.id.ok2);
        ok3 =  view.findViewById(R.id.ok3);
        ok4 =  view.findViewById(R.id.ok4);
        ok5 =  view.findViewById(R.id.ok5);
        ok6 =  view.findViewById(R.id.ok6);
        ok7 =  view.findViewById(R.id.ok7);
        ok8 =  view.findViewById(R.id.ok8);

        nome_sensore1 =  view.findViewById(R.id.Sensore1);
        nome_sensore2 =  view.findViewById(R.id.Sensore2);
        nome_sensore3 =  view.findViewById(R.id.Sensore3);
        nome_sensore4 =  view.findViewById(R.id.Sensore4);
        nome_sensore5 =  view.findViewById(R.id.Sensore5);
        nome_sensore6 =  view.findViewById(R.id.Sensore6);
        nome_sensore7 =  view.findViewById(R.id.Sensore7);
        nome_sensore8 =  view.findViewById(R.id.Sensore8);

        //SensorManager
        manager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        //VERIFICHE CHE I SENSORI SONO SUPPORTATI DAL DISPOSITIVO
        //1.Accelerometro
        if (manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            nome_sensore1.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row1.setEnabled(true);
            accelerometro = new Instrument("Accelerometro",Sensor.TYPE_ACCELEROMETER,
                    getResources().getStringArray(R.array.descrizione_3_valori),
                    "m/s\u00B2",3);
        }else {
            ok1.setVisibility(View.VISIBLE);
            row1.setEnabled(false);
        }
        //2.Accelerometro non calibrato
        if (manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED) != null){
            nome_sensore2.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row2.setEnabled(true);
            accelerometroNonCalibrato= new Instrument("AccelerometroNonCb",Sensor.TYPE_ACCELEROMETER_UNCALIBRATED,
                    getResources().getStringArray(R.array.descrizione_6_valori),
                    "m/s\u00B2",6);
        }else {
            ok2.setVisibility(View.VISIBLE);
            row2.setEnabled(false);
        }
        //3.Gravità
        if (manager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
            nome_sensore3.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row3.setEnabled(true);
            gravita= new Instrument("gravita",Sensor.TYPE_GRAVITY,
                    getResources().getStringArray(R.array.descrizione_3_valori),
                    "m/s\u00B2",3);
        }else {
            ok3.setVisibility(View.VISIBLE);
            row3.setEnabled(false);
        }
        //4.Giroscopio
        if (manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            nome_sensore4.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row4.setEnabled(true);
            giroscopio= new Instrument("giroscopio",Sensor.TYPE_GYROSCOPE,
                    getResources().getStringArray(R.array.descrizione_3_valori),
                    "rad/s",3);
        }else {
            ok4.setVisibility(View.VISIBLE);
            row4.setEnabled(false);
        }
        //5.Giroscopio non calibrato
        if (manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED) != null){
            nome_sensore5.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row5.setEnabled(true);
            giroscopioNonCalibrato= new Instrument("giroscopioNonCb",Sensor.TYPE_GYROSCOPE_UNCALIBRATED,
                    getResources().getStringArray(R.array.descrizione_6_valori),
                    "rad/s",6);
        }else {
            ok5.setVisibility(View.VISIBLE);
            row5.setEnabled(false);
        }
        //6.Accelerazione lineare
        if (manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            nome_sensore6.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row6.setEnabled(true);
            accelerazioneLineare= new Instrument("AccelerazioneNonL",Sensor.TYPE_LINEAR_ACCELERATION,
                    getResources().getStringArray(R.array.descrizione_3_valori),
                    "m/s",3);
        }else {
            ok6.setVisibility(View.VISIBLE);
            row6.setEnabled(false);
        }
        //7.Vettore di rotazione
        if (manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null){
            nome_sensore7.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row7.setEnabled(true);
            vettoreRotazione= new Instrument("VettoreRotaz",Sensor.TYPE_ROTATION_VECTOR,
                    getResources().getStringArray(R.array.descrizione_5_valori),
                    " ",5);
        }else {
            ok7.setVisibility(View.VISIBLE);
            row7.setEnabled(false);
        }
        //8.Conta passi
        if (manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            nome_sensore8.setTypeface(nome_sensore1.getTypeface(), Typeface.BOLD);
            row8.setEnabled(true);
            contaPassi= new Instrument("ContaPassi",Sensor.TYPE_STEP_COUNTER,
                    new String[]{" "},
                    "steps",1);
        }else {
            ok8.setVisibility(View.VISIBLE);
            row8.setEnabled(false);
        }

        //azioni bottoni
        row1.setOnClickListener(this);
        row2.setOnClickListener(this);
        row3.setOnClickListener(this);
        row4.setOnClickListener(this);
        row5.setOnClickListener(this);
        row6.setOnClickListener(this);
        row7.setOnClickListener(this);
        row8.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), GestioneSensori.class);
        switch (v.getId()) {
            case R.id.TableRow1:
                intent.putExtra("TipoSensore", accelerometro);
                startActivity(intent);
                break;
            case R.id.TableRow2:
                intent.putExtra("TipoSensore", accelerometroNonCalibrato);
                startActivity(intent);
                break;
            case R.id.TableRow3:
                intent.putExtra("TipoSensore", gravita);
                startActivity(intent);
                break;
            case R.id.TableRow4:
                intent.putExtra("TipoSensore", giroscopio);
                startActivity(intent);
                break;
            case R.id.TableRow5:
                intent.putExtra("TipoSensore", giroscopioNonCalibrato);
                startActivity(intent);
                break;
            case R.id.TableRow6:
                intent.putExtra("TipoSensore", accelerazioneLineare);
                startActivity(intent);
                break;
            case R.id.TableRow7:
                intent.putExtra("TipoSensore", vettoreRotazione);
                startActivity(intent);
                break;
            case R.id.TableRow8:
                intent.putExtra("TipoSensore", contaPassi);
                startActivity(intent);
                break;
        }
    }
}