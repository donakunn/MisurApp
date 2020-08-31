package com.example.misurapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.List;
import java.util.Objects;

public class BoyscoutDBValuesActivity extends MisurAppBaseActivity {

    private TableRow.LayoutParams tableRowPar = new TableRow.LayoutParams
            (TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    private DbManager appDb;
    private LinearLayout linearLayout;
    private String sensorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorName = Objects.requireNonNull(getIntent().getExtras()).getString("sensorName");
        appDb = new DbManager(this);
        List<InstrumentRecord> instrumentRecordsReadFromDB = appDb.readBoyscoutValuesFromDB
                (sensorName);
        setContentView(R.layout.activity_database_boyscout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linearLayout = findViewById(R.id.linearLayout);

        if (instrumentRecordsReadFromDB.isEmpty()) {
            final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage(R.string.noValue);
            dlgAlert.setTitle("MisurApp");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            dlgAlert.create().show();
        } else {
            showBoyscoutTableValues(instrumentRecordsReadFromDB);
        }

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
            date.setText(record.getDate());

            dbBoyScoutQuery.addView(date);

            TextView value = new TextView(BoyscoutDBValuesActivity.this, null, R.style.textstyle);
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

            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    deleteButton.setClickable(false);
                 DeleteRowActions deleteRowActions = new DeleteRowActions
                         (BoyscoutDBValuesActivity.this,appDb,
                                 linearLayout,InstrumentsDBSchema.BoyscoutTable.TABLENAME);
                    deleteRowActions.actionsOnDeleteButtonPress(v, record);
                }
            });

            dbBoyScoutQuery.addView(deleteButton);
            linearLayout.addView(dbBoyScoutQuery);
        }
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
            String[] listItems = new String[]{getResources().getString(R.string.lingua_inglese),
                    getResources().getString(R.string.lingua_spagnola), getResources()
                    .getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder
                    (BoyscoutDBValuesActivity.this);
            mBuilder.setSingleChoiceItems(listItems, -1,
                    new DialogInterface.OnClickListener() {
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

            mBuilder.setNeutralButton(getResources().getString(R.string.dialog_annulla),
                    new DialogInterface.OnClickListener() {
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

        //pulsante condividi
        if (id == R.id.action_condividi) {
            Intent intent = new Intent(BoyscoutDBValuesActivity.this,
                    BluetoothConnectionActivity.class);
            intent.putExtra("sensorName", sensorName);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_google_drive) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder
                    (BoyscoutDBValuesActivity.this);
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