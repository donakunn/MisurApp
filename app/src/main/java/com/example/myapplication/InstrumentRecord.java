package com.example.myapplication;

public class InstrumentRecord {
    private String date;
    private float value;

    public InstrumentRecord(String date, float value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public float getValue() {
        return value;
    }
}
