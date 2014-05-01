package com.example.birthdayapp;

import java.util.List;

import com.example.birthdayapp.ContactEntryContract.ContactEntry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContactsAdapter extends ArrayAdapter<ContactEntry> {
	List<ContactEntry> contacts;
    public ContactsAdapter(Context context, List<ContactEntry> contacts) {
	  super(context, R.layout.contact_entry, contacts);
	  this.contacts = contacts;
    }
    @Override public View getView (int position, View convertView, ViewGroup parent) {
    	if (convertView == null) {
    		LayoutInflater inflater = (LayoutInflater) getContext()
    				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_entry, null);
        }

  // Populate
    	TextView nameView = (TextView) convertView.findViewById(R.id.nameTextView);
  		TextView birthDateView = (TextView) convertView.findViewById(R.id.dateTextView);
  		nameView.setText(contacts.get(position).getName());
  		birthDateView.setText(contacts.get(position).getBirthDateAsString());
  		return convertView;
    }
}
