


/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.misurapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.misurapp.BluetoothConnection.BluetoothClient;
import com.example.misurapp.BluetoothConnection.BluetoothConnectionService;
import com.example.misurapp.BluetoothConnection.Constants;
import com.example.misurapp.db.DbManager;
import com.example.misurapp.db.RecordsWithEmailAndInstrument;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Set;


/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */

public class BluetoothConnectionActivity extends MisurAppBaseActivity {
    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;

    private String instrumentName;
    private DbManager dbManager = new DbManager(this);

    private ListView newDevicesListView;

    /**
     * Tag for Log
     */
    private static final String TAG = "DeviceListActivity";


    /**
     * Member fields
     */
    private BluetoothAdapter mBtAdapter;

    /**
     * Newly discovered devices
     */
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    /**
     * Button to perform device scan.
     */
    Button scanButton;


    /**
     * Member object for the connection services
     */

    private BluetoothClient btConnectionHandler = null;

    /**
     * Boolean that indicates if data is already set to send
     */
    boolean dataReady = false;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device_list);

        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();

        IntentFilter bluetoothStateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, bluetoothStateFilter);

        instrumentName = Objects.requireNonNull
                (getIntent().getExtras()).getString("sensorName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBtAdapter == null) {
            Toast.makeText(this, R.string.bluetoothNotAvailable, Toast.LENGTH_LONG).show();
            BluetoothConnectionActivity.this.finish();
        }

        // Initialize the button to perform device discovery
        scanButton = findViewById(R.id.button_scan);
        scanButton.setOnClickListener(v -> {
            doDiscovery();
            v.setVisibility(View.GONE);
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder dlgAlert = new AlertDialog.Builder
                    (BluetoothConnectionActivity.this);
            dlgAlert.setMessage(getString(R.string.position_permission_needed));
            dlgAlert.setTitle("MisurApp");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("Ok",
                    (dialog, which) -> ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            1));
            dlgAlert.create().show();
        }
        else {
            initializeArrayAdapters();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                
                initializeArrayAdapters();
            } else {
                final AlertDialog.Builder dlgAlert = new AlertDialog.Builder
                        (BluetoothConnectionActivity.this);
                dlgAlert.setMessage(getString(R.string.position_permission_not_given));
                dlgAlert.setTitle("MisurApp");
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton("Ok",
                        (dialog, which) -> {
                            finish();
                        });
                dlgAlert.create().show();
            }
        }
    }

    private void initializeArrayAdapters() {
        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);

        // Find and set up the ListView for newly discovered devices
        newDevicesListView = findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }

            //add listener only if there are paired devices
            pairedListView.setOnItemClickListener(mDeviceClickListener);

        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);

            pairedListView.setOnItemClickListener(null);
        }

        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (btConnectionHandler == null) {
            btConnectionHandler = new BluetoothClient(getClientHandler());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage(getResources().getString(R.string.bluetoothNeeded));
            dlgAlert.setTitle("MisurApp");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("Ok",
                    (dialog, which) -> BluetoothConnectionActivity.this.finish());
            dlgAlert.create().show();
        }
    }

    @SuppressLint("HandlerLeak")
    private class ClientHandler extends Handler {
        //Using a weak reference means you won't prevent garbage collection
        private final WeakReference<BluetoothConnectionActivity> clientWeakReference;

        public ClientHandler(BluetoothConnectionActivity clientIstance) {
            clientWeakReference = new WeakReference<>(clientIstance);
        }

        @Override
        public void handleMessage(Message msg) {
            BluetoothConnectionActivity handler = clientWeakReference.get();
            if (handler != null) {
                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothConnectionService.STATE_CONNECTED:
                                setTitle(getApplicationContext().getString(R.string.connected));
                                break;
                            case BluetoothConnectionService.STATE_CONNECTING:
                                setTitle(getApplicationContext().getString(R.string.connecting));
                                break;
                            case BluetoothConnectionService.STATE_NONE:
                                setTitle(getApplicationContext().getString(R.string.not_connected));
                                break;
                        }
                        break;
                    case Constants.MESSAGE_TOAST:
                        if (Objects.equals(msg.getData().getString(Constants.TOAST),
                                Constants.CONNECTIONLOST)) {
                            Toast.makeText(BluetoothConnectionActivity.this,
                                    getApplicationContext().getString(R.string.connectionLost),
                                    Toast.LENGTH_SHORT).show();
                        } else if (Objects.equals(msg.getData().getString(Constants.TOAST),
                                Constants.CONNECTIONFAILED)) {
                            Toast.makeText(BluetoothConnectionActivity.this,
                                    getApplicationContext().getString(R.string.connectionFailed),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BluetoothConnectionActivity.this,
                                    getApplicationContext().getString(R.string.dataSendComplete),
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            }
        }
    }

    /**
     * An example getter to provide it to some external class
     * or just use 'new MyHandler(this)' if you are using it internally.
     * If you only use it internally you might even want it as final member:
     * private final MyHandler mHandler = new MyHandler(this);
     */
    private Handler getClientHandler() {
        return new ClientHandler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        if (btConnectionHandler != null) {
            btConnectionHandler.stop();
        }
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }


    /**
     * Start device discover with the BluetoothAdapter
     */

    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(getApplicationContext().getString(R.string.scanning));

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }


    /**
     * The on-click listener for all devices in the ListViews
     */

    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, final View v, int arg2, long arg3) {

            final String info = ((TextView) v).getText().toString();
            //0 Name, 1 Address
            final String[] nameAndAddress = info.split("\n");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder
                    (BluetoothConnectionActivity.this);
            alertDialog.setMessage
                    (getApplicationContext().getString(R.string.conferma_invio_dati)
                            + " " + nameAndAddress[0]);
            alertDialog.setPositiveButton(R.string.Si, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    try {
                        setDataToSend(dbManager.recordsToSendBuilder(instrumentName));
                    } catch (IOException e) {
                        e.printStackTrace(); //da gestire
                    }
                    mBtAdapter.cancelDiscovery();
                    if (dataReady) {
                        // Create the result Intent and include the MAC address
                        connectDevice(nameAndAddress[1]); //check se corretto
                    } else {
                        Toast.makeText(BluetoothConnectionActivity.this,
                                R.string.no_data,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            alertDialog.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //annulla la scelta
                }
            });
            alertDialog.create().show();
        }
    };


    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (Objects.requireNonNull(device).getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                newDevicesListView.setOnItemClickListener(mDeviceClickListener);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                    newDevicesListView.setOnItemClickListener(null);
                }
                scanButton.setVisibility(View.VISIBLE);
            }
            if (Objects.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    setTitle(getResources().getString(R.string.disconnected));
                    final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(BluetoothConnectionActivity.this);
                    dlgAlert.setMessage(getString(R.string.bluetoothNotAvailable));
                    dlgAlert.setTitle("MisurApp");
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton("Ok",
                            (dialog, which) -> {
                                finish();
                            });
                    dlgAlert.create().show();
                }
            }
        }
    };

    /**
     * Establish connection with other device
     *
     * @param address Address of the device we're connecting
     */
    private void connectDevice(String address) {
        // Get the BluetoothDevice object
        BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        btConnectionHandler.connect(device);
    }

    /**
     * Set data to send.
     *
     * @param records data to be send over bluetooth
     */
    private void setDataToSend(RecordsWithEmailAndInstrument records) throws IOException {
        // Check that there's actually something to send
        if (records != null) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] bytesToSend = records.serialize();
            btConnectionHandler.setData(bytesToSend);
            dataReady = true;
        } else {
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
        }
    }
}



