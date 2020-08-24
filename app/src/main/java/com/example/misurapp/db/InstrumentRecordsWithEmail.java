package com.example.misurapp.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class InstrumentRecordsWithEmail implements Serializable {

    private String email;
    private List<InstrumentRecord> boyscoutRecords;

    public InstrumentRecordsWithEmail(String email, String instrumentName,
                                      List<InstrumentRecord> boyscoutRecords) {
        this.email = email;
        this.boyscoutRecords = boyscoutRecords;
    }

    public String getBoyScoutEmail() {
        return email;
    }
    public List<InstrumentRecord> getBoyscoutRecords() {
        return this.boyscoutRecords;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());
        objectOutputStream.writeObject(this);
        return byteArray.toByteArray();
    }

    public static InstrumentRecordsWithEmail deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArray);
        return (InstrumentRecordsWithEmail) objectInputStream.readObject();
    }
}

