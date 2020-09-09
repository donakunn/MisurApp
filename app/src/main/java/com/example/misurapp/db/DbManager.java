package com.example.misurapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * this class is responsible for managing reading and writing data to the database
 */
public class DbManager {
    /**
     * debug tag
     */
    private static final String TAG = "DbManager";
    /**
     * Context object of the activity
     */
    private Context context;
    /**
     * SQLiteDatabase object for executing queries
     */
    private SQLiteDatabase database;
    /**
     * RecordedValuesBaseHelper object that handle creating and managing the database .
     */
    private RecordedValuesBaseHelper dbHelper;

    public DbManager(Context context) {
        this.context = context;
    }

    /**
     * Create and/or open a database that will be used for reading and writing.
     * @throws SQLException If something whent wrong during SQL operations.
     */
    public void open() throws SQLException {
        Log.d(TAG,"opening db");
        dbHelper = new RecordedValuesBaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Close the database
     */
    public void close() {
        Log.d(TAG,"closing db");
        dbHelper.close();
    }

    /**
     * Create an ContentValues object which contains data to be written on database.
     * @param tableName name of the table in which to insert values
     * @param email account email of the user
     * @param timestamp timestamp of the query
     * @param instrumentName name of the query Instrument
     * @param valueToSave registered value to be saved
     * @return ContentValues object containing the passed data
     */
    private ContentValues createContentValues(String tableName, String email, String timestamp,
                                              String instrumentName, float valueToSave) {
        Log.d(TAG,"creating ContentValues");
        ContentValues values = new ContentValues();
        if (tableName.equals(InstrumentsDBSchema.BoyscoutTable.TABLENAME)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd-MM-yyyy HH:mm:ss", Locale.ITALIAN);
            values.put(InstrumentsDBSchema.BoyscoutTable.cols.INSTRUMENTNAME, instrumentName);
            if (timestamp == null) {
                values.put(InstrumentsDBSchema.BoyscoutTable.cols.TIMESTAMP,
                        dateFormat.format(new Date()));
            } else {
                values.put(InstrumentsDBSchema.BoyscoutTable.cols.TIMESTAMP, timestamp);
            }
            values.put(InstrumentsDBSchema.BoyscoutTable.cols.VALUEREAD, valueToSave);
        } else if (tableName.equals(InstrumentsDBSchema.ScoutMasterTable.TABLENAME)) {
            values.put(InstrumentsDBSchema.ScoutMasterTable.cols.EMAIL, email);
            values.put(InstrumentsDBSchema.ScoutMasterTable.cols.TIMESTAMP, timestamp);
            values.put(InstrumentsDBSchema.ScoutMasterTable.cols.INSTRUMENTNAME, instrumentName);
            values.put(InstrumentsDBSchema.ScoutMasterTable.cols.VALUEREAD, valueToSave);
        }
        return values;
    }

    /**
     * Open database, call insertIntoTable method for performing save query and close the database
     * @param tableName name of the table in which to insert values
     * @param email account email of the user
     * @param timestamp timestamp of the query
     * @param instrumentName name of the query Instrument
     * @param value registered value to be saved
     */
    public void saveRegisteredValues(String tableName, String email, String timestamp,
                                     String instrumentName, float value) {
        Log.d(TAG,"saving RegisteredValues");
        this.open();
        insertIntoTable(tableName, email, timestamp, instrumentName, value);
        this.close();
    }

    /**
     * Create ContentValues object related to the passed data and save data on database
     * @param tableName name of the table in which to insert values
     * @param email account email of the user
     * @param timestamp timestamp of the query
     * @param instrumentName name of the query Instrument
     * @param valueRead registered value to be saved
     */
    private void insertIntoTable(String tableName, String email, String timestamp,
                                 String instrumentName, float valueRead) {
        Log.d(TAG,"insert values into table");
        ContentValues valuesToSave = createContentValues(tableName, email, timestamp,
                instrumentName, valueRead);
        database.insertOrThrow(tableName, null, valuesToSave);
    }

    /**
     * Manage operations for a multiple insert on the database
     * @param recordList List which contains records to be saved on database
     */
    public void multipleInsert(List<ScoutMasterInstrumentRecord> recordList) {
        Log.d(TAG,"multiple data insert");
        this.open();
        database.beginTransaction();
        try {
            for (ScoutMasterInstrumentRecord record : recordList) {
                ContentValues valuesToSave = createContentValues
                        (InstrumentsDBSchema.ScoutMasterTable.TABLENAME, record.getEmail(),
                                record.getTimestamp(), record.getInstrumentName(),
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

    /**
     * deletes the record corresponding to the passed id
     * @param tableName name of the table where to delete the value
     * @param idRecordToDelete id corresponding to the query to delete
     */
    public void deleteARow(String tableName, long idRecordToDelete) {
        Log.d(TAG,"deleting query " + idRecordToDelete );
        this.open();
        database.delete(tableName,
                "_id= " + idRecordToDelete, null);
        this.close();
    }

    /**
     * Read values on the boyscout table related on the passed instrument name
     * @param instrumentNameToRead name of the instrument we want to read data
     * @return list of InstrumentRecord object containing read data
     */
    public List<InstrumentRecord> readBoyscoutValuesFromDB(String instrumentNameToRead) {
        Log.d(TAG,"Reading data of " + instrumentNameToRead);
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
        cursor.close();
        this.close();
        return readQueryList;
    }
    /**
     * Read values on the ScoutMaster table
     * @return list of ScoutMasterInstrumentRecord object containing read data
     */
    public List<ScoutMasterInstrumentRecord> readScoutMasterValuesFromDB() {
        Log.d(TAG,"reading values on ScoutMaster table");
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
        cursor.close();
        this.close();
        return readQueryList;
    }

    /**
     * Create an RecordsWithEmailAndInstrumentName object which contains email of the user,
     * instrument name and a list of InstrumentRecord by reading values on the database
     * related to the instrument name
     *
     * @param instrumentNameToRead name of the instrument we want to read data
     * @return RecordsWithEmailAndInstrumentName object containing related data
     */
    public RecordsWithEmailAndInstrument recordsToSendBuilder
            (String instrumentNameToRead) {
        Log.d(TAG,"Building RecordsWithEmailAndInstrumentName of " + instrumentNameToRead);
        List<InstrumentRecord> valueRead = readBoyscoutValuesFromDB(instrumentNameToRead);
        return new RecordsWithEmailAndInstrument(getEmail(context), instrumentNameToRead,
                valueRead);
    }

    private static SharedPreferences getPrefs(Context context) {
        String PREF_NAME = "shared_pref_name";
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Read email from SharedPreferences
     * @param context Application Context
     * @return email of the user
     */
    private static String getEmail(Context context) {
        Log.d(TAG,"Reading email from preferences");
        return getPrefs(context).getString("email", "");
    }

    /**
     * clear both BoyScout and ScoutMaster tables
     */
    public void dropTables() {
        Log.d(TAG,"Clearing tables");
        this.open();
        database.execSQL("DELETE FROM " + InstrumentsDBSchema.ScoutMasterTable.TABLENAME);
        database.execSQL("DELETE FROM " + InstrumentsDBSchema.BoyscoutTable.TABLENAME);
        this.close();
    }
}