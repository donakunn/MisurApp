package com.example.myapplication.db;

import java.io.Serializable;

public class BoyscoutsInstrumentRecord extends InstrumentRecord implements Serializable {

    private String boyScoutNickName;

    public BoyscoutsInstrumentRecord(String boyScoutNickName,String date, float value) {
        super(date, value);
        this.boyScoutNickName = boyScoutNickName;
    }

    public String getBoyScoutNickName() {
        return boyScoutNickName;
    }
}
