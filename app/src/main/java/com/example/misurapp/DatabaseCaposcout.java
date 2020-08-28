package com.example.misurapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misurapp.BluetoothConnection.BluetoothConnectionService;
import com.example.misurapp.BluetoothConnection.BluetoothServer;
import com.example.misurapp.BluetoothConnection.Constants;
import com.example.misurapp.db.DbManager;
import com.example.misurapp.db.InstrumentsDBSchema;
import com.example.misurapp.db.RecordsWithEmailAndInstrumentName;
import com.example.misurapp.db.InstrumentRecord;
import com.example.misurapp.db.ScoutMasterInstrumentRecord;
import com.example.misurapp.utility.DeleteRowActions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class DatabaseCaposcout extends AppCompatActivity {

    String[] listItems;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    //BluetoothClass members
    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;

    /**
     * Local Bluetooth adapter
     */

    private BluetoothAdapter mBluetoothAdapter = null;


    /**
     * Member object for the chat services
     */

    private BluetoothServer btConnectionHandler = null;

    private TableRow.LayoutParams tableRowPar = new TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    private LinearLayout linearLayout;

    private DbManager dbManager = new DbManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_caposcout);

        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // startServer() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (btConnectionHandler == null) {
            startServer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (btConnectionHandler != null) {
            btConnectionHandler.stop();
        }
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
                // Start the Bluetooth chat services
                btConnectionHandler.start();
            }
        }
        showRecordsOnScoutMasterActivity(dbManager.readScoutMasterValuesFromDB());
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private void startServer() {
        //SERVER IN ASCOLTO PER 300 SECONDI, SI PUO COLLEGARE A BOTTONE
        ensureDiscoverable();
        btConnectionHandler = new BluetoothServer(mHandler);
        btConnectionHandler.start();

    }
    //LEGGERE RISULTATO ACTIVITY PER CONTROLLARE CHE SIA STATO ACCETTATO

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
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
                        case BluetoothConnectionService.STATE_LISTEN:
                            setTitle(getApplicationContext().getString(R.string.listen));

                    }
                    break;
                case Constants.MESSAGE_READ:
                    setTitle(getApplicationContext().getString(R.string.dataDownload));
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    try {
                        List<ScoutMasterInstrumentRecord> receivedValues =
                                scoutMasterRecordListMaker
                                        (RecordsWithEmailAndInstrumentName.deserialize(readBuf));
                        saveReceivedRecordsOnDB(receivedValues);
                        Toast.makeText(DatabaseCaposcout.this,
                                getApplicationContext().getString(R.string.newData),
                                Toast.LENGTH_SHORT).show();

                        //refresh data list
                        linearLayout.removeAllViews();
                        showRecordsOnScoutMasterActivity(dbManager.readScoutMasterValuesFromDB());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (msg.getData().getString(Constants.TOAST).equals(Constants.CONNECTIONLOST)) {
                        Toast.makeText(DatabaseCaposcout.this,
                                getApplicationContext().getString(R.string.connectionLost),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DatabaseCaposcout.this,
                                getApplicationContext().getString(R.string.connectionFailed),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
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
                    (record.getId(),record.getDate(),record.getValue(),
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
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        TableRow query;

        TextView nickname, data, strumento, valore;

        ImageButton cancella;
        for (final ScoutMasterInstrumentRecord record : records) {
            query = new TableRow(DatabaseCaposcout.this);
            query.setPadding(20, 20, 5, 20);

            nickname = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            nickname.setLayoutParams(tableRowPar);
            nickname.setGravity(Gravity.CENTER_VERTICAL);
            nickname.setPadding(10, 10, 10, 10);
            nickname.setTypeface(null, Typeface.BOLD);
            nickname.setText(record.getEmail());

            query.addView(nickname);

            data = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            data.setLayoutParams(tableRowPar);
            data.setGravity(Gravity.CENTER_VERTICAL);
            data.setPadding(10, 10, 10, 10);
            data.setTypeface(null, Typeface.BOLD);
            data.setText(record.getDate());

            query.addView(data);

            strumento = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            strumento.setLayoutParams(tableRowPar);
            strumento.setGravity(Gravity.CENTER_VERTICAL);
            strumento.setPadding(10, 10, 10, 10);
            strumento.setTypeface(null, Typeface.BOLD);
            strumento.setText(record.getInstrumentName());

            query.addView(strumento);

            valore = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            valore.setLayoutParams(tableRowPar);
            valore.setGravity(Gravity.CENTER_VERTICAL);
            valore.setTypeface(null, Typeface.BOLD);
            valore.setText(String.valueOf(record.getValue()));

            query.addView(valore);

            cancella = new ImageButton(DatabaseCaposcout.this, null, R.style.buttondeletestyle);
            cancella.setLayoutParams(tableRowPar);
            cancella.setImageResource(R.drawable.ic_baseline_delete_24);

            cancella.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DeleteRowActions deleteRowActions = new DeleteRowActions
                            (DatabaseCaposcout.this,dbManager,
                                    linearLayout, InstrumentsDBSchema.ScoutMasterTable.TABLENAME);
                    deleteRowActions.actionsOnDeleteButtonPress(v, record);

                }
            });

            query.addView(cancella);
            linearLayout.addView(query);
        }
    }

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem condividi = menu.findItem(R.id.action_condividi);
        condividi.setVisible(true);

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
            listItems = new String[]{getResources().getString(R.string.lingua_inglese), getResources().getString(R.string.lingua_spagnola), getResources().getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(DatabaseCaposcout.this);
            mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = getIntent();


                    switch (which) {

                        case 0:
                            setAppLocale("en");
                            finish();
                            startActivity(intent);
                            editor.putBoolean("flagMain", true);
                            editor.apply();
                            break;

                        case 1:
                            setAppLocale("es");
                            finish();
                            startActivity(intent);
                            editor.putBoolean("flagMain", true);
                            editor.apply();
                            break;

                        case 2:
                            setAppLocale("it");
                            finish();
                            startActivity(intent);
                            editor.putBoolean("flagMain", true);
                            editor.apply();
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
            return true;
        }

        if (id == R.id.action_condividi) {
            return true;
        }

        if (id == R.id.action_google_drive) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DatabaseCaposcout.this);
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