package com.zetzaus.criminalintent.database;

/**
 * This class holds the strings for the database schema.
 */
public class CrimeDbSchema {

    /**
     * This inner class holds the strings for the table.
     */
    public static final class CrimeTable {
        public static final String NAME = "crime";

        /**
         * This inner class holds the strings for the table columns.
         */
        public static final class CrimeColumn {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String SUSPECT = "suspect";
            public static final String PHONE = "phone";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String REQUIRES_POLICE = "requires_police";
        }
    }

}
