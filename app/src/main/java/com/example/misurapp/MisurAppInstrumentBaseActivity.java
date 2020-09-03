package com.example.misurapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;

import com.example.misurapp.db.DbManager;

import java.io.IOException;

/**
 * This class extend MisurAppBaseActivity by adding layout elements and properties that are in
 * common to all Instrument activities
 */
public class MisurAppInstrumentBaseActivity extends MisurAppBaseActivity {
    /**
     * debug tag
     */
    private final String TAG = "InstrumentBaseActivity";
    /**
     * instrument name related to the activity
     */
    protected static String instrumentName;
    /**
     * DbManager object to perform database operations.
     */
    protected DbManager dbManager = new DbManager(this);

    private DriveServiceHelper mDriveServiceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_misur_app_instrument_base);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Override inherited method by adding share button
     *
     * @param menu Menu object related to the activity
     * @return boolean that describe operation result
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu");
        MenuItem share = menu.findItem(R.id.action_archivio);
        share.setVisible(true);
        return true;
    }

    /**
     * Override inherited method by adding restore and archive button
     *
     * @param item MenuItem object related to the activity
     * @return boolean that describe operation result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionItemSelected");
        int id = item.getItemId();

        if (id == R.id.action_cambio_lingua) {
            String[] listItems = new String[]{getResources().getString(R.string.lingua_inglese),
                    getResources().getString(R.string.lingua_spagnola), getResources().
                    getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
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
        if (id == R.id.action_ripristino) { //ripristino
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Vuoi ripristinare le misure dell'ultimo salvataggio fatte sul tuo Google Drive?");
            alertDialog.setPositiveButton(R.string.Si, (dialog, id1) -> {
                //codice di ripristino
                try {
                    mDriveServiceHelper = getGDriveServiceHelper();
                    mDriveServiceHelper.restoreFile(dbManager,instrumentName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            alertDialog.setNegativeButton(R.string.No, (dialog, id12) -> {
            });
            AlertDialog mDialog = alertDialog.create();
            alertDialog.show();
            return true;
        }//fine ripristino

        if (id == R.id.action_archivio) {
            Intent intent = new Intent(this, BoyscoutDBValuesActivity.class);
            intent.putExtra("sensorName", instrumentName);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}