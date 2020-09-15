package com.example.misurapp.activities.instrumentActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.R;
import com.example.misurapp.activities.MisurAppInstrumentBaseActivity;
import com.example.misurapp.utility.SaveAndFeedback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * This class is about defining the layout, showing the value read by the sensors,
 * showing an animation and allowing you to save the read value for the compass instrument
 */
public class CompassActivity extends MisurAppInstrumentBaseActivity implements SensorEventListener {
    /**
     * Debug Tag
     */
    private final String TAG = this.getClass().toString();
    /**
     * SensorManager object to manage access to device's sensors.
     */
    private SensorManager mSensorManager;
    /**
     * TextView to show current values
     */
    private TextView measure;
    /**
     *  View containing an animation relative to the instrument activity
     */
    private ImageView imageView;
    /**
     * degrees compared to the North
     */
    private int mAzimuth;
    /**
     * object representing sensors used by compass instrument
     */
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    /**
     * Boolean indicating whether the device has the first sensor
     */
    private boolean haveSensor = false;
    /**
     * Boolean indicating whether the device has the second sensor
     */
    private boolean haveSensor2 = false;
    /**
     * rotation vector registered values
     */
    private float[] rMat = new float[9];
    /**
     * orientation registered values
     */
    private float[] orientation = new float[3];
    /**
     * accelerometer registered values
     */
    private float[] mLastAccelerometer = new float[3];
    /**
     * magnetometer registered values
     */
    private float[] mLastMagnetometer = new float[3];
    /**
     * Boolean indicating whether the accelerometer is set
     */
    private boolean mLastAccelerometerSet = false;
    /**
     * Boolean indicating whether the magnetometer is set
     */
    private boolean mLastMagnetometerSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_compass);

        instrumentName = "compass";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        imageView = findViewById(R.id.img_animazione);
        measure = findViewById(R.id.misura);

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
        Log.d(TAG,"onResume");
        start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        stop();
    }

    /**
     * Refresh animation based on the value read by the sensor
     * @param event Sensor event object wich holds information such as the sensor's type,
     * the time-stamp, accuracy and of course the sensor's SensorEvent#values
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG,"onSensorChanged");
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.abs((Math.toDegrees(SensorManager.getOrientation(rMat,
                    orientation)[0]) - 360))) % 360;
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
            mAzimuth = (int) (Math.abs((Math.toDegrees(SensorManager.getOrientation(rMat,
                    orientation)[0]) - 360))) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
        imageView.setRotation(mAzimuth * -1);

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


        measure.setText(getString(R.string.compass_textViewContent,mAzimuth,where));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * This method check if device supports the RotationVector which is the set of compass and
     * gyroscope sensor. If so, we will establish it. If not, we check that our device
     * equipped with the accelerometer and compass. If so, we set up the two sensors,
     * if not we call the method noSensorAlert() showing us the error message.
     */
    public void start() {
        Log.d(TAG,"Starting reading values from sensors");
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

    /**
     * This method unregister listener for used sensors.
     */
    public void stop() {
        Log.d(TAG,"Stopping sensors read");
        if (haveSensor && haveSensor2) {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        } else {
            if (haveSensor)
                mSensorManager.unregisterListener(this, mRotationV);
        }

    }
}