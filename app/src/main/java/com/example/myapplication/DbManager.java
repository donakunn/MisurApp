package com.example.myapplication;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContentResolver;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        values.put( KEY_NAME, nome);
        values.put( "DataOra", dateFormat.format(date)); //////////////
        for (int i = 0; i < valori.length; i++) {
            values.put("valore"+ i, valori[i]);
        }
        return values;
    }



    //crea la tabella inizializzando i valori
    public long createTabella(String nome, float[] valori) {
        ContentValues initialValues = createContentValues(nome, valori);
        /*try {
            backupDB();///////////////////////////////// DA CAMBIARE
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            restoreDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
    }

    //delete a table
    public void deleteTable(long _id) {
        database.delete(DATABASE_TABLE, KEY_NAME + "=" + _id, null);
    }

    //backup database
    public void backupDB() throws IOException {

        final String inFileName = context.getDatabasePath("mydatabase.db").getPath(); //DA CAMBIARE!!!
        //in alternativa
        //final String inFileName = "/data/data/com.example.myapplication/databases/myapplication.db";
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        File folderToSaveDB = new File(Environment.getExternalStorageDirectory()+File.separator + "SensorAppDBBackup");

        if (!folderToSaveDB.exists()) {
            folderToSaveDB.mkdirs();
        }
        // Open the empty db as the output stream
        OutputStream output = new FileOutputStream(folderToSaveDB.getPath()+ "/database_copy.db");

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer))>0){
            output.write(buffer, 0, length);
        }

        // Close the streams
        output.flush();
        output.close();
        fis.close();
    }
    //backup database
    public void restoreDB() throws IOException {

        final String inFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorAppDBBackup/database_copy.db"; //DA CAMBIARE!!!
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);
        // Open the empty db as the output stream
        OutputStream output = new FileOutputStream(Environment.getDataDirectory()+ "/data/com.example.myapplication/databases/mydatabase.db");

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer))>0){
            output.write(buffer, 0, length);
        }

        // Close the streams
        output.flush();
        output.close();
        fis.close();
    }


}