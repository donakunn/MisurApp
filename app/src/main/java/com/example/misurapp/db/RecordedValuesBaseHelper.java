package com.example.misurapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*Questa classe servirà a gestire la nascita e l’aggiornamento del database
su memoria fisica e a recuperare un riferimento all’oggetto SQLiteDatabase,
usato come accesso ai dati;*/

public class RecordedValuesBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "misurapp.db";
    private static final int DATABASE_VERSION = 1;

    // Costruttore
    public RecordedValuesBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Questo metodo viene chiamato durante la creazione del database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + InstrumentsDBSchema.BoyscoutTable.TABLENAME +
                " ("+ " _id integer primary key autoincrement, " +
                InstrumentsDBSchema.BoyscoutTable.cols.INSTRUMENTNAME + " text not null, " +
                InstrumentsDBSchema.ScoutMasterTable.cols.TIMESTAMP + " date, "+
                InstrumentsDBSchema.BoyscoutTable.cols.VALUEREAD + " float(12));" );
        database.execSQL("create table " + InstrumentsDBSchema.ScoutMasterTable.TABLENAME +
                " ("+ " _id integer primary key autoincrement, " +
                InstrumentsDBSchema.ScoutMasterTable.cols.EMAIL + " text not null, "+
                InstrumentsDBSchema.ScoutMasterTable.cols.TIMESTAMP + " date, "+
                InstrumentsDBSchema.ScoutMasterTable.cols.INSTRUMENTNAME + " text not null, " +
                InstrumentsDBSchema.ScoutMasterTable.cols.VALUEREAD + " float(12));" );
    }

    // Questo metodo viene chiamato durante l'upgrade del database, ad esempio quando viene
    // incrementato il numero di versione
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }
}
