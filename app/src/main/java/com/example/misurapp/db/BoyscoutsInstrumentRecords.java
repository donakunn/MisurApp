package com.example.misurapp.db;

import com.example.misurapp.Instrument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class BoyscoutsInstrumentRecords  implements Serializable {

    private String email;
    private List<InstrumentRecord> boyscoutRecords = new LinkedList<>();

    public BoyscoutsInstrumentRecords(String email, List<InstrumentRecord> boyscoutRecords) {
        this.email = email;
        this.boyscoutRecords = boyscoutRecords;
    }

    public String getBoyScoutEmail() {
        return email;
    }

    public byte[] serialize(BoyscoutsInstrumentRecords recordToSend) throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());
        objectOutputStream.writeObject(recordToSend);
        return byteArray.toByteArray();
    }

    public static BoyscoutsInstrumentRecords deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArray);
        return (BoyscoutsInstrumentRecords) objectInputStream.readObject();
    }
}

