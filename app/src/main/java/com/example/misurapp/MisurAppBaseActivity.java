package com.example.misurapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;
import java.util.Objects;

public class MisurAppBaseActivity extends AppCompatActivity {

    protected SharedPreferences prefs;
    protected SharedPreferences.Editor editor;

    protected String currentLangCode;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();

        currentLangCode = prefs.getString("Language", "it");
        loadLocale();

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
            String[] listItems = new String[]{getResources().getString(R.string.lingua_inglese), getResources().getString(R.string.lingua_spagnola), getResources().getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
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


        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        if (!currentLangCode.equals(getResources().getConfiguration().locale.getLanguage())) {
            currentLangCode = getResources().getConfiguration().locale.getLanguage();
            recreate();
        }
    }

    public void loadLocale() {
        String langPref = "Language";
        String language = prefs.getString(langPref, "");
        changeLang(Objects.requireNonNull(language));
    }

    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources()
                .getDisplayMetrics());

    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        editor.putString(langPref, lang);
        editor.apply();
    }
}