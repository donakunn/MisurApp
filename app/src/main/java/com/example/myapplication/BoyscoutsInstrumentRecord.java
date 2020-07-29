package com.example.myapplication;

public class BoyscoutsInstrumentRecord extends InstrumentRecord {

    private String boyScoutNickName;

    public BoyscoutsInstrumentRecord(String boyScoutNickName,String date, float value) {
        super(date, value);
        this.boyScoutNickName = boyScoutNickName;
    }

    public String getBoyScoutNickName() {
        return boyScoutNickName;
    }
}
