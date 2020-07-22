package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Sensore implements Parcelable {
    private int tipo;
    private String [] descrizione;
    private String unitàDiMisura;
    private int dimensione;

    public Sensore(int tipo, String[] descrizione, String unitàDiMisura, int dimensione) {
        this.tipo = tipo;
        this.descrizione = descrizione;
        this.unitàDiMisura = unitàDiMisura;
        this.dimensione = dimensione;
    }

    protected Sensore(Parcel in) {
        tipo = in.readInt();
        descrizione = in.createStringArray();
        unitàDiMisura = in.readString();
        dimensione = in.readInt();
    }

    public static final Creator<Sensore> CREATOR = new Creator<Sensore>() {
        @Override
        public Sensore createFromParcel(Parcel in) {
            return new Sensore(in);
        }

        @Override
        public Sensore[] newArray(int size) {
            return new Sensore[size];
        }
    };

    public int getDimensione() {
        return dimensione;
    }

    public String getUnitàDiMisura() {
        return unitàDiMisura;
    }

    public int getTipo() {
        return tipo;
    }

    public String[] getDescrizione() {
        return descrizione;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(tipo);
        dest.writeStringArray(descrizione);
        dest.writeString(unitàDiMisura);
        dest.writeInt(dimensione);
    }
}
