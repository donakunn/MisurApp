package com.example.misurapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.utility.SaveAndFeedback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CompassActivity extends MisurAppInstrumentBaseActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView misura;
    private ImageView imageView;

    int mAzimuth; //gradi rispetto al Nord
    //private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        instrumentName = "compass";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        imageView = findViewById(R.id.img_animazione);
        misura = findViewById(R.id.misura);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(CompassActivity.this,
                    R.anim.button_click));
            SaveAndFeedback.saveAndMakeToast(dbManager,getApplicationContext(),
                    instrumentName, (float) mAzimuth);
        });
        start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat,
                    orientation)[0]) + 360) % 360;
        }


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat,
                    orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
        imageView.setRotation(mAzimuth);

        String where = "NO";

        if (mAzimuth >= 350 || mAzimuth <= 10)
            where = "N";
        if (mAzimuth < 350 && mAzimuth > 280)
            where = "NW";
        if (mAzimuth <= 280 && mAzimuth > 260)
            where = "W";
        if (mAzimuth <= 260 && mAzimuth > 190)
            where = "SW";
        if (mAzimuth <= 190 && mAzimuth > 170)
            where = "S";
        if (mAzimuth <= 170 && mAzimuth > 100)
            where = "SE";
        if (mAzimuth <= 100 && mAzimuth > 80)
            where = "E";
        if (mAzimuth <= 80 && mAzimuth > 10)
            where = "NE";


        misura.setText(getString(R.string.compass_textViewContent,mAzimuth,where));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void start() {
        /*verifichiamo se il nostro dispositivo supporta il RotationVector che è l’insieme
        del sensore della Bussola e del Giroscopio.
        In caso affermativo lo instanziamo. In caso negativo controlliamo che il nostro dispositivo
        sia provvisto dell’Accelerometro e della Bussola.
        In caso affermativo instanziamo i due sensori, in caso negativo chiamiamo il metodo
        noSensorAlert() che ci mostra il messaggio di errore.*/
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            haveSensor = mSensorManager.registerListener(this, mAccelerometer,
                    SensorManager.SENSOR_DELAY_UI);
            haveSensor2 = mSensorManager.registerListener(this, mMagnetometer,
                    SensorManager.SENSOR_DELAY_UI);
        } else {
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop() {
        if (haveSensor && haveSensor2) {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        } else {
            if (haveSensor)
                mSensorManager.unregisterListener(this, mRotationV);
        }

    }
}