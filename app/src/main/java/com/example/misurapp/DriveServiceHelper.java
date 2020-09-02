package com.example.misurapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Pair;

import com.example.misurapp.db.DbManager;
import com.example.misurapp.db.InstrumentRecord;
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
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;
    private static final String TAG = "GoogleDrive";

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }


    //METODO PER CREARE E SALVARE FILE IN GOOGLE DRIVE
    public void createAndSaveFile(DbManager appDb, String sensorName) throws IOException {
        Log.d(TAG, "Creating a file.");

        //setta nome del file
        String fileName = sensorName; //nome file sarà nomesensore

        //setta contenuto del file
        String fileContent = "";
        List<InstrumentRecord> instrumentRecordsReadFromDB = appDb.readBoyscoutValuesFromDB
                (sensorName);

        for (final InstrumentRecord record : instrumentRecordsReadFromDB) {
            fileContent = fileContent +" Data: "+ record.getTimestamp() + " Misura: "+ record.getValue() + "\n";
        }
        String finalFileContent = fileContent;
        //salva file
        getIdentificativo(fileName)
                .addOnSuccessListener(fileId -> saveFile(fileId, finalFileContent))
                .addOnFailureListener(exception ->
                        Log.e(TAG, "Couldn't create file.", exception));
        //CREARE UN TOAST PER CAPIRE SE IL SAVATAGGIO è ANDATO A BUON FINE
    }

    //METODO PER RIPRISTINARE I VALORI DA GOOGLE DRIVE DA USARE PER GLI STRUMENTI DELLE ACTIVITY
    public void restoreFile(DbManager appDb, String sensorName) throws IOException {
        getIdentificativo(sensorName)
                .addOnSuccessListener(fileId -> readFile(fileId)
                        .addOnSuccessListener(fileContent -> {
                            List<InstrumentRecord> instrumentRecordsReadFromDB = appDb.readBoyscoutValuesFromDB(sensorName);
                            String timestamp;
                            boolean control = false;
                            String[] lines = fileContent.split("\n");
                            String[] words;
                            if (fileContent != null ) { //controlla che il file non sia vuoto
                                if (!instrumentRecordsReadFromDB.isEmpty()) {//controlla che nel db ci siano salvati dei valori
                                    for (String string : lines) {
                                        words = string.split(" ");
                                        timestamp = words[2] + " " + words[3];
                                        control = false;
                                        //controlla che il valore non sia già salvato sul database controllando il timestamp
                                        for (final InstrumentRecord record : instrumentRecordsReadFromDB) {
                                            if (record.getTimestamp().contentEquals(timestamp)) {
                                                control = true;
                                            }
                                        }
                                        if (!control) {
                                            appDb.saveRegisteredValues("valuesRecordedByBoyscout", null, timestamp,
                                                    sensorName, Float.valueOf(words[5]));
                                        }
                                    }
                                } else {
                                    for (String string : lines) {
                                        words = string.split(" ");
                                        timestamp = words[2] + " " + words[3];
                                        appDb.saveRegisteredValues("valuesRecordedByBoyscout", null, timestamp,
                                                sensorName, Float.valueOf(words[5]));
                                    }
                                }
                            }
                        }));
    }

    //in base al nome del file controlla se il file esiste già; se esiste ritorna il suo id, se non esiste crea un nuovo file e restituisce l'id
    public Task<String> getIdentificativo(String nomeFile) throws IOException {
        return Tasks.call(mExecutor, () -> {
            FileList fileList = mDriveService.files().list().setSpaces("drive").execute();//queryFiles()
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
            if (control==false){
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
     * Opens the file identified by {@code fileId} and returns a string of its
     * contents.
     */


    //legge file e restituisce il contenuto
    public Task<String> readFile(String fileId) {
        return Tasks.call(mExecutor, () -> {

            // Stream the file contents to a String.
            try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                String contents = stringBuilder.toString();
                return contents;
            }
        });
    }

    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    private Task<Void> saveFile(String fileId, String content) {
        return Tasks.call(mExecutor, () -> {
            // Create a File containing any metadata changes.
            File metadata = new File();

            // Convert content to an AbstractInputStreamContent instance.
            ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

            // Update the metadata and contents.
            mDriveService.files().update(fileId, metadata, contentStream).execute();
            return null;
        });
    }
}
