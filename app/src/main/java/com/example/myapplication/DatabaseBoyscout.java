package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DatabaseBoyscout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_boyscout);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        TableRow query;
        TableRow.LayoutParams tableRowPar = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);


        TextView data;
        TextView valore;
        ImageButton cancella;

        int num_query = 3;

        for(int i =0; i< num_query;i++){
            query = new TableRow(DatabaseBoyscout.this);
            query.setPadding(20,20,5,20);

            data = new TextView(DatabaseBoyscout.this);
            tableRowPar.weight = 1;
            data.setLayoutParams(tableRowPar);
            data.setGravity(Gravity.CENTER_VERTICAL);
            data.setPadding(10,10,10,10);
            data.setTextAppearance(R.style.textstyle);
            data.setTypeface(null,Typeface.BOLD);
            data.setText("data");

            query.addView(data);

            valore = new TextView(DatabaseBoyscout.this);
            tableRowPar.weight = 1;
            valore.setLayoutParams(tableRowPar);
            valore.setGravity(Gravity.CENTER);
            valore.setTextAppearance(R.style.textstyle);
            valore.setTypeface(null,Typeface.BOLD);
            valore.setText("valore");

            query.addView(valore);

            cancella = new ImageButton(DatabaseBoyscout.this, null, R.style.buttondeletestyle);
            cancella.setLayoutParams(tableRowPar);
            cancella.setImageResource(R.drawable.ic_baseline_delete_24);

            cancella.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(DatabaseBoyscout.this, R.anim.button_click));

                }
            });

            query.addView(cancella);
            linearLayout.addView(query);

        }
    }//fine onCreate
}