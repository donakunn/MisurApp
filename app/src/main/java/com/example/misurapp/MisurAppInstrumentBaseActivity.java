package com.example.misurapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.example.misurapp.db.DbManager;
import com.example.misurapp.utility.SaveAndFeedback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Locale;

public class MisurAppInstrumentBaseActivity extends MisurAppBaseActivity {

    protected static String instrumentName;
    protected DbManager dbManager = new DbManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_misur_app_instrument_base);
    }

    protected void setAppLocale(String localCode) {
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
        MenuItem condividi = menu.findItem(R.id.action_archivio);
        condividi.setVisible(true);
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
                    getResources().getString(R.string.lingua_spagnola), getResources().
                    getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setSingleChoiceItems(listItems, -1,
                    new DialogInterface.OnClickListener() {
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


        if (id == R.id.action_archivio) {
            Intent intent = new Intent(this, BoyscoutDBValuesActivity.class);
            intent.putExtra("sensorName", instrumentName);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}