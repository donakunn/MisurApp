package com.example.myapplication.db;

public class InstrumentRecord {
    private long id;
    private String date;
    private float value;

    public InstrumentRecord(long id,String date, float value) {
        this.id = id;
        this.date = date;
        this.value = value;
    }

    public long getId() { return id; }

    public String getDate() {
        return date;
    }

    public float getValue() {
        return value;
    }
}
