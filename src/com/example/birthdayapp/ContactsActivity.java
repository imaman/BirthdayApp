package com.example.birthdayapp;

import java.util.List;

import com.example.birthdayapp.ContactEntryContract.ContactEntry;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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
        setContentView(R.layout.activity_contacts);
		startEditing();
		
        contactsListView = (ListView)findViewById(R.id.contactsListView);
        contactDbHelper = new ContactDbHelper(this);
        //addEntriesToDb();
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
		intent.putExtra("birthdate", currContact.getBirthDate());
        intent.putExtra("name", currContact.getName());
		startActivityForResult(intent, EDIT_CODE);
    }
    
	public void startEditing() {
		Intent intent = new Intent(this, EditActivity.class);
//		intent.putExtra("create", true);
		intent.putExtra("birthdate", 893579071000L);
        intent.putExtra("name", "Itay Maman");
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
              String name = data.getStringExtra("name");
              long bd = data.getLongExtra("birthdate", 0);
              Toast.makeText(this, "!!! " + name + ", born " + bd, Toast.LENGTH_LONG).show();
          } 
          break; 
        } 
      }
    }

}
