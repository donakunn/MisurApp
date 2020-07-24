package com.example.myapplication;

public class queryDB {
    private int id;
    private String data;
    private float[] valori;

    queryDB(int id, String data, float[] valori) {
        this.id = id;
        this.data = data;
        this.valori = valori;
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public float[] getValori() {
        return valori;
    }
}
