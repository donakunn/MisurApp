package com.example.misurapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.BluetoothConnection.BluetoothConnectionService;
import com.example.misurapp.BluetoothConnection.BluetoothServer;
import com.example.misurapp.BluetoothConnection.Constants;
import com.example.misurapp.db.InstrumentRecord;
import com.example.misurapp.db.InstrumentsDBSchema;
import com.example.misurapp.db.RecordsWithEmailAndInstrumentName;
import com.example.misurapp.db.ScoutMasterInstrumentRecord;
import com.example.misurapp.utility.DeleteRowActions;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ScoutMasterDatabaseActivity extends MisurAppInstrumentBaseActivity {

    public static final int DISCOVERY_DURATION = 300;
    private SharedPreferences.Editor editor;

    /**
     * Local Bluetooth adapter
     */

    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Intent request Code
     */
    private static final int REQUEST_ACTION_DISCOVERABLE = 3;
    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothServer btConnectionHandler = null;

    private TableRow.LayoutParams tableRowPar = new TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    private LinearLayout linearLayout;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_caposcout);

        IntentFilter bluetoothStateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, bluetoothStateFilter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, getResources().getString(R.string.bluetoothNotAvailable),
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // startServer() will then be called during onActivityResult
            ensureDiscoverable();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (btConnectionHandler != null) {
            btConnectionHandler.stop();
        }
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (btConnectionHandler != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (btConnectionHandler.getState() == BluetoothConnectionService.STATE_NONE) {
                // Start the Bluetooth services
                btConnectionHandler.start();
            }
        } else {
            if (mBluetoothAdapter.getScanMode() ==
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                startServer();
            }
        }
        showRecordsOnScoutMasterActivity(dbManager.readScoutMasterValuesFromDB());
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                    DISCOVERY_DURATION);
            startActivityForResult(discoverableIntent, REQUEST_ACTION_DISCOVERABLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACTION_DISCOVERABLE) {
            if (resultCode == DISCOVERY_DURATION) {
                startServer();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder
                        (ScoutMasterDatabaseActivity.this);
                alertDialog.setMessage
                        (getApplicationContext().getString(R.string.discoverableNeeded));
                alertDialog.setPositiveButton(R.string.Si, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mBluetoothAdapter.isEnabled()) {
                            startServer();
                        } else {
                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                        }
                    }
                });

                alertDialog.setNegativeButton(R.string.No, (dialog, id) -> ensureDiscoverable());
                alertDialog.create().show();
            }
        } else if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                startServer();
            } else {
                final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage(getResources().getString(R.string.bluetoothNeeded));
                dlgAlert.setTitle("MisurApp");
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton("Ok",
                        (dialog, which) -> {
                        });
                dlgAlert.create().show();
            }
        }
    }

    private void startServer() {
        btConnectionHandler = new BluetoothServer(getServerHandler());
        btConnectionHandler.start();
    }

    @SuppressLint("HandlerLeak")
    private class ServerHandler extends Handler {
        //Using a weak reference means you won't prevent garbage collection
        private final WeakReference<ScoutMasterDatabaseActivity> serverWeakReference;

        public ServerHandler(ScoutMasterDatabaseActivity serverIstance) {
            serverWeakReference = new WeakReference<>(serverIstance);
        }

        @Override
        public void handleMessage(Message msg) {
            ScoutMasterDatabaseActivity handler = serverWeakReference.get();
            if (handler != null) {
                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothConnectionService.STATE_CONNECTED:
                                setTitle(getResources().getString(R.string.connected));
                                break;
                            case BluetoothConnectionService.STATE_CONNECTING:
                                setTitle(getResources().getString(R.string.connecting));
                                break;
                            case BluetoothConnectionService.STATE_NONE:
                                setTitle(getResources().getString(R.string.not_connected));
                                break;
                            case BluetoothConnectionService.STATE_LISTEN:
                                setTitle(getResources().getString(R.string.listen));
                        }
                        break;
                    case Constants.MESSAGE_READ:
                        setTitle(getResources().getString(R.string.dataDownload));
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        try {
                            List<ScoutMasterInstrumentRecord> receivedValues =
                                    scoutMasterRecordListMaker
                                            (RecordsWithEmailAndInstrumentName
                                                    .deserialize(readBuf));
                            saveReceivedRecordsOnDB(receivedValues);
                            Toast.makeText(ScoutMasterDatabaseActivity.this,
                                    getResources().getString(R.string.newData),
                                    Toast.LENGTH_SHORT).show();

                            //refresh data list
                            linearLayout.removeAllViews();
                            showRecordsOnScoutMasterActivity(dbManager.
                                    readScoutMasterValuesFromDB());
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Constants.MESSAGE_TOAST:
                        if (Objects.equals(msg.getData().getString(Constants.TOAST),
                                Constants.CONNECTIONLOST)) {
                            Toast.makeText(ScoutMasterDatabaseActivity.this,
                                    getResources().getString(R.string.connectionLost),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ScoutMasterDatabaseActivity.this,
                                    getResources().getString(R.string.connectionFailed),
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
    private Handler getServerHandler() {
        return new ServerHandler(this);
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (Objects.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                    setTitle(getResources().getString(R.string.disconnected));
                    btConnectionHandler.stop();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        setTitle(getResources().getString(R.string.disconnecting));
                        break;
                    case BluetoothAdapter.STATE_ON:
                    startServer();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        setTitle(getResources().getString(R.string.connecting));
                        break;
                }
            }
        }
    };

    //trasforma l'oggetto contenente lista record, email e strumento in una lista di record con
    //schema uguale a tabella scout master che conterrà i valori
    private List<ScoutMasterInstrumentRecord> scoutMasterRecordListMaker
    (RecordsWithEmailAndInstrumentName recordsWithEmail) {
        List<ScoutMasterInstrumentRecord> scoutMasterRecordsList = new LinkedList<>();
        List<InstrumentRecord> recordList = recordsWithEmail.getBoyscoutRecords();
        for (InstrumentRecord record : recordList) {
            ScoutMasterInstrumentRecord recordToSave = new ScoutMasterInstrumentRecord
                    (record.getId(), record.getDate(), record.getValue(),
                            recordsWithEmail.getBoyScoutEmail(),
                            recordsWithEmail.getInstrumentName());
            scoutMasterRecordsList.add(recordToSave);
        }
        return scoutMasterRecordsList;
    }

    //salva la lista su db
    private void saveReceivedRecordsOnDB(List<ScoutMasterInstrumentRecord> records) {
        dbManager.multipleInsert(records);
    }

    private void showRecordsOnScoutMasterActivity(List<ScoutMasterInstrumentRecord> records) {
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.removeAllViews();
        TableRow query;

        TextView nickname, timestamp, strumento, value;

        for (final ScoutMasterInstrumentRecord record : records) {
            query = new TableRow(ScoutMasterDatabaseActivity.this);
            query.setPadding(20, 20, 5, 20);

            nickname = new TextView(ScoutMasterDatabaseActivity.this, null,
                    R.style.textstyle);
            tableRowPar.weight = 1;
            nickname.setLayoutParams(tableRowPar);
            nickname.setGravity(Gravity.CENTER_VERTICAL);
            nickname.setPadding(10, 10, 10, 10);
            nickname.setTypeface(null, Typeface.BOLD);
            nickname.setText(record.getEmail());

            query.addView(nickname);

            timestamp = new TextView(ScoutMasterDatabaseActivity.this, null,
                    R.style.textstyle);
            tableRowPar.weight = 1;
            timestamp.setLayoutParams(tableRowPar);
            timestamp.setGravity(Gravity.CENTER_VERTICAL);
            timestamp.setPadding(10, 10, 10, 10);
            timestamp.setTypeface(null, Typeface.BOLD);
            timestamp.setText(record.getDate());

            query.addView(timestamp);

            strumento = new TextView(ScoutMasterDatabaseActivity.this, null,
                    R.style.textstyle);
            tableRowPar.weight = 1;
            strumento.setLayoutParams(tableRowPar);
            strumento.setGravity(Gravity.CENTER_VERTICAL);
            strumento.setPadding(10, 10, 10, 10);
            strumento.setTypeface(null, Typeface.BOLD);
            strumento.setText(record.getInstrumentName());

            query.addView(strumento);

            value = new TextView(ScoutMasterDatabaseActivity.this, null,
                    R.style.textstyle);
            tableRowPar.weight = 1;
            value.setLayoutParams(tableRowPar);
            value.setGravity(Gravity.CENTER_VERTICAL);
            value.setTypeface(null, Typeface.BOLD);
            value.setText(String.valueOf(record.getValue()));

            query.addView(value);

            final ImageButton deleteButton = new ImageButton(ScoutMasterDatabaseActivity.this, null, R.style.buttondeletestyle);
            deleteButton.setLayoutParams(tableRowPar);
            deleteButton.setImageResource(R.drawable.ic_baseline_delete_24);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    deleteButton.setClickable(false);
                    DeleteRowActions deleteRowActions = new DeleteRowActions
                            (ScoutMasterDatabaseActivity.this, dbManager,
                                    linearLayout, InstrumentsDBSchema.ScoutMasterTable.TABLENAME);
                    deleteRowActions.actionsOnDeleteButtonPress(v, record);

                }
            });

            query.addView(deleteButton);
            linearLayout.addView(query);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem googleDrive = menu.findItem(R.id.action_google_drive);
        googleDrive.setVisible(true);
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
            String[] listItems = new String[]{getResources().getString(R.string.lingua_inglese), getResources().getString(R.string.lingua_spagnola), getResources().getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ScoutMasterDatabaseActivity.this);
            mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = getIntent();
                    switch (which) {
                        case 0:
                            changeLang("en");
                            currentLangCode = "en";
                            finish();
                            startActivity(intent);
                            break;
                        case 1:
                            changeLang("es");
                            currentLangCode = "es";
                            finish();
                            startActivity(intent);
                            break;
                        case 2:
                            changeLang("it");
                            currentLangCode = "it";
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



        if (id == R.id.action_google_drive) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScoutMasterDatabaseActivity.this);
            alertDialog.setMessage(R.string.conferma_google_drive);
            alertDialog.setPositiveButton(R.string.Si, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //Qui va il codice per salvare le misure su Google Drive
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}