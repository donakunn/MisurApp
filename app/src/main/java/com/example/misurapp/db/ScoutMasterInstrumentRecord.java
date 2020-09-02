package com.example.misurapp.db;

/**
 * Class which extends InstrumentRecord adding user email and
 * instrument name. Describe and handle ScoutMaster instrument records
 */
public class ScoutMasterInstrumentRecord extends InstrumentRecord {

    /**
     * User email
     */
    private String email;
    /**
     * name of the instrument.
     */
    private String instrumentName;

    public ScoutMasterInstrumentRecord(long id, String date, float value, String email,
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
