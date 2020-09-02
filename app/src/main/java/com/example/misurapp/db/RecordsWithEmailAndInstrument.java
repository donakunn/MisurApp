package com.example.misurapp.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * This class create and manage and object containing user email, instrument name and a list of
 * records.
 */
public class RecordsWithEmailAndInstrument implements Serializable {

    /**
     * User email
     */
    private String email;
    /**
     * Name of the instrument
     */
    private String instrumentName;
    /**
     * list of InstrumentRecord objects
     */
    private List<InstrumentRecord> boyscoutRecords;

    public RecordsWithEmailAndInstrument(String email, String instrumentName,
                                         List<InstrumentRecord> boyscoutRecords) {
        this.email = email;
        this.instrumentName = instrumentName;
        this.boyscoutRecords = boyscoutRecords;
    }

    public String getBoyScoutEmail() {
        return email;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public List<InstrumentRecord> getBoyscoutRecords() {
        return this.boyscoutRecords;
    }

    /**
     * Serialize this object into an array of bytes
     *
     * @return bytes array.
     * @throws IOException if something went wrong during the serialization
     */
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArray);
        objectOutputStream.writeObject(this);
        return byteArray.toByteArray();
    }

    /**
     * Deserialize an array of bytes into a RecordsWithEmailAndInstrument object
     *
     * @param bytes bytes array to be de-serialized
     * @return RecordsWithEmailAndInstrument object
     * @throws IOException            if something went wrong during the de-serialization
     * @throws ClassNotFoundException if Class can't be found
     */
    public static RecordsWithEmailAndInstrument deserialize(byte[] bytes) throws IOException,
            ClassNotFoundException {
        ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArray);
        return (RecordsWithEmailAndInstrument) objectInputStream.readObject();
    }
}

