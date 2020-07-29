package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*Questa classe servirà a gestire la nascita e l’aggiornamento del database
su memoria fisica e a recuperare un riferimento all’oggetto SQLiteDatabase,
usato come accesso ai dati;*/

public class RecordedValuesBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "misurapp.db";
    private static final int DATABASE_VERSION = 1;
    //private SQLiteDatabase db;
    // Lo statement SQL di creazione del database
    private static final String DATABASE_CREATE = "create table valoriRegistrati" +
            "(_id integer primary key autoincrement,NomeSensore text not null,DataOra date," +
            "valore0 float(24),valore1 float(24), valore2 float(24), valore3 float(24), " +
            "valore4 float(24), valore5 float(24));";

    // Costruttore
    public RecordedValuesBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //db = getWritableDatabase();
    }

    // Questo metodo viene chiamato durante la creazione del database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + InstrumentsDBSchema.BoyscoutTable.TABLENAME +
                " ("+ " _id integer primary key autoincrement, " +
                InstrumentsDBSchema.BoyscoutTable.cols.INSTRUMENTNAME + " text not null, " +
                "DataOra date, " + InstrumentsDBSchema.BoyscoutTable.cols.VALUEREAD +
                " float(24));" );
        database.execSQL("create table " + InstrumentsDBSchema.ScoutMasterTable.TABLENAME +
                " ("+ " _id integer primary key autoincrement, " +
                InstrumentsDBSchema.ScoutMasterTable.cols.BOYSCOUTNICKNAME + " text not null, "+
                InstrumentsDBSchema.ScoutMasterTable.cols.TIMESTAMP + " date, "+
                InstrumentsDBSchema.ScoutMasterTable.cols.INSTRUMENTNAME + " text not null, " +
                InstrumentsDBSchema.ScoutMasterTable.cols.VALUEREAD + " float(24));" );
    }

    // Questo metodo viene chiamato durante l'upgrade del database, ad esempio quando viene
    // incrementato il numero di versione
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }
}
