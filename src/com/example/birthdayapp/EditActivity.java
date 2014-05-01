package com.example.birthdayapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends ActionBarActivity {

    private Button birthdateButton;
    private DatePickerFragment datePicker;
    private EditText nameEdit;
    private long birthDateInMillis = 0;
    private EditText emailEdit;
    private long contactId = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        nameEdit = (EditText) findViewById(R.id.edit_name);
        emailEdit = (EditText) findViewById(R.id.edit_email);
        
        datePicker = new DatePickerFragment();
        Bundle extras = this.getIntent().getExtras();
        String name = "???";
        String emailAddress = "???";
        long birthdateMillis = new Date().getTime();
        contactId = -1;
        if (extras.getBoolean("create",  false)) {
            
        } else {
            contactId = extras.getLong(Items.CONTACT_ID);
            name = extras.getString(Items.ITEM_NAME, name);
            nameEdit.setText(name);
            
            emailAddress = extras.getString(Items.ITEM_EMAIL, emailAddress);
            emailEdit.setText(emailAddress);
            
            birthdateMillis = (Long) extras.get(Items.ITEM_BIRTHDATE);
            datePicker.setTime(birthdateMillis);
        }
        
        birthdateChanged(birthdateMillis);
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

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
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
        if (id == R.id.action_done) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Items.CONTACT_ID, contactId);
            resultIntent.putExtra(Items.ITEM_NAME, nameEdit.getText().toString());
            resultIntent.putExtra(Items.ITEM_BIRTHDATE, birthDateInMillis);
            resultIntent.putExtra(Items.ITEM_EMAIL, emailEdit.getText().toString());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
        
        if (id == R.id.action_cancel) {
            this.finish();
        }
        
        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        datePicker.show(getFragmentManager(), "datePicker");
    }
}
