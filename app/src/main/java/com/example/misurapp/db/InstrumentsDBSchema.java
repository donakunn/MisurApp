package com.example.misurapp.db;
//Questa classe descrive lo schema delle due tabelle contenenti i valori salvati dagli strumenti
public class InstrumentsDBSchema {
    //schema tabella che contiene i valori registrati dall'app in modalità boyscout
    public static final class BoyscoutTable {
        public static final String TABLENAME = "valuesRecordedByBoyscout";

        public static final class cols {
            public static final String INSTRUMENTNAME = "instrumentName";
            public static final String VALUEREAD = "valueRead";
            public static final String TIMESTAMP = "timestamp";
        }
    }
    //schema tabella che contiene valori mostrati al capo scout, ricevuti dai boyscout
    public static final class ScoutMasterTable {
        public static final String TABLENAME = "valuesReceivedByBoyscout";

        public static final class cols {
            public static final String EMAIL = "email";
            public static final String TIMESTAMP = "timestamp";
            public static final String INSTRUMENTNAME = "instrumentName";
            public static final String VALUEREAD = "valueRead";

        }
    }
}
