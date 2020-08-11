package com.example.myapplication.db;

import java.io.Serializable;

public class BoyscoutsInstrumentRecord extends InstrumentRecord implements Serializable {

    private String boyScoutNickName;

    public BoyscoutsInstrumentRecord(long id,String boyScoutNickName,String date, float value) {
        super(id,date, value);
        this.boyScoutNickName = boyScoutNickName;
    }

    public String getBoyScoutNickName() {
        return boyScoutNickName;
    }
}
