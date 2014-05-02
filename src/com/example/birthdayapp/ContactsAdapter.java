package com.example.birthdayapp;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.birthdayapp.ContactEntryContract.Contact;

public class ContactsAdapter extends ArrayAdapter<Contact> {
	List<Contact> contacts;
    private final ContactsActivity contactsActivity;
    public ContactsAdapter(Context context, List<Contact> contacts, ContactsActivity contactsActivity) {
	  super(context, R.layout.contact_entry, contacts);
	  
	  this.contacts = contacts;
	  this.contactsActivity = contactsActivity;
    }
    
    @Override
    public long getItemId(int position) {
        return this.contacts.get(position).getEntryId();
    }

    @Override public View getView (final int position, View convertView, ViewGroup parent) {
        Calendar now = Calendar.getInstance();
    	if (convertView == null) {
    		LayoutInflater inflater = (LayoutInflater) getContext()
    				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_entry, null);
        }

    	// Populate
    	TextView nameView = (TextView) convertView.findViewById(R.id.nameTextView);
  		TextView birthDateView = (TextView) convertView.findViewById(R.id.dateTextView);
  		Contact contact = contacts.get(position);
      nameView.setText(contact.getName());
  		birthDateView.setText(contact.getBirthDateAsString());
  		TextView timeLeftView = (TextView) convertView.findViewById(R.id.timeLeftTextView);
  		timeLeftView.setText(Ui.daysLeftMessage(now, contact));
  		
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
