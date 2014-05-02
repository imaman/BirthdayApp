package com.example.birthdayapp;

import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.birthdayapp.ContactEntryContract.ContactEntry;


public class ContactsActivity extends ActionBarActivity {

    private static final int EDIT_CODE = 1;

	private ContactDbHelper contactDbHelper;
	private ListView contactsListView;
	private ContactsAdapter contactsAdapter;
	List<ContactEntry> contactsList;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(this, AlarmReciever.class);
        PendingIntent alaramIntent = PendingIntent.getBroadcast(this, 0, serviceIntent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, 1000 * 5, 
                alaramIntent);
        
        
        setContentView(R.layout.activity_contacts);
		
        contactsListView = (ListView)findViewById(R.id.contactsListView);
        contactDbHelper = new ContactDbHelper(this);
        //addEntriesToDb();
        contactsList = contactDbHelper.getContacts();
        contactsAdapter = new ContactsAdapter(this, contactsList, this);
        contactsListView.setAdapter(contactsAdapter);
    }

    public void editContact(long id) {
        Intent intent = new Intent(this, EditActivity.class);
        ContactEntry contact = contactFromId(id);
        
        intent.putExtra(Items.CONTACT_ID, contact.getEntryId());
        intent.putExtra(Items.ITEM_BIRTHDATE, contact.getBirthDate());
        intent.putExtra(Items.ITEM_NAME, contact.getName());
        intent.putExtra(Items.ITEM_EMAIL, contact.getEmail());
		startActivityForResult(intent, EDIT_CODE);
    }
        
    private ContactEntry contactFromId(long id) {
        for (ContactEntry curr : contactsList) {
            if (curr.getEntryId() == id)
                return curr;
        }
        return null;
    }
    
    public boolean deleteContact(final long id_) {
        final ContactEntry contactToDelete = contactFromId(id_);
        if (contactToDelete == null)
            return true;
        
        
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Delete?");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_launcher);
 
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                // TODO(imaman): this should be an async task.
                contactDbHelper.deleteEntry(contactToDelete.getEntryId());
                contactsList.remove(contactToDelete);
                dataChanged();
            }
        });
 
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
        return true;
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        
        if (id == R.id.action_add) {
            ContactEntry contact = new ContactEntry(this, "", 0, "", null);
            contactDbHelper.addEntry(contact);
            contactsList.add(contact);
            editContact(contact.getEntryId());
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    public void addEntriesToDb() {
     	contactDbHelper.addEntry(
     			new ContactEntry(this, "Shai Sabag", 100000000, "shais@google.com", null));
     	contactDbHelper.addEntry(
     			new ContactEntry(this, "Itai Maman", 200000000, "imaman@google.com", null));
     }
    
    public void initCursor() {
    	
    }
    
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {     
      super.onActivityResult(requestCode, resultCode, data); 
      switch(requestCode) { 
        case (EDIT_CODE) : { 
          if (resultCode == Activity.RESULT_OK) {
        	  long contactId = data.getLongExtra(Items.CONTACT_ID, -1);
        	  if (contactId < 0)
        	      break;
        	  ContactEntry contact = contactFromId(contactId);
        	  if (contact == null) {
        	      Toast.makeText(this, "ID " + contactId + " not found", Toast.LENGTH_SHORT).show();
        	      break;
        	  }
        	  contact.setName(data.getStringExtra(Items.ITEM_NAME));
        	  contact.setEmail(data.getStringExtra(Items.ITEM_EMAIL));
        	  contact.setBirthDate(data.getLongExtra(Items.ITEM_BIRTHDATE, 0));
        	  contactDbHelper.updateEntry(contact);
        	  dataChanged();
          } 
          break; 
        } 
      }
    }

    void dataChanged() {
        contactsAdapter.notifyDataSetChanged();
        AlarmReciever.startNotificationService(this);
    }
}
