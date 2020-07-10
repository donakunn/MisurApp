package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContentResolver;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DbManager
{
    private static final String LOG_TAG = DbManager.class.getSimpleName();
    private Context context;
    private SQLiteDatabase database;
    private DBhelper dbHelper;
    // Database fields
    private static final String DATABASE_TABLE = "valoriRegistrati";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "NomeSensore";
    public static final String KEY_0 = "valore0";
    public static final String KEY_1 = "valore1";
    public static final String KEY_2 = "valore2";
    public static final String KEY_3 = "valore3";
    public static final String KEY_4 = "valore4";
    public static final String KEY_5 = "valore5";


    //Costruttore
    public DbManager(Context context)
    {
        this.context = context;
    }
    /*open() e close() sono metodi che useremo ogni volta che dovremmo comunicare con il database:
    sarà sufficiente chiamare questi metodi per lavorare con il database.*/

    public DbManager open() throws SQLException {
        dbHelper = new DBhelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }
    /*il metodo createContentValues() ha un compito molto semplice: memorizzare un insieme di valori
    che il ContentResolver può processare per fornire l’accesso applicativo
    al modello del contenuto.*/

    private ContentValues createContentValues(String nome, float[] valori) {
        ContentValues values = new ContentValues();
        values.put( KEY_NAME, nome);
        for (int i = 0; i < valori.length; i++) {
            values.put("valore"+ i, valori[i]);
        }
        return values;
    }

    //crea la tabella inizializzando i valori
    public long createTabella(String nome, float[] valori) {
        ContentValues initialValues = createContentValues(nome, valori);
        return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
    }

    //delete a table
    public void deleteTable(long _id) {
        database.delete(DATABASE_TABLE, KEY_NAME + "=" + _id, null);
    }


}