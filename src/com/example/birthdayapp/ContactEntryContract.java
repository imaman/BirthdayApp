package com.example.birthdayapp;

import android.provider.BaseColumns;

public final class ContactEntryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ContactEntryContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";        
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_BIRTH_DATE = "birthdate";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_EMAIL = "email";
    }
}
