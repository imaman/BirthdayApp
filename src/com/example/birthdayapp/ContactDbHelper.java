package com.example.birthdayapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.birthdayapp.ContactEntryContract.Contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDbHelper extends SQLiteOpenHelper {
	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + Contact.TABLE_NAME + " (" +
	    Contact.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY, " +
	    Contact.COLUMN_NAME_EMAIL + " TEXT, " +
	    Contact.COLUMN_NAME_NAME + " TEXT, " +
	    Contact.COLUMN_NAME_BIRTH_DATE + " INTEGER, " +
	    Contact.COLUMN_NAME_IMAGE + " TEXT" +
	    " )";
	
	private static final String SQL_DELETE_ENTRIES =
		    "DROP TABLE IF EXISTS " + Contact.TABLE_NAME;
	
	public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Contacts.db";

    public ContactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
	
    public long addEntry(Contact entry) {
    	SQLiteDatabase db = getWritableDatabase();
    	// Create a new map of values, where column names are the keys
    	ContentValues values = new ContentValues();
    	values.put(Contact.COLUMN_NAME_ENTRY_ID, entry.getEntryId());
    	values.put(Contact.COLUMN_NAME_NAME, entry.getName());
    	values.put(Contact.COLUMN_NAME_BIRTH_DATE, entry.getBirthDate());
    	values.put(Contact.COLUMN_NAME_EMAIL, entry.getEmail());
    	if (entry.getImageFileName() != null)
    	values.put(Contact.COLUMN_NAME_IMAGE, entry.getImageFileName());

    	// Insert the new row, returning the primary key value of the new row
    	return db.insert(Contact.TABLE_NAME, null, values);
    }

    public long updateEntry(Contact entry) {
        SQLiteDatabase db = getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Contact.COLUMN_NAME_ENTRY_ID, entry.getEntryId());
        values.put(Contact.COLUMN_NAME_NAME, entry.getName());
        values.put(Contact.COLUMN_NAME_BIRTH_DATE, entry.getBirthDate());
        values.put(Contact.COLUMN_NAME_EMAIL, entry.getEmail());
        if (entry.getImageFileName() != null)
        values.put(Contact.COLUMN_NAME_IMAGE, entry.getImageFileName());

        // Insert the new row, returning the primary key value of the new row
        String selection = Contact.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(entry.getEntryId()) };
        return db.update(Contact.TABLE_NAME, values, selection, selectionArgs);
    }
    
	public void deleteEntry(long entryId) {
		SQLiteDatabase db = getWritableDatabase();
		// Define 'where' part of query.
		String selection = Contact.COLUMN_NAME_ENTRY_ID + " LIKE ?";
		// Specify arguments in placeholder order.
		String[] selectionArgs = { String.valueOf(entryId) };
		// Issue SQL statement.
		db.delete(Contact.TABLE_NAME, selection, selectionArgs);
	}
	
	public Cursor getCursor() {
    	SQLiteDatabase db = getReadableDatabase();

    	// Define a projection that specifies which columns from the database
    	// you will actually use after this query.
    	String[] projection = {
    		Contact.COLUMN_NAME_ENTRY_ID,
    	    Contact.COLUMN_NAME_NAME,
    	    Contact.COLUMN_NAME_BIRTH_DATE,
    	    Contact.COLUMN_NAME_EMAIL,
    	    Contact.COLUMN_NAME_IMAGE
    	    };

    	// How you want the results sorted in the resulting Cursor
    	String sortOrder =
    		Contact.COLUMN_NAME_BIRTH_DATE + " ASC";

    	return db.query(
    		Contact.TABLE_NAME,  // The table to query
    	    projection,                               // The columns to return
    	    null,                                // The columns for the WHERE clause
    	    null,                            // The values for the WHERE clause
    	    null,                                     // don't group the rows
    	    null,                                     // don't filter by row groups
    	    sortOrder                                 // The sort order
    	    );
	}
	public List<Contact> listContactsByBirthdays() {
		Cursor cursor = getCursor();
		List<Contact> contactsList = new ArrayList<Contact>(cursor.getCount());
		if (!cursor.moveToFirst())
			return contactsList;
		do { 
			contactsList.add(new Contact(cursor));
		} while (cursor.moveToNext());
		
		Collections.sort(contactsList, new Comparator<Contact>() {
	        private final Calendar now = Calendar.getInstance();
            @Override
            public int compare(Contact lhs, Contact rhs) {
                long lhsDays = lhs.daysTillNextBirthday(now);
                long rhsDays = rhs.daysTillNextBirthday(now);
                return Long.compare(lhsDays, rhsDays);
            }
        });
		
		return contactsList;
	}
}
