package com.example.misurapp;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.db.DbManager;
import com.example.misurapp.db.InstrumentRecord;
import com.example.misurapp.db.InstrumentsDBSchema;
import com.example.misurapp.utility.DeleteRowActions;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class BoyscoutDBValuesActivity extends MisurAppBaseActivity {
private final String TAG ="BoyScoutDBValActivity";
    private DriveServiceHelper mDriveServiceHelper;
    //FINE ATTRIBUTI GOOGLE DRIVE
    private TableRow.LayoutParams tableRowPar = new TableRow.LayoutParams
            (TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    private DbManager appDb;
    private LinearLayout linearLayout;
    private String instrumentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instrumentName = Objects.requireNonNull(getIntent().getExtras()).getString("sensorName");
        appDb = new DbManager(this);
        List<InstrumentRecord> instrumentRecordsReadFromDB = appDb.readBoyscoutValuesFromDB
                (instrumentName);
        setContentView(R.layout.activity_database_boyscout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linearLayout = findViewById(R.id.linearLayout);

        IntentFilter bluetoothStateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, bluetoothStateFilter);

        if (instrumentRecordsReadFromDB.isEmpty()) {
            final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage(R.string.noValue);
            dlgAlert.setTitle("MisurApp");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("Ok",
                    (dialog, which) -> finish());
            dlgAlert.create().show();
        } else {
            showBoyscoutTableValues(instrumentRecordsReadFromDB);
        }

        //ACCESSO CREDENZIALI GOOGLE DRIVE
        mDriveServiceHelper = getGDriveServiceHelper();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void showBoyscoutTableValues(List<InstrumentRecord> instrumentRecords) {
        for (final InstrumentRecord record : instrumentRecords) {
            TableRow dbBoyScoutQuery = new TableRow(BoyscoutDBValuesActivity.this);
            dbBoyScoutQuery.setPadding(20, 20, 5, 20);

            TextView date = new TextView(BoyscoutDBValuesActivity.this, null,
                    R.style.textstyle);
            tableRowPar.weight = 1;
            date.setLayoutParams(tableRowPar);
            date.setGravity(Gravity.CENTER_VERTICAL);
            date.setPadding(10, 10, 10, 10);
            date.setTypeface(null, Typeface.BOLD);
            date.setText(record.getTimestamp());

            dbBoyScoutQuery.addView(date);

            TextView value = new TextView(BoyscoutDBValuesActivity.this, null,
                    R.style.textstyle);
            tableRowPar.weight = 1;
            value.setLayoutParams(tableRowPar);
            value.setGravity(Gravity.CENTER);
            value.setTypeface(null, Typeface.BOLD);
            value.setText(String.valueOf(record.getValue()));

            dbBoyScoutQuery.addView(value);

            final ImageButton deleteButton = new ImageButton
                    (BoyscoutDBValuesActivity.this, null, R.style.buttondeletestyle);
            deleteButton.setLayoutParams(tableRowPar);
            deleteButton.setImageResource(R.drawable.ic_baseline_delete_24);

            deleteButton.setOnClickListener(v -> {
                deleteButton.setClickable(false);
             DeleteRowActions deleteRowActions = new DeleteRowActions
                     (BoyscoutDBValuesActivity.this,appDb,
                             linearLayout,InstrumentsDBSchema.BoyscoutTable.TABLENAME);
                deleteRowActions.actionsOnDeleteButtonPress(v, record);
            });

            dbBoyScoutQuery.addView(deleteButton);
            linearLayout.addView(dbBoyScoutQuery);
        }
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (Objects.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    setTitle(getResources().getString(R.string.disconnected));
                    final AlertDialog.Builder dlgAlert = new AlertDialog.Builder
                            (BoyscoutDBValuesActivity.this);
                    dlgAlert.setMessage(R.string.bluetoothNotAvailable);
                    dlgAlert.setTitle("MisurApp");
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton("Ok",
                            (dialog, which) -> finish());
                    dlgAlert.create().show();
                }
            }
        }
    };

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
            String[] listItems = new String[]{getResources().getString(R.string.lingua_inglese),
                    getResources().getString(R.string.lingua_spagnola), getResources()
                    .getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder
                    (BoyscoutDBValuesActivity.this);
            mBuilder.setSingleChoiceItems(listItems, -1,
                    (dialog, which) -> {
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

            mBuilder.setNeutralButton(getResources().getString(R.string.dialog_annulla),
                    (dialog, which) -> {
                    });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
            return true;
        }

        if (id == R.id.action_ripristino) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder
                    (BoyscoutDBValuesActivity.this);
            alertDialog.setMessage("Vuoi ripristinare le misure dell'ultimo salvataggio fatte sul" +
                    " tuo Google Drive?");
            alertDialog.setPositiveButton(R.string.Si, (dialog, id12) -> {
                //codice di ripristino
                try {
                    mDriveServiceHelper.getIdentificativo(instrumentName)
                            .addOnSuccessListener(fileId -> mDriveServiceHelper.readFile(fileId)
                                    .addOnSuccessListener(fileContent -> {
                                        List<InstrumentRecord> instrumentRecordsReadFromDB =
                                                appDb.readBoyscoutValuesFromDB(instrumentName);
                                        String timestamp;
                                        boolean control;
                                        String[] lines = fileContent.split("\n");
                                        String[] words;
                                        if (!fileContent.isEmpty() ) { //controlla che il file
                                            // non sia vuoto
                                            if (!instrumentRecordsReadFromDB.isEmpty()) {//controlla
                                                // che nel db ci siano salvati dei valori
                                                for (String string : lines) {
                                                    words = string.split(" ");
                                                    timestamp = words[2] + " " + words[3];
                                                    control = false;
                                                    //controlla che il valore non sia già
                                                    // salvato sul database controllando il timestamp
                                                    for (final InstrumentRecord record :
                                                            instrumentRecordsReadFromDB) {
                                                        if (record.getTimestamp()
                                                                .contentEquals(timestamp)) {
                                                            control = true;
                                                        }
                                                    }
                                                    if (!control) {
                                                        appDb.saveRegisteredValues
                                                                ("valuesRecordedByBoyscout",
                                                                        null, timestamp,
                                                                instrumentName,
                                                                Float.parseFloat(words[5]));
                                                    }
                                                }
                                            } else {
                                                for (String string : lines) {
                                                    words = string.split(" ");
                                                    timestamp = words[2] + " " + words[3];
                                                    appDb.saveRegisteredValues
                                                            ("valuesRecordedByBoyscout",
                                                                    null, timestamp,
                                                            instrumentName,
                                                            Float.parseFloat(words[5]));
                                                }
                                            }
                                            showBoyscoutTableValues
                                                    (appDb.readBoyscoutValuesFromDB
                                                            (instrumentName));
                                        }
                                    }));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            alertDialog.setNegativeButton(R.string.No, (dialog, id1) -> {
            });
            AlertDialog mDialog = alertDialog.create();
            alertDialog.show();
            return true;
        }

        //pulsante condividi
        if (id == R.id.action_condividi) {
            Intent intent = new Intent(BoyscoutDBValuesActivity.this,
                    BluetoothConnectionActivity.class);
            intent.putExtra("sensorName", instrumentName);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_google_drive) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder
                    (BoyscoutDBValuesActivity.this);
            alertDialog.setMessage(R.string.conferma_google_drive);
            alertDialog.setPositiveButton(R.string.Si, (dialog, id13) -> {
                try {
                    mDriveServiceHelper.createAndSaveFile(appDb, instrumentName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            alertDialog.setNegativeButton(R.string.No, (dialog, id14) -> {
                //annulla la scelta
            });
            AlertDialog mDialog = alertDialog.create();
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
