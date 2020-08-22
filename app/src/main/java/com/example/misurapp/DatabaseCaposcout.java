package com.example.misurapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;

public class DatabaseCaposcout extends AppCompatActivity {

    String [] listItems;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_caposcout);

        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        TableRow query;
        TableRow.LayoutParams tableRowPar = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);


        TextView nickname, data, strumento, valore;

        ImageButton cancella;

        int num_query = 3;

        for (int i = 0; i < num_query; i++) {
            query = new TableRow(DatabaseCaposcout.this);
            query.setPadding(20, 20, 5, 20);

            nickname = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            nickname.setLayoutParams(tableRowPar);
            nickname.setGravity(Gravity.CENTER_VERTICAL);
            nickname.setPadding(10, 10, 10, 10);
            nickname.setTypeface(null, Typeface.BOLD);
            nickname.setText("nickname");

            query.addView(nickname);

            data = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            data.setLayoutParams(tableRowPar);
            data.setGravity(Gravity.CENTER_VERTICAL);
            data.setPadding(10, 10, 10, 10);
            data.setTypeface(null, Typeface.BOLD);
            data.setText("data");

            query.addView(data);

            strumento = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            strumento.setLayoutParams(tableRowPar);
            strumento.setGravity(Gravity.CENTER_VERTICAL);
            strumento.setPadding(10, 10, 10, 10);
            strumento.setTypeface(null, Typeface.BOLD);
            strumento.setText("strumento");

            query.addView(strumento);

            valore = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            valore.setLayoutParams(tableRowPar);
            valore.setGravity(Gravity.CENTER_VERTICAL);
            valore.setTypeface(null, Typeface.BOLD);
            valore.setText("valore");

            query.addView(valore);

            cancella = new ImageButton(DatabaseCaposcout.this, null, R.style.buttondeletestyle);
            cancella.setLayoutParams(tableRowPar);
            cancella.setImageResource(R.drawable.ic_baseline_delete_24);

            cancella.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(DatabaseCaposcout.this, R.anim.button_click));

                }
            });

            query.addView(cancella);
            linearLayout.addView(query);

        }
    }

    private void setAppLocale(String localCode){
        Resources res = getResources();
        DisplayMetrics dm =res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            conf.setLocale(new Locale(localCode.toLowerCase()));
        }else{
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
    public boolean onPrepareOptionsMenu(Menu menu)
    {
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
            listItems = new String[] {getResources().getString(R.string.lingua_inglese), getResources().getString(R.string.lingua_spagnola), getResources().getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(DatabaseCaposcout.this);
            mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = getIntent();


                    switch (which){

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