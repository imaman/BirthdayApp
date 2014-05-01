package com.example.birthdayapp;

import java.util.List;

import com.example.birthdayapp.ContactEntryContract.ContactEntry;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;


public class ContactsActivity extends ActionBarActivity implements OnItemClickListener {

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
//        addEntriesToDb();
        contactsList = contactDbHelper.getContacts();
        contactsAdapter = new ContactsAdapter(this, contactsList);
        contactsListView.setAdapter(contactsAdapter);
        contactsListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Intent intent = new Intent(this, EditActivity.class);
        ContactEntry currContact = contactsList.get(position);
		intent.putExtra(Items.ITEM_POSITION, position);
		intent.putExtra(Items.ITEM_BIRTHDATE, currContact.getBirthDate());
        intent.putExtra(Items.ITEM_NAME, currContact.getName());
        intent.putExtra(Items.ITEM_EMAIL, currContact.getEmail());
		startActivityForResult(intent, EDIT_CODE);
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
        	  long position = data.getLongExtra(Items.ITEM_POSITION, 0);
        	  ContactEntry contact = contactsList.get((int) position);
        	  contact.setName(data.getStringExtra(Items.ITEM_NAME));
        	  contact.setEmail(data.getStringExtra(Items.ITEM_EMAIL));
        	  contact.setBirthDate(data.getLongExtra(Items.ITEM_BIRTHDATE, 0));
        	  contactDbHelper.updateEntry(contact);
        	  contactsAdapter.notifyDataSetChanged();
          } 
          break; 
        } 
      }
    }

}
