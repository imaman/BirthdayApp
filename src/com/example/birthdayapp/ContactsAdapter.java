package com.example.birthdayapp;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.birthdayapp.ContactEntryContract.Contact;

public class ContactsAdapter extends ArrayAdapter<Contact> {
	private final List<Contact> contacts;
    private final ContactsActivity contactsActivity;
    
    private class BitmapStatus {
        public boolean requested = false;
        private volatile Bitmap bitmap;
        
        public boolean shouldRequest() {
            return bitmap == null && !requested;
        }
    }
    
    private final Map<String, BitmapStatus> bitmapByFilename = new HashMap<String, BitmapStatus>();
    public ContactsAdapter(Context context, List<Contact> contacts, ContactsActivity contactsActivity) {
	  super(context, R.layout.contact_entry, contacts);
	  
	  this.contacts = contacts;
	  this.contactsActivity = contactsActivity;
    }    
    
    @Override
    public long getItemId(int position) {
        return this.contacts.get(position).id();
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

  		ImageView photo = (ImageView) convertView.findViewById(R.id.contactImage);
        BitmapStatus status = bitmapByFilename.get(contact.getImageFileName());
        if (status == null) {
            status = new BitmapStatus();
            bitmapByFilename.put(contact.getImageFileName(), status);
        }
        if (status.shouldRequest()) {
            status.requested = true;
            fetch(contact.getImageFileName(), status);
        }
        
        Bitmap bm = status.bitmap;
        photo.setImageBitmap(bm);
  		convertView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return contactsActivity.deleteContact(getItemId(position));
            }
  		});
  		convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsActivity.editContact(contacts.get(position), true);
            }
        });
  		return convertView;
    }

    private void fetch(final String filename, final BitmapStatus status) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                status.bitmap = BitmapFactory.decodeFile(filename, options);
                return status.bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result == null)
                    return;
                
                ContactsAdapter.this.notifyDataSetChanged();
            }
        }.execute();
    }
}
