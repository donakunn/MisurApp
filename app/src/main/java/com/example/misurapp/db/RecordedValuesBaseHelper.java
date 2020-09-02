package com.example.misurapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class will be used to manage the creation and upgrade of the database
 * and retrieve a reference to the SQLiteDatabase object.
 * Used as data access
 */
public class RecordedValuesBaseHelper extends SQLiteOpenHelper {
    /**
     * Name of the Database
     */
    private static final String DATABASE_NAME = "misurapp.db";

    /**
     * Version of the Database
     */
    private static final int DATABASE_VERSION = 1;

    public RecordedValuesBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create tables if they not exists on the database.
     *
     * @param database SQLiteDatabase object in which to create tables.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        String TAG = "RecordedValBaseHelper";
        Log.d(TAG, "Creating tables on db");
        database.execSQL("create table " + InstrumentsDBSchema.BoyscoutTable.TABLENAME +
                " (" + " _id integer primary key autoincrement, " +
                InstrumentsDBSchema.BoyscoutTable.cols.INSTRUMENTNAME + " text not null, " +
                InstrumentsDBSchema.ScoutMasterTable.cols.TIMESTAMP + " date, " +
                InstrumentsDBSchema.BoyscoutTable.cols.VALUEREAD + " float(12));");
        database.execSQL("create table " + InstrumentsDBSchema.ScoutMasterTable.TABLENAME +
                " (" + " _id integer primary key autoincrement, " +
                InstrumentsDBSchema.ScoutMasterTable.cols.EMAIL + " text not null, " +
                InstrumentsDBSchema.ScoutMasterTable.cols.TIMESTAMP + " date, " +
                InstrumentsDBSchema.ScoutMasterTable.cols.INSTRUMENTNAME + " text not null, " +
                InstrumentsDBSchema.ScoutMasterTable.cols.VALUEREAD + " float(12));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    }
}
