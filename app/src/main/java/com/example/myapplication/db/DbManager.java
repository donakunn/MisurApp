package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
//da implementare metodi tabella caposcout
public class DbManager {

    //private static final String LOG_TAG = DbManager.class.getSimpleName();
    private Context context;
    private SQLiteDatabase database;
    private RecordedValuesBaseHelper dbHelper;
    // Database fields
    private static final String BOYSCOUT_DATABASE_TABLE = "valuesRecordedByBoyscout";
    private static final String SCOUTMASTER_DATABASE_TABLE = "valuesReceivedByBoyscout";

    //Costruttore
    public DbManager(Context context)
    {
        this.context = context;
    }
    /*open() e close() sono metodi che useremo ogni volta che dovremmo comunicare con il database:
    sarà sufficiente chiamare questi metodi per lavorare con il database.*/

    public DbManager open() throws SQLException {
        dbHelper = new RecordedValuesBaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }
    /*il metodo createContentValues() ha un compito molto semplice: memorizzare un insieme di valori
    che il ContentResolver può processare per fornire l’accesso applicativo
    al modello del contenuto.*/

    private ContentValues createContentValues(String instrumentName, float valueToSave) {
        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss", Locale.ITALIAN);
        values.put(InstrumentsDBSchema.BoyscoutTable.cols.INSTRUMENTNAME, instrumentName);
        values.put("Timestamp", dateFormat.format(new Date()));
        values.put(InstrumentsDBSchema.BoyscoutTable.cols.VALUEREAD, valueToSave);
        return values;
    }



    //impacchetta valori in un oggetto di ContentValues e li scrive sul db
    public long insertIntoTable(String instrumentName, float valueRead) {
        ContentValues valuesToSave = createContentValues(instrumentName, valueRead);
        return database.insertOrThrow(BOYSCOUT_DATABASE_TABLE, null, valuesToSave);
    }

    //cancella la query dal db il cui id è uguale a quello passato in input
    public void deleteARow(long _id) {
        database.delete(BOYSCOUT_DATABASE_TABLE,
                InstrumentsDBSchema.BoyscoutTable.cols.INSTRUMENTNAME +
                        "=" + _id, null);
    }

    //backup database
    public void backupDB() throws IOException {

        final String inFileName = context.getDatabasePath("misurapp.db").getPath();
        //in alternativa
        //final String inFileName =
        // "/data/data/com.example.myapplication/databases/myapplication.db";
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        File folderToSaveDB = new File(Environment.getExternalStorageDirectory()+
                File.separator + "MisurAppBackup");

        if (!folderToSaveDB.exists()) {
            folderToSaveDB.mkdirs();
        }
        // Open the empty db as the output stream
        OutputStream output = new FileOutputStream(folderToSaveDB.getPath()+
                "/MisurApp_Database_copy.db");

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
    //restore database
    public void restoreDB() throws IOException {

        final String inFileName = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/MisurAppBackup/MisurApp_Database_copy.db";
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);
        // Open the empty db as the output stream
        OutputStream output = new FileOutputStream(Environment.getDataDirectory()+
                "/data/com.example.myapplication/databases/misurapp.db");

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

    List<InstrumentRecord> leggiValoriDaDB(String instrumentNameToRead) {
        List<InstrumentRecord> listaQueryLette = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + BOYSCOUT_DATABASE_TABLE +
                " WHERE instrumentName = '" + instrumentNameToRead + "';";
        //SQLiteDatabase db = this.getWritableDatabase(); serve?
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                float savedValue=Float.parseFloat(cursor.getString(3));
                InstrumentRecord queryRead = new InstrumentRecord(
                        cursor.getString(2),savedValue);

                // Adding query to list
                listaQueryLette.add(queryRead);
            } while (cursor.moveToNext());
        }
        return listaQueryLette;
    }
}