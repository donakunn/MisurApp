package com.example.misurapp.db;

public class ScoutMasterInstrumentRecord extends InstrumentRecord {

    private String email;
    private String instrumentName;

    public ScoutMasterInstrumentRecord(long id, String date, float value,String email,
                                       String instrumentName) {
        super(id, date, value);
        this.email = email;
        this.instrumentName = instrumentName;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public String getEmail() {
        return email;
    }
}
