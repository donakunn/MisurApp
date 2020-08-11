package com.example.misurapp;

public class Instrument {
    private String instrumentName;
    private int type;
    private String [] description;
    private String unitOfMeasure;

    public Instrument(String instrumentName, int type, String[] description,
                      String unitOfMeasure) {
        this.instrumentName = instrumentName;
        this.type = type;
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getinstrumentName() {return instrumentName;};

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public int getType() {
        return type;
    }

    public String[] getDescription() {
        return description;
    }

}
