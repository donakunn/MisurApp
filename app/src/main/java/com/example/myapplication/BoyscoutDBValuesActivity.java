package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.db.DbManager;
import com.example.myapplication.db.InstrumentRecord;

import java.util.List;
import java.util.Objects;

public class BoyscoutDBValuesActivity extends AppCompatActivity {

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
                (Objects.requireNonNull(getIntent().getExtras()).getString("sensorName"));
        //da cambiare con il sensore relativo
        if (instrumentRecordsReadFromDB.isEmpty()) {
            return;
        } else {
            showBoyscoutTableValues(instrumentRecordsReadFromDB);
        }
    }

    private void showBoyscoutTableValues(List<InstrumentRecord> instrumentRecords) {
        for (final InstrumentRecord record : instrumentRecords) {
            TableRow dbBoyScoutQuery = new TableRow(BoyscoutDBValuesActivity.this);
            dbBoyScoutQuery.setPadding(20, 20, 5, 20);

            TextView date = new TextView(BoyscoutDBValuesActivity.this);
            tableRowPar.weight = 1;
            date.setLayoutParams(tableRowPar);
            date.setGravity(Gravity.CENTER_VERTICAL);
            date.setPadding(10, 10, 10, 10);
            //date.setTextAppearance(R.style.textstyle);
            date.setTypeface(null, Typeface.BOLD);
            date.setText(record.getDate());

            dbBoyScoutQuery.addView(date);

            TextView value = new TextView(BoyscoutDBValuesActivity.this);
            tableRowPar.weight = 1;
            value.setLayoutParams(tableRowPar);
            value.setGravity(Gravity.CENTER);
            //value.setTextAppearance(R.style.textstyle);
            value.setTypeface(null, Typeface.BOLD);
            value.setText(String.valueOf(record.getValue()));

            dbBoyScoutQuery.addView(value);

            ImageButton deleteButton = new ImageButton
                    (BoyscoutDBValuesActivity.this, null, R.style.buttondeletestyle);
            deleteButton.setLayoutParams(tableRowPar);
            deleteButton.setImageResource(R.drawable.ic_baseline_delete_24);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   actionsOnDeleteButtonPress(v,record);
                }
            });

            dbBoyScoutQuery.addView(deleteButton);
            linearLayout.addView(dbBoyScoutQuery);
        }
    }

    private void actionsOnDeleteButtonPress(View v,InstrumentRecord record) {
        v.startAnimation(AnimationUtils.loadAnimation
                (BoyscoutDBValuesActivity.this, R.anim.button_click));
        appDb.deleteARow(record.getId());
        Toast toast = Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.cancellato), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.show();
        deleteAndRedraw(v);
    }

    private void deleteAndRedraw(View v) {
        final View row = (View) v.getParent();
        onDeleteAnimation(row);
        linearLayout.postDelayed(new Runnable() {
            public void run() {
                ViewGroup container = ((ViewGroup)row.getParent());
                container.removeView(row);
                container.invalidate();
            }
        }, 500);
    }

    private void onDeleteAnimation(final View row) {
        Animation fadeout = new AlphaAnimation(1.f, 0.f);
        fadeout.setDuration(500);
        fadeout.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation){}

            @Override
            public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation){
                row.setVisibility(View.GONE);
            }
        });
        row.startAnimation(fadeout);
    }

}