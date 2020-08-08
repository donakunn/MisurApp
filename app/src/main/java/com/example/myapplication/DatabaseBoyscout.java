package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.db.DbManager;
import com.example.myapplication.db.InstrumentRecord;

import java.util.List;

public class DatabaseBoyscout extends AppCompatActivity {

    private TableRow dbBoyScoutQueries;
    private TextView date;
    private TextView value;
    private ImageButton deleteButton;
    private TableRow.LayoutParams tableRowPar = new TableRow.LayoutParams
            (TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    private DbManager appDb;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_boyscout);
        linearLayout = findViewById(R.id.linearLayout);
        appDb = new DbManager(this);
        List<InstrumentRecord> instrumentRecordsReadFromDB = appDb.readValuesFromDB
                ("Sensor.TYPE_LIGHT");
        //da cambiare con il sensore relativo
        if (instrumentRecordsReadFromDB.isEmpty()) {
            return;
        } else {
            showBoyscoutTableValues(instrumentRecordsReadFromDB);
        }
    }

    private void showBoyscoutTableValues(List<InstrumentRecord> instrumentRecords) {
        for (final InstrumentRecord record : instrumentRecords) {
            dbBoyScoutQueries = new TableRow(DatabaseBoyscout.this);
            dbBoyScoutQueries.setPadding(20, 20, 5, 20);

            date = new TextView(DatabaseBoyscout.this);
            tableRowPar.weight = 1;
            date.setLayoutParams(tableRowPar);
            date.setGravity(Gravity.CENTER_VERTICAL);
            date.setPadding(10, 10, 10, 10);
            date.setTextAppearance(R.style.textstyle);
            date.setTypeface(null, Typeface.BOLD);
            date.setText(record.getDate());

            dbBoyScoutQueries.addView(date);

            value = new TextView(DatabaseBoyscout.this);
            tableRowPar.weight = 1;
            value.setLayoutParams(tableRowPar);
            value.setGravity(Gravity.CENTER);
            value.setTextAppearance(R.style.textstyle);
            value.setTypeface(null, Typeface.BOLD);
            value.setText(String.valueOf(record.getValue()));

            dbBoyScoutQueries.addView(value);

            deleteButton = new ImageButton
                    (DatabaseBoyscout.this, null, R.style.buttondeletestyle);
            deleteButton.setLayoutParams(tableRowPar);
            deleteButton.setImageResource(R.drawable.ic_baseline_delete_24);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation
                            (DatabaseBoyscout.this, R.anim.button_click));
                    appDb.deleteARow(record.getId());
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.cancellato), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 50);
                    toast.show();
                }
            });

            dbBoyScoutQueries.addView(deleteButton);
            linearLayout.addView(dbBoyScoutQueries);
        }
    }
}