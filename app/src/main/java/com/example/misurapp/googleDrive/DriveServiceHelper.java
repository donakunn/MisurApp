package com.example.misurapp.googleDrive;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.misurapp.activities.BoyscoutDBValuesActivity;
import com.example.misurapp.activities.MisurAppInstrumentBaseActivity;
import com.example.misurapp.R;
import com.example.misurapp.activities.ScoutMasterDbActivity;
import com.example.misurapp.db.DbManager;
import com.example.misurapp.db.InstrumentRecord;
import com.example.misurapp.db.ScoutMasterInstrumentRecord;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class perform and manage read/write operations on Drive files using the REST API
 */
public class DriveServiceHelper {
    /**
     * Executor object that executes submitted Runnable tasks
     */
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    /**
     * Drive object to perform read and write operations on Google Drive
     */
    private final Drive mDriveService;

    /**
     * debug tag
     */
    private static final String TAG = "GoogleDrive";

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    /**
     * This method create and save files in GDrive for boyscout type of user
     *
     * @param dbManager DbManager object to handle operations on database.
     * @param activity  Activity in which we are performing saves
     * @param nomeFile  Name of the file we are creating/changing
     */
    public void createAndSaveFile(DbManager dbManager, BoyscoutDBValuesActivity activity,
                                  String nomeFile) throws IOException {
        Log.d(TAG, "Creating a file.");
        StringBuilder fileContent = new StringBuilder();
        //setta contenuto del file
        List<InstrumentRecord> instrumentRecordsReadFromDB = dbManager.readBoyscoutValuesFromDB
                (nomeFile);
        for (final InstrumentRecord record : instrumentRecordsReadFromDB) {
            fileContent.append(" Data: ").append(record.getTimestamp()).append(" Misura: ")
                    .append(record.getValue()).append("\n");
        }
        String finalFileContent = fileContent.toString();
        if (finalFileContent.isEmpty()) {//se il contenuto è vuoto
            //feedback
            Toast toast = Toast.makeText(activity, R.string.nessuna_misura_da_salvare, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 300);
            toast.show();
        } else {//se il contenuto esiste
            //salva file
            getIdentificativo(nomeFile)
                    .addOnSuccessListener(fileId -> {
                        saveFile(fileId, finalFileContent);
                        //feedback
                        Toast toast = Toast.makeText(activity, R.string.success_salvataggio_drive,
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 300);
                        toast.show();
                    })
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't create file.", exception));
        }
    }

    /**
     * This method create and save files in GDrive for Scoutmaster type of user
     *
     * @param dbManager DbManager object to handle operations on database.
     * @param activity  Activity in which we are performing saves
     */
    public void createAndSaveFile(DbManager dbManager, ScoutMasterDbActivity activity)
            throws IOException {
        Log.d(TAG, "Creating a file.");
        StringBuilder fileContent = new StringBuilder();
        //setta contenuto del file
        List<ScoutMasterInstrumentRecord> instrumentRecordsReadFromDB =
                dbManager.readScoutMasterValuesFromDB();
        for (final ScoutMasterInstrumentRecord record : instrumentRecordsReadFromDB) {
            fileContent.append("Email: ").append(record.getEmail()).append(" Strumento: ")
                    .append(record.getInstrumentName()).append(" Data: ").append(record
                    .getTimestamp()).append(" Misura: ")
                    .append(record.getValue()).append("\n");
        }
        String finalFileContent = fileContent.toString();
        if (finalFileContent.isEmpty()) {//se il contenuto è vuoto
            //feedback
            Toast toast = Toast.makeText(activity, R.string.nessuna_misura_da_salvare, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 300);
            toast.show();
        } else {//se il contenuto esiste
            //salva file
            getIdentificativo("misure ricevute")
                    .addOnSuccessListener(fileId -> {
                        saveFile(fileId, finalFileContent);
                        //feedback
                        Toast toast = Toast.makeText(activity,
                                R.string.success_salvataggio_drive, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 300);
                        toast.show();
                    })
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't create file.", exception));
        }
    }

