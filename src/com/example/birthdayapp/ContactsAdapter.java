package com.example.birthdayapp;

import com.example.birthdayapp.ContactEntryContract.ContactEntry;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class ContactsAdapter extends SimpleCursorAdapter {
  public ContactsAdapter(Context context, Cursor c, int flags) {
	  super(context, 
			R.layout.contact_entry,
			c, 
			new String[] { ContactEntry.COLUMN_NAME_NAME, ContactEntry.COLUMN_NAME_BIRTH_DATE}, 
			new int[] { R.id.nameTextView, R.id.dateTextView}, 
			flags);
  }
}
