package com.example.misurapp.db;

/**
 * App tables schema
 */
public class InstrumentsDBSchema {

    /**
     * BoyScout table schema
     */
    public static final class BoyscoutTable {
        public static final String TABLENAME = "valuesRecordedByBoyscout";

        public static final class cols {
            public static final String INSTRUMENTNAME = "instrumentName";
            public static final String VALUEREAD = "valueRead";
            public static final String TIMESTAMP = "timestamp";
        }
    }

    /**
     * ScoutMaster table schema
     */
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
