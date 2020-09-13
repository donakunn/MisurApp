package com.example.misurapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

import com.example.misurapp.R;
import com.example.misurapp.bluetoothConnection.BluetoothConnectionService;
import com.example.misurapp.bluetoothConnection.BluetoothServer;
import com.example.misurapp.bluetoothConnection.Constants;
import com.example.misurapp.db.InstrumentRecord;
import com.example.misurapp.db.InstrumentsDBSchema;
import com.example.misurapp.db.RecordsWithEmailAndInstrument;
import com.example.misurapp.db.ScoutMasterInstrumentRecord;
import com.example.misurapp.googleDrive.DriveServiceHelper;
import com.example.misurapp.utility.DeleteRowActions;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * This activity is about showing the values received by the boyscout, starting a bluetooth server
 * that takes care of receiving and showing new values. Each row contains email from the user who
 * sent the values, date, tool name, value and its delete button. from the top bar you can back up
 * the values to Google Drive and restore them.
 */
public class ScoutMasterDbActivity extends MisurAppInstrumentBaseActivity {
    /**
     * debug tag
     */
    private final String TAG = "ScoutMasterDBActivity";
    /**
     * DriveServiceHelper to handle google Drive operations
     */
    private DriveServiceHelper mDriveServiceHelper;
    /**
     * progress bar of the activity
     */
    private LinearLayout progressBar;
    /**
     * Duration of the bluetooth discovery mode.
     */
    public static final int DISCOVERY_DURATION = 300;
    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;
    /**
     * Intent request Code for discoverable mode.
     */
    private static final int REQUEST_ACTION_DISCOVERABLE = 3;
    /**
     * Intent request Code for enable bluetooth request.
     */
    private static final int REQUEST_ENABLE_BT = 2;
    /**
     * BluetoothServer object to start using bluetooth server features
     */
    private BluetoothServer btConnectionHandler = null;
    /**
     * Set of layout parameters used in table rows.
     */
    private TableRow.LayoutParams tableRowPar = new TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    /**
     * Layout used to show values in the activity
     */
    private LinearLayout linearLayout;


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_database_caposcout);

        IntentFilter bluetoothStateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, bluetoothStateFilter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.llProgressBar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, getResources().getString(R.string.bluetoothNotAvailable),
                    Toast.LENGTH_LONG).show();
            finish();
        }

        //ACCESSO CREDENZIALI GOOGLE DRIVE
        mDriveServiceHelper = getGDriveServiceHelper();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        // If BT is not on, request that it be enabled.
        // startServer() will then be called during onActivityResult
        ensureDiscoverable();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        //stop the server
        if (btConnectionHandler != null) {
            btConnectionHandler.stop();
        }
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
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

    /**
     * This method is responsible for checking whether the bluetooth is active and in discovery
     * mode, otherwise it starts a discoverableIntent to prompt it to be activated
     */
    private void ensureDiscoverable() {
        Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                    DISCOVERY_DURATION);
            startActivityForResult(discoverableIntent, REQUEST_ACTION_DISCOVERABLE);
        }
    }

    /**
     * This method reads the response received from the discoverableIntent and whether or
     * not to start the server
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its
     *                    setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACTION_DISCOVERABLE) {
            if (resultCode == DISCOVERY_DURATION) {
                startServer();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                if (mBluetoothAdapter.isEnabled()) {
                    startServer();
                } else {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
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

    /**
     * This method initializes the server and starts it
     */
    private void startServer() {
        Log.d(TAG, "starting server");
        btConnectionHandler = new BluetoothServer(getServerHandler());
        btConnectionHandler.start();
    }

    /**
     * Handler implementation to manage Message object received by server. It manage update activity
     * title, save values received by the server and refresh the values list
     */
    @SuppressLint("HandlerLeak")
    private class ServerHandler extends Handler {
        //Using a weak reference means you won't prevent garbage collection
        private final WeakReference<ScoutMasterDbActivity> serverWeakReference;

        public ServerHandler(ScoutMasterDbActivity serverIstance) {
            serverWeakReference = new WeakReference<>(serverIstance);
        }

        @Override
        public void handleMessage(Message msg) {
            ScoutMasterDbActivity handler = serverWeakReference.get();
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
                                            (RecordsWithEmailAndInstrument
                                                    .deserialize(readBuf));
                            saveReceivedRecordsOnDB(receivedValues);
                            Toast.makeText(ScoutMasterDbActivity.this,
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
                            Toast.makeText(ScoutMasterDbActivity.this,
                                    getResources().getString(R.string.connectionLost),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ScoutMasterDbActivity.this,
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

    /**
     * This broadcast receiver is responsible for checking whether bluetooth state change while
     * activity is active, and if this happens it update activity title and start or stop the server
     * based on bluetooth state
     */
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (Objects.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
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

    /**
     * turns the object containing record list, email, and tool into a list of records with schema
     * equal to master scout table that will contain values
     *
     * @param recordsWithEmail object wich contains user email and read values.
     * @return list with ScoutMasterInstrumentRecord objects
     */
    private List<ScoutMasterInstrumentRecord> scoutMasterRecordListMaker
    (RecordsWithEmailAndInstrument recordsWithEmail) {
        Log.d(TAG, "making list of queries");
        List<ScoutMasterInstrumentRecord> scoutMasterRecordsList = new LinkedList<>();
        List<InstrumentRecord> recordList = recordsWithEmail.getBoyscoutRecords();
        for (InstrumentRecord record : recordList) {
            ScoutMasterInstrumentRecord recordToSave = new ScoutMasterInstrumentRecord
                    (record.getId(), record.getTimestamp(), record.getValue(),
                            recordsWithEmail.getBoyScoutEmail(),
                            recordsWithEmail.getInstrumentName());
            scoutMasterRecordsList.add(recordToSave);
        }
        return scoutMasterRecordsList;
    }

    /**
     * This method saves the queries contained in the list within the database.
     *
     * @param records list which contains values to be saved on the database.
     */
    private void saveReceivedRecordsOnDB(List<ScoutMasterInstrumentRecord> records) {
        Log.d(TAG, "saving multiple queries");
        dbManager.multipleInsert(records);
    }

    /**
     * This method manage showing queries read from the database.
     *
     * @param records list of the values read from database
     */
    public void showRecordsOnScoutMasterActivity(List<ScoutMasterInstrumentRecord> records) {
        Log.d(TAG, "showing records");

        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.removeAllViews();
        TableRow query;

        TextView nickname, timestamp, strumento, value;

        for (final ScoutMasterInstrumentRecord record : records) {
            query = new TableRow(ScoutMasterDbActivity.this);
            query.setPadding(20, 20, 5, 20);

            nickname = new TextView(ScoutMasterDbActivity.this, null,
                    R.style.textstyle);
            tableRowPar.weight = 1;
            nickname.setLayoutParams(tableRowPar);
            nickname.setGravity(Gravity.CENTER_VERTICAL);
            nickname.setPadding(10, 10, 10, 10);
            nickname.setTypeface(null, Typeface.BOLD);
            nickname.setText(record.getEmail());

            query.addView(nickname);

            timestamp = new TextView(ScoutMasterDbActivity.this, null,
                    R.style.textstyle);
            tableRowPar.weight = 1;
            timestamp.setLayoutParams(tableRowPar);
            timestamp.setGravity(Gravity.CENTER_VERTICAL);
            timestamp.setPadding(10, 10, 10, 10);
            timestamp.setTypeface(null, Typeface.BOLD);
            timestamp.setText(record.getTimestamp());

            query.addView(timestamp);

            strumento = new TextView(ScoutMasterDbActivity.this, null,
                    R.style.textstyle);
            tableRowPar.weight = 1;
            strumento.setLayoutParams(tableRowPar);
            strumento.setGravity(Gravity.CENTER_VERTICAL);
            strumento.setPadding(10, 10, 10, 10);
            strumento.setTypeface(null, Typeface.BOLD);
            strumento.setText(getInstrumentNameBasedOnLanguage(record.getInstrumentName()));

            query.addView(strumento);

            value = new TextView(ScoutMasterDbActivity.this, null,
                    R.style.textstyle);
            tableRowPar.weight = 1;
            value.setLayoutParams(tableRowPar);
            value.setGravity(Gravity.CENTER_VERTICAL);
            value.setTypeface(null, Typeface.BOLD);
            value.setText(String.valueOf(record.getValue()));

            query.addView(value);

            final ImageButton deleteButton = new ImageButton
                    (ScoutMasterDbActivity.this, null,
                            R.style.buttondeletestyle);
            deleteButton.setLayoutParams(tableRowPar);
            deleteButton.setImageResource(R.drawable.ic_baseline_delete_24);

            deleteButton.setOnClickListener(v -> {
                deleteButton.setClickable(false);
                DeleteRowActions deleteRowActions = new DeleteRowActions
                        (ScoutMasterDbActivity.this, dbManager,
                                linearLayout, InstrumentsDBSchema.ScoutMasterTable.TABLENAME);
                deleteRowActions.actionsOnDeleteButtonPress(v, record);

            });

            query.addView(deleteButton);
            linearLayout.addView(query);
        }
    }

    /**
     * This method returns the name of the instrument translated into the language in use based on
     * the name of the tool passed
     *
     * @param instrumentName name of the tool to provide the string from the resources
     * @return Tool name from resource constant strings
     */
    private String getInstrumentNameBasedOnLanguage(String instrumentName) {
        switch (instrumentName) {
            case "altimeter":
                return getString(R.string.strumento_altimetro);
            case "barometer":
                return getString(R.string.strumento_barometro);
            case "compass":
                return getString(R.string.strumento_bussola);
            case "hygrometer":
                return getString(R.string.strumento_igrometro);
            case "photometer":
                return getString(R.string.strumento_fotometro);
            case "lastStepsRegister":
                return getString(R.string.strumento_contapassi);
            case "thermometer":
                return getString(R.string.strumento_termometro);
        }
        //se per qualche motivo la stringa non viene trovata
        return instrumentName;
    }

    /**
     * This methods add google Drive button on the activity menu
     *
     * @param menu Activity menu reference.
     * @return true if it succeeds, false otherwise
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem googleDrive = menu.findItem(R.id.action_google_drive);
        googleDrive.setVisible(true);

        MenuItem ripristino = menu.findItem(R.id.action_ripristino);
        ripristino.setVisible(true);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected. It perform language
     * change based on the selected item, backup on Google Drive and restore.
     *
     * @param item MenuItem object
     * @return boolean that describe operations result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Gestisci i clic sugli elementi della barra delle azioni qui.
        La barra delle azioni gestirà automaticamente i clic sul pulsante Home / Up button,
        a condizione che specifichi un'attività genitore in AndroidManifest.xml.*/
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cambio_lingua) {
            String[] listItems = new String[]{getResources().getString(R.string.lingua_inglese),
                    getResources().getString(R.string.lingua_spagnola), getResources()
                    .getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder
                    (ScoutMasterDbActivity.this);
            mBuilder.setSingleChoiceItems(listItems, -1, (dialog, which) -> {
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
            });

            mBuilder.setNeutralButton(getResources().getString(R.string.dialog_annulla), (dialog, which) -> {

            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
            return true;
        }

        if (id == R.id.action_ripristino) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder
                    (ScoutMasterDbActivity.this);
            alertDialog.setMessage(R.string.conferma_ripristino);
            alertDialog.setPositiveButton(R.string.Si, (dialog, id12) -> {
                try {
                    blockScreen(true);
                    progressBar.setVisibility(View.VISIBLE);
                    mDriveServiceHelper.restoreFile(dbManager,
                            ScoutMasterDbActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            alertDialog.setNegativeButton(R.string.No, (dialog, id1) -> {
            });
            alertDialog.create();
            alertDialog.show();
            return true;
        }

        if (id == R.id.action_google_drive) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder
                    (ScoutMasterDbActivity.this);
            alertDialog.setMessage(R.string.conferma_google_drive);
            alertDialog.setPositiveButton(R.string.Si, (dialog, id13) -> {
                //Qui va il codice per salvare le misure su Google Drive
                progressBar.setVisibility(View.VISIBLE);
                try {
                    mDriveServiceHelper.createAndSaveFile(dbManager,
                            ScoutMasterDbActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            alertDialog.setNegativeButton(R.string.No, (dialog, id1) -> {
                //annulla la scelta
            });
            alertDialog.create();
            alertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void blockScreen(boolean value){
        if(value) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }
}