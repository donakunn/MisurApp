package com.example.myapplication;

public class InstrumentsDBSchema {

    public static final class BoyscoutTable {
        public static final String TABLENAME = "valuesRecordedByBoyscout";

        public static final class cols {
            public static final String INSTRUMENTNAME = "instrumentName";
            public static final String FIRSTVALUEREAD = "firstValueRead";
            public static final String SECONDVALUEREAD = "secondValueRead";
            public static final String THIRDVALUEREAD = "thirdValueRead";
            //public static final String FOURTHVALUEREAD = "fourthValueRead";
            //public static final String FIFTHVALUEREAD = "fifthValueRead";
        }
    }

    public static final class ScoutMasterTable {
        public static final String TABLENAME = "valuesReceivedByBoyscout";

        public static final class cols {
            public static final String BOYSCOUTNICKNAME = "boyscoutNickname";
            public static final String TIMESTAMP = "timestamp";
            public static final String INSTRUMENTNAME = "instrumentName";
            public static final String FIRSTVALUEREAD = "firstValueRead";
            public static final String SECONDVALUEREAD = "secondValueRead";
            public static final String THIRDVALUEREAD = "thirdValueRead";
            //public static final String FOURTHVALUEREAD = "fourthValueRead";
            //public static final String FIFTHVALUEREAD = "fifthValueRead";
        }
    }
}
