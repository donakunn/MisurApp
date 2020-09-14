package com.example.misurapp.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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

import com.example.misurapp.googleDrive.DriveServiceHelper;
import com.example.misurapp.R;
import com.example.misurapp.db.DbManager;
import com.example.misurapp.db.InstrumentRecord;
import com.example.misurapp.db.InstrumentsDBSchema;
import com.example.misurapp.utility.DeleteRowActions;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * This activity is about showing the saved values of the instrument from which you called the
 * function.
 * Each row contains date, value, and its delete button. From the top bar, you can invoke the
 * function of sharing, backing up on Google Drive, and restoring values.
 */
public class BoyscoutDBValuesActivity extends MisurAppBaseActivity {
    /**
     * debug tag
     */
    private final String TAG = "BoyScoutDBValActivity";
    /**
     * DriveServiceHelper to handle google Drive operations
     */
    private DriveServiceHelper mDriveServiceHelper;
    /**
     * Set of layout parameters used in table rows.
     */
    private TableRow.LayoutParams tableRowPar = new TableRow.LayoutParams
            (TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    /**
     * DbManager object to manage database operations.
     */
    private DbManager dbManager;
    /**
     * Layout used to show values in the activity
     */
    private LinearLayout linearLayout;
    /**
     * name of the instrument whose values you want to display
     */
    private String instrumentName;
    /**
     * progress bar of the activity
     */
    private LinearLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        instrumentName = Objects.requireNonNull(getIntent().getExtras())
                .getString("sensorName");
        dbManager = new DbManager(this);
        setContentView(R.layout.activity_database_boyscout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linearLayout = findViewById(R.id.linearLayout);
        progressBar = findViewById(R.id.llProgressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        List<InstrumentRecord> instrumentRecordsReadFromDB = dbManager.readBoyscoutValuesFromDB
                (instrumentName);
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
            //ACCESSO CREDENZIALI GOOGLE DRIVE
            mDriveServiceHelper = getGDriveServiceHelper();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    /**
     * This method manage showing queries read from the database
     *
     * @param instrumentRecords List of the values read from database
     */
    public void showBoyscoutTableValues(List<InstrumentRecord> instrumentRecords) {
        linearLayout.removeAllViews();
        Log.d(TAG,"showing values read from DB");
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
                        (BoyscoutDBValuesActivity.this, dbManager,
                                linearLayout, InstrumentsDBSchema.BoyscoutTable.TABLENAME);
                deleteRowActions.actionsOnDeleteButtonPress(v, record);
            });

            dbBoyScoutQuery.addView(deleteButton);
            linearLayout.addView(dbBoyScoutQuery);
        }
    }

    /**
     * This methods adds share and google Drive button on the activity menu
     * @param menu Activity menu reference.
     * @return true if it succeeds, false otherwise
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem condividi = menu.findItem(R.id.action_condividi);
        condividi.setVisible(true);

        MenuItem googleDrive = menu.findItem(R.id.action_google_drive);
        googleDrive.setVisible(true);

        MenuItem ripristino = menu.findItem(R.id.action_ripristino);
        ripristino.setVisible(true);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected. It perform language
     * change based on the selected item, backup on Google Drive, restore, and calls the activity
     * for the bluetooth connection.
     *
     * @param item MenuItem object
     * @return boolean that describe operations result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            alertDialog.setMessage(R.string.conferma_ripristino);
            alertDialog.setPositiveButton(R.string.Si, (dialog, id12) -> {
                //codice di ripristino
                blockScreen(true);
                progressBar.setVisibility(View.VISIBLE);
                try {
                    mDriveServiceHelper.restoreFile(dbManager, instrumentName,
                            BoyscoutDBValuesActivity.this);
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
                progressBar.setVisibility(View.VISIBLE);
                try {
                    mDriveServiceHelper.createAndSaveFile(dbManager,
                            BoyscoutDBValuesActivity.this, instrumentName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            alertDialog.setNegativeButton(R.string.No, (dialog, id14) -> {
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
