package com.example.misurapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

//da implementare metodi tabella caposcout
public class DbManager {

    //private static final String LOG_TAG = DbManager.class.getSimpleName();
    private Context context;
    private SQLiteDatabase database;
    private RecordedValuesBaseHelper dbHelper;
    private SharedPreferences prefs;
    private static String PREF_NAME = "shared_pref_name";

    //Costruttore
    public DbManager(Context context) {
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

    private ContentValues createContentValues(String tableName, String email, String timestamp,
                                              String instrumentName, float valueToSave) {
        ContentValues values = new ContentValues();
        if (tableName.equals(InstrumentsDBSchema.BoyscoutTable.TABLENAME)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd-MM-yyyy HH:mm:ss", Locale.ITALIAN);
            values.put(InstrumentsDBSchema.BoyscoutTable.cols.INSTRUMENTNAME, instrumentName);
            values.put(InstrumentsDBSchema.BoyscoutTable.cols.TIMESTAMP,
                    dateFormat.format(new Date()));
            values.put(InstrumentsDBSchema.BoyscoutTable.cols.VALUEREAD, valueToSave);
        } else if (tableName.equals(InstrumentsDBSchema.ScoutMasterTable.TABLENAME)) {
            values.put(InstrumentsDBSchema.ScoutMasterTable.cols.EMAIL, email);
            values.put(InstrumentsDBSchema.ScoutMasterTable.cols.TIMESTAMP, timestamp);
            values.put(InstrumentsDBSchema.ScoutMasterTable.cols.INSTRUMENTNAME, instrumentName);
            values.put(InstrumentsDBSchema.ScoutMasterTable.cols.VALUEREAD, valueToSave);
        }
        return values;
    }


    public void saveRegisteredValues(String tableName, String email, String timestamp,
                                     String instrumentName, float value) {
        this.open();
        insertIntoTable(tableName, email, timestamp, instrumentName, value);
        this.close();
    }

    //impacchetta valori in un oggetto di ContentValues e li salva sul db

    private long insertIntoTable(String tableName, String email, String timestamp,
                                 String instrumentName, float valueRead) {
        ContentValues valuesToSave = createContentValues(tableName, email, timestamp,
                instrumentName, valueRead);
        return database.insertOrThrow(tableName, null, valuesToSave);
    }

    public void multipleInsert(List<ScoutMasterInstrumentRecord> recordList) {
        this.open();
        database.beginTransaction();
        try {
            for (ScoutMasterInstrumentRecord record : recordList) {
                ContentValues valuesToSave = createContentValues
                        (InstrumentsDBSchema.ScoutMasterTable.TABLENAME, record.getEmail(),
                                record.getDate(), record.getInstrumentName(),
                                record.getValue());
                database.insertOrThrow(InstrumentsDBSchema.ScoutMasterTable.TABLENAME,
                        null, valuesToSave);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            this.close();
        }

    }

    //cancella la query dal db il cui id è uguale a quello passato in input
    public void deleteARow(String tableName, long idRecordToDelete) {
        this.open();
        database.delete(tableName,
                "_id= " + idRecordToDelete, null);
        this.close();
    }

    //backup database
    public void backupDB() throws IOException {

        final String inFileName = context.getDatabasePath("misurapp.db").getPath();
        //in alternativa
        //final String inFileName =
        // "/data/data/com.example.myapplication/databases/myapplication.db";
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        File folderToSaveDB = new File(Environment.getExternalStorageDirectory() +
                File.separator + "MisurAppBackup");

        if (!folderToSaveDB.exists()) {
            folderToSaveDB.mkdirs();
        }
        // Open the empty db as the output stream
        OutputStream output = new FileOutputStream(folderToSaveDB.getPath() +
                "/MisurApp_Database_copy.db");

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
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
        OutputStream output = new FileOutputStream(Environment.getDataDirectory() +
                "/data/com.example.misurapp/databases/misurapp.db");

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        // Close the streams
        output.flush();
        output.close();
        fis.close();
    }

    public List<InstrumentRecord> readBoyscoutValuesFromDB(String instrumentNameToRead) {
        List<InstrumentRecord> readQueryList = new LinkedList<>();
        String selectQuery = "SELECT * FROM " + InstrumentsDBSchema.BoyscoutTable.TABLENAME +
                " WHERE instrumentName = '" + instrumentNameToRead + "';";
        this.open();
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                InstrumentRecord queryRead = new InstrumentRecord(cursor.getLong(0),
                        cursor.getString(2), Float.parseFloat(cursor.getString(3)));

                // Adding query to list
                readQueryList.add(queryRead);
            } while (cursor.moveToNext());

        }
        this.close();
        return readQueryList;
    }

    public List<ScoutMasterInstrumentRecord> readScoutMasterValuesFromDB() {
        List<ScoutMasterInstrumentRecord> readQueryList = new LinkedList<>();
        String selectQuery = "SELECT * FROM " + InstrumentsDBSchema.ScoutMasterTable.TABLENAME + ";";
        this.open();
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
                do {
                    ScoutMasterInstrumentRecord queryRead = new ScoutMasterInstrumentRecord
                            (cursor.getLong(0), cursor.getString(2),
                                    Float.parseFloat(cursor.getString(4)), cursor.getString(1),
                                    cursor.getString(3));

                    // Adding query to list
                    readQueryList.add(queryRead);
                } while (cursor.moveToNext());
        }
        this.close();
        return readQueryList;
    }

    public RecordsWithEmailAndInstrumentName recordsToSendBuilder
            (String instrumentNameToRead) {
        List<InstrumentRecord> valueRead = readBoyscoutValuesFromDB(instrumentNameToRead);
        return new RecordsWithEmailAndInstrumentName(getEmail(context), instrumentNameToRead, valueRead);
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String getEmail(Context context) {
        return getPrefs(context).getString("email", "");
    }
}