


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

package com.example.misurapp.BluetoothConnection;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.ListaStrumentiActivity;
import com.example.misurapp.R;

import java.util.Locale;
import java.util.Set;


/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */

public class DeviceListActivity extends AppCompatActivity {
    String[] listItems;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    /**
     * Tag for Log
     */

    private static final String TAG = "DeviceListActivity";


    /**
     * Return Intent extra
     */

    public static String EXTRA_DEVICE_ADDRESS = "device_address";


    /**
     * Member fields
     */

    private BluetoothAdapter mBtAdapter;


    /**
     * Newly discovered devices
     */

    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();
        setContentView(R.layout.activity_device_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

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
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
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
        setTitle(R.string.scanning);

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

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DeviceListActivity.this);
            alertDialog.setMessage(R.string.conferma_invio_dati + info);//info dovrebbe essere il nome del Device
            alertDialog.setPositiveButton(R.string.Si, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //Qui va il codice che avvia la connessione Bluetooth
                    // User clicked OK button
                    // Cancel discovery because it's costly and we're about to connect
                    mBtAdapter.cancelDiscovery();
                    // Get the device MAC address, which is the last 17 chars in the View
                    //String info = ((TextView) v).getText().toString();
                    String address = info.substring(info.length() - 17);
                    // Create the result Intent and include the MAC address
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                    // Set result and finish this Activity
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });

            alertDialog.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
            //annulla la scelta
                }
            });
            AlertDialog mDialog = alertDialog.create();
            alertDialog.show();

            // Cancel discovery because it's costly and we're about to connect
           // mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            /*String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);*/

            // Create the result Intent and include the MAC address
           /* Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);*/

            // Set result and finish this Activity
           /* setResult(Activity.RESULT_OK, intent);
            finish();*/
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
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

    private void setAppLocale(String localCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            conf.setLocale(new Locale(localCode.toLowerCase()));
        } else {
            conf.locale = new Locale(localCode.toLowerCase());
        }
        res.updateConfiguration(conf, dm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Gestisci i clic sugli elementi della barra delle azioni qui.
        La barra delle azioni gestirà automaticamente i clic sul pulsante Home / Up button,
        a condizione che specifichi un'attività genitore in AndroidManifest.xml.*/
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cambio_lingua) {
            listItems = new String[]{getResources().getString(R.string.lingua_inglese), getResources().getString(R.string.lingua_spagnola), getResources().getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(DeviceListActivity.this);
            mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = getIntent();

                    switch (which) {

                        case 0:
                            setAppLocale("en");
                            finish();
                            startActivity(intent);
                            break;

                        case 1:
                            setAppLocale("es");
                            finish();
                            startActivity(intent);
                            break;

                        case 2:
                            setAppLocale("it");
                            finish();
                            startActivity(intent);
                            break;
                    }

                }
            });
            mBuilder.setNeutralButton(getResources().getString(R.string.dialog_annulla), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
            return true;
        }

        if (id == R.id.action_backup) {
            Intent intent = new Intent(DeviceListActivity.this, ListaStrumentiActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean("flagMain", false)) {
            editor.putBoolean("flagMain", false);
            editor.apply();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}

