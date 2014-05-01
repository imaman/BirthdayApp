package com.example.birthdayapp;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.os.Build;

public class ContactsActivity extends ActionBarActivity {

	private ContactDbHelper contactDbHelper;
	private Cursor contactsCursor;
	private ListView contactsList;
	private ContactsAdapter contactsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        contactsList = (ListView)findViewById(R.id.contactsListView);
        contactDbHelper = new ContactDbHelper(this);
//        addEntriesToDb();
//        contactsCursor = contactDbHelper.getCursor();
//        contactsAdapter = new ContactsAdapter(this, contactsCursor, 0);
//        contactsList.setAdapter(contactsAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
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
     	contactDbHelper.addEntry("Shai Sabag", 100000);
     	contactDbHelper.addEntry("Itai Maman", 200000);
    }
    
    public void initCursor() {
    	
    }

}
