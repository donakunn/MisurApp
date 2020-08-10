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

public class DatabaseCaposcout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_caposcout);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        TableRow query;
        TableRow.LayoutParams tableRowPar = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);


        TextView nickname, data, strumento, valore;

        ImageButton cancella;

        int num_query = 3;

        for(int i =0; i< num_query;i++){
            query = new TableRow(DatabaseCaposcout.this);
            query.setPadding(20,20,5,20);

            nickname = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            nickname.setLayoutParams(tableRowPar);
            nickname.setGravity(Gravity.CENTER_VERTICAL);
            nickname.setPadding(10,10,10,10);
            nickname.setTypeface(null, Typeface.BOLD);
            nickname.setText("nickname");

            query.addView(nickname);

            data = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            data.setLayoutParams(tableRowPar);
            data.setGravity(Gravity.CENTER_VERTICAL);
            data.setPadding(10,10,10,10);
            data.setTypeface(null, Typeface.BOLD);
            data.setText("data");

            query.addView(data);

            strumento = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            strumento.setLayoutParams(tableRowPar);
            strumento.setGravity(Gravity.CENTER_VERTICAL);
            strumento.setPadding(10,10,10,10);
            strumento.setTypeface(null, Typeface.BOLD);
            strumento.setText("strumento");

            query.addView(strumento);

            valore = new TextView(DatabaseCaposcout.this, null, R.style.textstyle);
            tableRowPar.weight = 1;
            valore.setLayoutParams(tableRowPar);
            valore.setGravity(Gravity.CENTER_VERTICAL);
            valore.setTypeface(null,Typeface.BOLD);
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
}