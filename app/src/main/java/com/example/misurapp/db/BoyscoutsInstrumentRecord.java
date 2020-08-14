package com.example.misurapp.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class BoyscoutsInstrumentRecord extends InstrumentRecord implements Serializable {

    private String boyScoutNickName;

    public BoyscoutsInstrumentRecord(long id, String boyScoutNickName, String date, float value) {
        super(id, date, value);
        this.boyScoutNickName = boyScoutNickName;
    }

    public String getBoyScoutNickName() {
        return boyScoutNickName;
    }

    public byte[] serialize(List<BoyscoutsInstrumentRecord> recordToSend) throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());
        objectOutputStream.writeObject(recordToSend);
        return byteArray.toByteArray();
    }

    public static List<BoyscoutsInstrumentRecord> deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArray);
        return (List<BoyscoutsInstrumentRecord>) objectInputStream.readObject();
    }
}

