package com.example.birthdayapp;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import a_vcard.android.syncml.pim.PropertyNode;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.birthdayapp.ContactEntryContract.Contact;

public class EditActivity extends ActionBarActivity {

    private Button birthdateButton;
    private DatePickerFragment datePicker;
    private EditText nameEdit;
    private long birthDateInMillis = 0;
    private EditText emailEdit;
    private Long contactId = null;
    private boolean update = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.activity_edit_custom_action_bar);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.getCustomView().findViewById(R.id.action_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        actionBar.getCustomView().findViewById(R.id.action_done).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact newContact = new Contact(EditActivity.this,
                        contactId,
                        nameEdit.getText().toString(), 
                        birthDateInMillis, 
                        emailEdit.getText().toString(), 
                        null);
                saveAndExit(newContact);
            }
        });
        nameEdit = (EditText) findViewById(R.id.edit_name);
        emailEdit = (EditText) findViewById(R.id.edit_email);        
        datePicker = new DatePickerFragment();
        contactId = null;
        
        
                
        Intent intent = getIntent();
        boolean isShareIntent = Intent.ACTION_SEND.equals(intent.getAction()) && 
                "text/x-vcard".equals(intent.getType());
        
        if (!isShareIntent) {
            extractDetailsFromBundle(intent.getBundleExtra("contact"));
            return;
        }
        
        // Contact shared with this activity
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
            protected void onPostExecute(Contact contact) {
                if (contact == null) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
                populateUiWith(contact);
            }
        }.execute((Uri) intent.getExtras().get(Intent.EXTRA_STREAM));
    }

    protected void saveAndExit(final Contact contact) {
        Toast.makeText(this, 
                (update ? "updating " : "creating ") + contact.getEntryId() + " " + contact.getName(), 
                Toast.LENGTH_LONG).show();
        
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ContactDbHelper db = new ContactDbHelper(EditActivity.this);
                if (update)
                    db.updateEntry(contact);
                else 
                    db.addEntry(contact);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                Intent intent = new Intent();
                intent.putExtra("contact", Ui.bundleFromContact(contact));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }.execute();
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
            return new Contact(this, map.get("FN"), 0, map.get("EMAIL"), null);
        }
        
        return null;
    }

    void extractDetailsFromBundle(Bundle bundle) {
        Contact contact;
        if (bundle.getBoolean("create",  false)) {
            update = false;
            contact = new Contact(this, null, "???", 0, "???", null);            
        } else {
            update = true;
            contact = Ui.contactFromBundle(this, bundle);
        }
        
        populateUiWith(contact);
    }

    public void birthdateChanged(long birthdateMillis) {
        Date date = new Date();
        date.setTime(birthdateMillis);
        
        DateFormat sdf = SimpleDateFormat.getDateInstance();
        View view = findViewById(R.id.birthdate_button);
        birthdateButton = (Button) view;
        birthdateButton.setText(sdf.format(date));
        this.birthDateInMillis = birthdateMillis;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    public void showDatePickerDialog(View v) {
        datePicker.show(getFragmentManager(), "datePicker");
    }

    void populateUiWith(Contact contact) {
        Toast.makeText(this, "populating " + contact.getEntryId() + ", " + contact.getName() + " update?" + update, 
                Toast.LENGTH_LONG).show();
        
        contactId = contact.getEntryId();
        long birthdateMillis = contact.getBirthDate();
        nameEdit.setText(contact.getName());
        emailEdit.setText(contact.getEmail());
        datePicker.setTime(birthdateMillis);
        birthdateChanged(birthdateMillis);
    }
}
