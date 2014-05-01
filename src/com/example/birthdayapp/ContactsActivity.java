package com.example.birthdayapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;


public class ContactsActivity extends ActionBarActivity {

    private static final int EDIT_CODE = 1;

	private ContactDbHelper contactDbHelper;
	private Cursor contactsCursor;
	private ListView contactsList;
	private ContactsAdapter contactsAdapter;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(this, AlarmReciever.class);
        PendingIntent alaramIntent = PendingIntent.getBroadcast(this, 0, serviceIntent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, 1000 * 5, 
                alaramIntent);
        
        
        setContentView(R.layout.activity_contacts);
		startEditing("imaman@google.com", "Itay Maman", 893579071000L);
		
        contactsList = (ListView)findViewById(R.id.contactsListView);
        contactDbHelper = new ContactDbHelper(this);
        addEntriesToDb();
//        contactsCursor = contactDbHelper.getCursor();
//        contactsAdapter = new ContactsAdapter(this, contactsCursor, 0);
//        contactsList.setAdapter(contactsAdapter);
    }


	public void startEditing(String email, String name, long birthdateInMillis) {
		Intent intent = new Intent(this, EditActivity.class);
//		intent.putExtra("create", true);
		intent.putExtra(Items.ITEM_BIRTHDATE, birthdateInMillis);
        intent.putExtra(Items.ITEM_NAME, name); 
        intent.putExtra(Items.ITEM_EMAIL, email);
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
     	contactDbHelper.addEntry("Shai Sabag", 100000);
     	contactDbHelper.addEntry("Itai Maman", 200000);
    }
    
    public void initCursor() {
    	
    }
    
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {     
      super.onActivityResult(requestCode, resultCode, data); 
      switch(requestCode) { 
        case (EDIT_CODE) : { 
          if (resultCode == Activity.RESULT_OK) { 
              String name = data.getStringExtra(Items.ITEM_NAME);
              long bd = data.getLongExtra(Items.ITEM_BIRTHDATE, 0);
              String email = data.getStringExtra(Items.ITEM_EMAIL);
              Toast.makeText(this, "Got: " + name + ", " + bd + ", " + email, Toast.LENGTH_LONG).show();
            
          } 
          break; 
        } 
      }
    }

}
