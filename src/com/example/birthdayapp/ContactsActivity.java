package com.example.birthdayapp;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import a_vcard.android.syncml.pim.PropertyNode;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

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
        
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/x-vcard".equals(type)) {
                new AsyncTask<Uri, Integer, Contact>() {
                    @Override
                    protected Contact doInBackground(Uri... params) {
                        try {
                            return handleSendVcard(params[0]);
                        } catch (Exception e) {
                            Log.e(getClass().getName(), "Processing of vcard failed",  e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Contact newContact) {
                        if (newContact == null)
                            return;
                        contactsList.add(newContact);
                        editContact(newContact.getEntryId());
                    }
                }.execute((Uri) intent.getExtras().get(Intent.EXTRA_STREAM));
            }
        }
    }

    private Contact handleSendVcard(Uri uri) throws Exception {
        InputStream in = getContentResolver().openInputStream(uri);
        StringBuilder buffer = new StringBuilder();
        
        for (Scanner scanner = new Scanner(in); scanner.hasNext(); ) {
            buffer.append(scanner.nextLine()).append("\n");
        }
        
        VDataBuilder builder = new VDataBuilder();

        boolean parsed = new VCardParser().parse(buffer.toString(), "UTF-8", 
                builder);
        if (!parsed) {
            throw new VCardException("Could not parse vCard");
        }

        for (VNode node : builder.vNodeList) {
            Map<String, String> map = new HashMap<String, String>();
            for (PropertyNode curr : node.propList) {
                map.put(curr.propName,  curr.propValue);
            }
            Contact newContact = new Contact(this, map.get("FN"), 0, map.get("EMAIL"), null);
            contactDbHelper.addEntry(newContact);
            return newContact;
        }
        
        return null;
    }

    public void editContact(long id) {
        Intent intent = new Intent(this, EditActivity.class);
        Contact contact = contactFromId(id);
        
        intent.putExtra(Items.CONTACT_ID, contact.getEntryId());
        intent.putExtra(Items.ITEM_BIRTHDATE, contact.getBirthDate());
        intent.putExtra(Items.ITEM_NAME, contact.getName());
        intent.putExtra(Items.ITEM_EMAIL, contact.getEmail());
		startActivityForResult(intent, EDIT_CODE);
    }
        
    private Contact contactFromId(long id) {
        for (Contact curr : contactsList) {
            if (curr.getEntryId() == id)
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
        
        return super.onOptionsItemSelected(item);
    }

    public void addContact() {
        Contact contact = new Contact(this, "", 0, "", null);
        contactDbHelper.addEntry(contact);
        contactsList.add(contact);
        editContact(contact.getEntryId());
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
        	  Contact contact = contactFromId(contactId);
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
        contactsAdapter.sort(new ContactComparator());
        AlarmReciever.startNotificationService(this);
    }
}
