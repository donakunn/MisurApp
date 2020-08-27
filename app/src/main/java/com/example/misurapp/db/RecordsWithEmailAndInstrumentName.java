package com.example.misurapp.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class RecordsWithEmailAndInstrumentName implements Serializable {

    private String email;
    private String instrumentName;
    private List<InstrumentRecord> boyscoutRecords;

    public RecordsWithEmailAndInstrumentName(String email, String instrumentName,
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

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArray);
        objectOutputStream.writeObject(this);
        return byteArray.toByteArray();
    }

    public static RecordsWithEmailAndInstrumentName deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArray);
        return (RecordsWithEmailAndInstrumentName) objectInputStream.readObject();
    }
}

