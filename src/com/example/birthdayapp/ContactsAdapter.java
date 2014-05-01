package com.example.birthdayapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.birthdayapp.ContactEntryContract.ContactEntry;

public class ContactsAdapter extends ArrayAdapter<ContactEntry> {
	List<ContactEntry> contacts;
    private final ContactsActivity contactsActivity;
    private final Map<Long, ContactEntry> contactById = new HashMap<Long, ContactEntry>();
    public ContactsAdapter(Context context, List<ContactEntry> contacts, ContactsActivity contactsActivity) {
	  super(context, R.layout.contact_entry, contacts);
	  
	  for (ContactEntry curr : contacts) {
	      contactById.put(curr.getEntryId(), curr);
	  }
	  
	  this.contacts = contacts;
	  this.contactsActivity = contactsActivity;
    }
    
    @Override
    public long getItemId(int position) {
        return this.contacts.get(position).getEntryId();
    }

    @Override public View getView (final int position, View convertView, ViewGroup parent) {
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
  		
/*  		convertView.setLongClickable(true); */
  		convertView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return contactsActivity.deleteContact(getItemId(position));
            }
  		});
  		convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsActivity.editContact(getItemId(position));
            }
        });
  		return convertView;
    }
}
