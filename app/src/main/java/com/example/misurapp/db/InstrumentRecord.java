package com.example.misurapp.db;

import java.io.Serializable;

/**
 * Class which describe and handle instrument records
 */
public class InstrumentRecord implements Serializable {
    /**
     * id of the record
     */
    private long id;
    /**
     * timestamp of the record
     */
    private String timestamp;
    /**
     * registered value
     */
    private float value;

    public InstrumentRecord(long id,String timestamp, float value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    public long getId() { return id; }

    public String getTimestamp() {
        return timestamp;
    }

    public float getValue() {
        return value;
    }
}
