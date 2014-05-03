package com.example.birthdayapp;

import java.util.List;

import android.app.ActionBar;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.example.birthdayapp.ContactEntryContract.Contact;


public class ContactsActivity extends ActionBarActivity {

    private static final int EDIT_CODE = 1;

  private ContactDbHelper contactDbHelper;
	private ListView contactsListView;
	private ContactsAdapter contactsAdapter;
	List<Contact> contactsList;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.activity_contacts_custom_action_bar);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.getCustomView().findViewById(R.id.action_add_contact).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });
        
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(this, AlarmReciever.class);
        PendingIntent alaramIntent = PendingIntent.getBroadcast(this, 0, serviceIntent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, 1000 * 5, 
                alaramIntent);
        
        
        setContentView(R.layout.activity_contacts);
		
        contactsListView = (ListView)findViewById(R.id.contactsListView);
        contactDbHelper = new ContactDbHelper(this);
        // TODO: DB Access needs to be taken off the UI thread (via AsyncTask) 
        contactsList = contactDbHelper.listContactsByBirthdays();
        contactsAdapter = new ContactsAdapter(this, contactsList, this);
        contactsListView.setAdapter(contactsAdapter);
        
    }

    public void editContact(Contact contact, boolean update) {
        if (contact == null)
            throw new RuntimeException("contact is null");
        
        Intent intent = new Intent(this, EditActivity.class);
        Bundle bundle = Ui.bundleFromContact(contact);
        bundle.putBoolean("create", !update);     
        intent.putExtra("contact", bundle);
		startActivityForResult(intent, EDIT_CODE);
    }
        
    private Contact contactFromId(long id) {
        for (Contact curr : contactsList) {
            if (curr.id() == id)
                return curr;
        }
        return null;
    }
    
    public boolean deleteContact(final long id_) {
        final Contact contactToDelete = contactFromId(id_);
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
                contactDbHelper.deleteEntry(contactToDelete.id());
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
        
        return super.onOptionsItemSelected(item);
    }

    public void addContact() {
        editContact(new Contact(this, "", 0, "", null), false);
    }
    
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {     
      super.onActivityResult(requestCode, resultCode, data); 
      switch(requestCode) { 
        case (EDIT_CODE) : { 
          if (resultCode == Activity.RESULT_OK) {        	  
        	  Contact returned = Ui.contactFromBundle(this, data.getBundleExtra("contact"));
        	  long id = returned.id();
        	  if (id < 0)
        	      break;
        	  
              Contact existing = contactFromId(id);
              if (existing == null) {
                  contactsList.add(returned);
              } else {
                  int pos = contactsList.indexOf(existing);
                  if (pos >= 0)
                      contactsList.set(pos, returned);
              }        	  
        	  dataChanged();
          } 
          break; 
        } 
      }
    }

    void dataChanged() {
        contactsAdapter.sort(new ContactComparator());
        contactsAdapter.notifyDataSetChanged();
        AlarmReciever.startNotificationService(this);
    }
}
