package com.example.misurapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
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

import com.example.misurapp.bluetoothConnection.BluetoothClient;
import com.example.misurapp.bluetoothConnection.BluetoothConnectionService;
import com.example.misurapp.bluetoothConnection.Constants;
import com.example.misurapp.R;
import com.example.misurapp.db.DbManager;
import com.example.misurapp.db.RecordsWithEmailAndInstrument;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Set;

/**
 * This Activity lists any paired devices and devices detected in the area after discovery.
 * When a device is chosen by the user, establish a connection and send related data
 */
public class BluetoothConnectionActivity extends MisurAppBaseActivity {
    /**
     * Intent request code
     */
    private static final int REQUEST_ENABLE_BT = 3;
    /**
     * Instrument name
     */
    private String instrumentName;
    /**
     * DbManager object used to perform database operations.
     */
    private DbManager dbManager = new DbManager(this);
    /**
     * list view which contains new devices found.
     */
    private ListView newDevicesListView;
    /**
     * Tag for Log
     */
    private static final String TAG = "DeviceListActivity";
    /**
     * BluetoothAdapter reference
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

    /**
     * initialize layout, register a BroadCastReceiver initialize scan button with a listener and
     * check if app has COARSE_LOCATION permission
     *
     * @param savedInstanceState activity saved instance bundle
     */
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setResult(Activity.RESULT_CANCELED);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null) {
            Toast.makeText(this, R.string.bluetoothNotAvailable, Toast.LENGTH_LONG).show();
            BluetoothConnectionActivity.this.finish();
        }

        scanButton = findViewById(R.id.button_scan);
        scanButton.setOnClickListener(v -> {
            doDiscovery();
            v.setVisibility(View.GONE);
        });
        coarsePermissionCheck();
    }

    /**
     * check if app has COARSE_LOCATION permission, if is not granted start a request permission
     * activity
     */
    private void coarsePermissionCheck() {
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
        } else {
            initializeArrayAdapters();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult");
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
                        (dialog, which) -> finish());
                dlgAlert.create().show();
            }
        }
    }

    /**
     * This method manage operation to initialize array adapters. One for already paired devices and
     * // one for newly discovered devices
     */
    private void initializeArrayAdapters() {
        Log.d(TAG, "initializing array adapters");
        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);

        ListView pairedListView = findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);

        newDevicesListView = findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }

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

    /**
     * This class handles messages received from the BluetoothClient class and performs operations
     * based on the message received
     */
    @SuppressLint("HandlerLeak")
    private class ClientHandler extends Handler {
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
     * A getter that returns an inner class ClientHandler object
     */
    private Handler getClientHandler() {
        return new ClientHandler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        if (btConnectionHandler != null) {
            btConnectionHandler.stop();
        }

        this.unregisterReceiver(mReceiver);
    }


    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        setProgressBarIndeterminateVisibility(true);
        setTitle(getApplicationContext().getString(R.string.scanning));

        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        mBtAdapter.startDiscovery();
    }

    /**
     * The on-click listener for all devices in the ListViews; on click show a dialog asking if
     * we want to share data to the selected device, on positive result establish connection and
     * send data.
     */
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, final View v, int arg2, long arg3) {

            final String info = ((TextView) v).getText().toString();
            final String[] nameAndAddress = info.split("\n");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder
                    (BluetoothConnectionActivity.this);
            alertDialog.setMessage
                    (getApplicationContext().getString(R.string.conferma_invio_dati)
                            + " " + nameAndAddress[0]);
            alertDialog.setPositiveButton(R.string.Si, (dialog, id) -> {

                try {
                    setDataToSend(dbManager.recordsToSendBuilder(instrumentName));
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                mBtAdapter.cancelDiscovery();
                if (dataReady) {
                    connectDevice(nameAndAddress[1]);
                } else {
                    Toast.makeText(BluetoothConnectionActivity.this,
                            R.string.no_data,
                            Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.setNegativeButton(R.string.No, (dialog, id) -> {
            });
            alertDialog.create().show();
        }
    };

    /**
     * The BroadcastReceiver that listens for discovered devices, changes the title when
     * discovery is finished and finish the activity if the bluetooth is disabled
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
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
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    setTitle(getResources().getString(R.string.disconnected));
                    final AlertDialog.Builder dlgAlert = new AlertDialog.Builder
                            (BluetoothConnectionActivity.this);
                    dlgAlert.setMessage(getString(R.string.bluetoothNotAvailable));
                    dlgAlert.setTitle("MisurApp");
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton("Ok",
                            (dialog, which) -> finish());
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
        Log.d(TAG, "connecting to " + address);
        BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
        btConnectionHandler.connect(device);
    }

    /**
     * Set data to send.
     *
     * @param records data to be send over bluetooth
     */
    private void setDataToSend(RecordsWithEmailAndInstrument records) throws IOException {
        Log.d(TAG, "Setting data to send");
        if (records != null) {
            byte[] bytesToSend = records.serialize();
            btConnectionHandler.setData(bytesToSend);
            dataReady = true;
        } else {
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
        }
    }
}