    /**
     * method for reverting values from google Drive to use for scout master
     *
     * @param dbManager DbManager object to handle operations on database.
     * @param activity  Activity in which we are performing restore
     */
    public void restoreFile(DbManager dbManager, ScoutMasterDbActivity activity)
            throws IOException {
        Log.d(TAG,"restoring values on ScoutMaster");
        getIdentificativo("misure ricevute")
                .addOnSuccessListener(fileId -> readFile(fileId)
                        .addOnSuccessListener(fileContent -> {
                            LinearLayout progressBar = activity.findViewById(R.id.llProgressBar);
                            List<ScoutMasterInstrumentRecord> instrumentRecordsReadFromDB =
                                    dbManager.readScoutMasterValuesFromDB();
                            boolean control;
                            boolean valoriRipristino = false;
                            String email, strumento, timestamp;
                            String[] lines = fileContent.split("\n");
                            String[] words;
                            if (!fileContent.isEmpty()) {//controlla che il file non sia vuoto
                                if (!instrumentRecordsReadFromDB.isEmpty()) {//controlla che nel
                                    // db ci siano salvati dei valori
                                    for (String string : lines) {
                                        words = string.split(" ");
                                        email = words[1];
                                        strumento = words[3];
                                        timestamp = words[5] + " " + words[6];
                                        control = false;
                                        //controlla che il valore non sia già salvato sul
                                        // database controllando il timestamp ed email
                                        for (final ScoutMasterInstrumentRecord record :
                                                instrumentRecordsReadFromDB) {
                                            if ((record.getTimestamp().contentEquals(timestamp)) &&
                                                    (record.getEmail().contentEquals(email))) {
                                                control = true;
                                            }
                                        }
                                        if (!control) {
                                            dbManager.saveRegisteredValues(
                                                    "valuesReceivedByBoyscout", email,
                                                    timestamp, strumento,
                                                    Float.parseFloat(words[8]));
                                            valoriRipristino = true;
                                        }
                                    }
                                } else {//se nel db non ci sono valori, i dati del file vengono
                                    // aggiunti automaticamente
                                    for (String string : lines) {
                                        words = string.split(" ");
                                        email = words[1];
                                        strumento = words[3];
                                        timestamp = words[5] + " " + words[6];
                                        dbManager.saveRegisteredValues(
                                                "valuesReceivedByBoyscout", email,
                                                timestamp, strumento, Float.parseFloat(words[8]));
                                        valoriRipristino = true;
                                    }
                                }
                                activity.showRecordsOnScoutMasterActivity
                                        (dbManager.readScoutMasterValuesFromDB());
                                progressBar.setVisibility(View.GONE);
                                if (valoriRipristino) {
                                    //feedback
                                    Toast toast = Toast.makeText(activity,
                                            R.string.misure_ripristinate, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, 300);
                                    toast.show();
                                } else {
                                    //feedback
                                    Toast toast = Toast.makeText(activity,
                                            R.string.nessuna_misura_da_ripristinare,
                                            Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, 300);
                                    toast.show();
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                //feedback
                                Toast toast = Toast.makeText(activity, R.string.nessuna_misura_nel_file,
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM, 0, 300);
                                toast.show();
                            }
                        }));
    }

    /**
     * Method for reverting values from google Drive to use for activity instruments
     *
     * @param dbManager DbManager object to handle operations on database.
     * @param activity  Activity in which we are performing restore
     * @param nomeFile  name of the file from which to restore values
     */
    public void restoreFile(DbManager dbManager, MisurAppInstrumentBaseActivity activity,
                            String nomeFile) throws IOException {
        Log.d(TAG,"restoring values on Instrument Activity");
        getIdentificativo(nomeFile)
                .addOnSuccessListener(fileId -> readFile(fileId)
                        .addOnSuccessListener(fileContent -> {
                            List<InstrumentRecord> instrumentRecordsReadFromDB =
                                    dbManager.readBoyscoutValuesFromDB(nomeFile);
                            String timestamp;
                            boolean control;
                            boolean valoriRipristino = false;
                            String[] lines = fileContent.split("\n");
                            String[] words;
                            if (!fileContent.isEmpty()) { //controlla che il file non sia vuoto
                                if (!instrumentRecordsReadFromDB.isEmpty()) {//controlla che
                                    // nel db ci siano salvati dei valori
                                    for (String string : lines) {
                                        words = string.split(" ");
                                        timestamp = words[2] + " " + words[3];
                                        control = false;
                                        //controlla che il valore non sia già salvato sul
                                        // database controllando il timestamp
                                        for (final InstrumentRecord record :
                                                instrumentRecordsReadFromDB) {
                                            if (record.getTimestamp().contentEquals(timestamp)) {
                                                control = true;
                                            }
                                        }
                                        if (!control) {
                                            dbManager.saveRegisteredValues
                                                    ("valuesRecordedByBoyscout",
                                                            null, timestamp,
                                                            nomeFile,
                                                            Float.parseFloat(words[5]));
                                            valoriRipristino = true;//indica che c'è stato un
                                            // ripristino dei valori, altrimenti i valori del
                                            // file sono già visualizzati
                                        }
                                    }
                                } else {
                                    for (String string : lines) {
                                        words = string.split(" ");
                                        timestamp = words[2] + " " + words[3];
                                        dbManager.saveRegisteredValues(
                                                "valuesRecordedByBoyscout",
                                                null,
                                                timestamp,
                                                nomeFile, Float.parseFloat(words[5]));
                                        valoriRipristino = true;//indica che c'è stato una
                                        // ripristino dei valori, altrimenti i valori del file
                                        // sono già visualizzati
                                    }
                                }
                                if (valoriRipristino) {
                                    //feedback
                                    Toast toast = Toast.makeText(activity,
                                            R.string.misure_ripristinate,
                                            Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, 300);
                                    toast.show();
                                } else {
                                    //feedback
                                    Toast toast = Toast.makeText(activity,
                                            R.string.nessuna_misura_da_ripristinare,
                                            Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, 300);
                                    toast.show();
                                }
                            } else {
                                //feedback
                                Toast toast = Toast.makeText(activity, R.string.nessuna_misura_nel_file,
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM, 0, 300);
                                toast.show();
                            }

                        }));
    }

    /**
     * Method for reverting values from google Drive to use for boyscout
     *
     * @param dbManager      DbManager object to handle operations on database.
     * @param instrumentName name of the instrument whose values you want to reset
     * @param activity       Activity in which we are performing restore
     */
    public void restoreFile(DbManager dbManager, String instrumentName,
                            BoyscoutDBValuesActivity activity) throws IOException {
        Log.d(TAG,"restoring values on BoyScout");
        getIdentificativo(instrumentName)
                .addOnSuccessListener(fileId -> readFile(fileId)
                        .addOnSuccessListener(fileContent -> {
                            LinearLayout progressBar = activity.findViewById(R.id.llProgressBar);
                            List<InstrumentRecord> instrumentRecordsReadFromDB = dbManager
                                    .readBoyscoutValuesFromDB(instrumentName);
                            String timestamp;
                            boolean control;
                            boolean valoriRipristino = false;
                            String[] lines = fileContent.split("\n");
                            String[] words;
                            if (!fileContent.isEmpty()) { //controlla che il file non sia vuoto
                                if (!instrumentRecordsReadFromDB.isEmpty()) {//controlla che nel db
                                    // ci siano salvati dei valori
                                    for (String string : lines) {
                                        words = string.split(" ");
                                        timestamp = words[2] + " " + words[3];
                                        control = false;
                                        //controlla che il valore non sia già salvato sul database
                                        // controllando il timestamp
                                        for (final InstrumentRecord record :
                                                instrumentRecordsReadFromDB) {
                                            if (record.getTimestamp().contentEquals(timestamp)) {
                                                control = true;
                                            }
                                        }
                                        if (!control) { //se il valore non è presente nel db
                                            // questo viene aggiunto
                                            dbManager.saveRegisteredValues
                                                    ("valuesRecordedByBoyscout",
                                                            null, timestamp,
                                                    instrumentName, Float.parseFloat(words[5]));
                                            valoriRipristino = true;//indica che c'è stato un
                                            // ripristino dei valori, altrimenti i valori del file
                                            // sono già visualizzati
                                        }
                                    }
                                } else {//se nel db non ci sono valori, i dati del file vengono
                                    // aggiunti automaticamente
                                    for (String string : lines) {
                                        words = string.split(" ");
                                        timestamp = words[2] + " " + words[3];
                                        dbManager.saveRegisteredValues
                                                ("valuesRecordedByBoyscout",
                                                        null, timestamp,
                                                instrumentName, Float.parseFloat(words[5]));
                                        valoriRipristino = true;
                                    }
                                }
                                activity.showBoyscoutTableValues(dbManager.readBoyscoutValuesFromDB
                                        (instrumentName));
                                progressBar.setVisibility(View.GONE);
                                if (valoriRipristino) {
                                    //feedback
                                    Toast toast = Toast.makeText(activity,
                                            R.string.misure_ripristinate,Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, 300);
                                    toast.show();
                                } else {
                                    //feedback
                                    Toast toast = Toast.makeText(activity,
                                            R.string.nessuna_misura_da_ripristinare,
                                            Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, 300);
                                    toast.show();
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                //feedback
                                Toast toast = Toast.makeText(activity, R.string.nessuna_misura_nel_file,
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM, 0, 300);
                                toast.show();
                            }
                        }));
    }

    /**
     * This method checks whether the file already exists; if it exists
     * @param nomeFile name of the file to check
     * @return file id, if it does not exist it creates a new file and returns the id
     */
    private Task<String> getIdentificativo(String nomeFile) throws IOException {
        Log.d(TAG,"in getID");
        return Tasks.call(mExecutor, () -> {
            FileList fileList = mDriveService.files().list().setSpaces("drive").execute();
            String fileId = "";
            boolean control = false;

            //controlla che il file esiste e ne recupera l'id
            String currentFile;
            for (File file : fileList.getFiles()) {
                currentFile = file.getName();
                if (currentFile.contentEquals(nomeFile)) {
                    fileId = file.getId();
                    control = true;
                }
            }
            //se il file non esiste ne crea uno e ne recupera l'id
            if (!control) {
                File metadata = new File()
                        .setParents(Collections.singletonList("root"))
                        .setMimeType("text/plain")
                        .setName(nomeFile);
                File googleFile = mDriveService.files().create(metadata).execute();
                if (googleFile == null) {
                    throw new IOException("Null result when requesting file creation.");
                }
                fileId = googleFile.getId();
            }
            return fileId;
        });

    }

    /**
     * reads the identified file and returns its contents
     * @param fileId id of the file to read
     * @return content of the file as a string
     */
    private Task<String> readFile(String fileId) {
        return Tasks.call(mExecutor, () -> {
            try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                return stringBuilder.toString();
            }
        });
    }

    /**
     * updates the contents of the file with the content it receives as a parameter identified
     * with the id
     * @param fileId id of the file to update
     * @param content query to add to file
     */
    private void saveFile(String fileId, String content) {
        Tasks.call(mExecutor, () -> {

            File metadata = new File();

            ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain",
                    content);

            mDriveService.files().update(fileId, metadata, contentStream).execute();
            return null;
        });
    }
}
