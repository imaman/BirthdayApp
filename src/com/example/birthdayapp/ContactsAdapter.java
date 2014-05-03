package com.example.birthdayapp;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
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
    
    private final LruCache<String, BitmapStatus> bitmapByFilename = new LruCache<String, BitmapStatus>(40);
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
        assignPhoto(contact, photo);
            
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

    void assignPhoto(Contact contact, ImageView photo) {
        String imageFileName = contact.getImageFileName();
        Drawable drawable = contactsActivity.getResources().getDrawable(R.drawable.ic_launcher);
        if (imageFileName == null) {
            photo.setImageDrawable(drawable);
            return;
        }
        BitmapStatus status = bitmapByFilename.get(imageFileName);
        if (status == null) {
            status = new BitmapStatus();
            bitmapByFilename.put(imageFileName, status);
        }
        if (status.shouldRequest()) {
            status.requested = true;
            fetch(imageFileName, status);
        }
        
        Bitmap bm = status.bitmap;
        if (bm != null) {
            photo.setImageBitmap(bm);
            return;
        }
        
        photo.setImageDrawable(drawable);
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
