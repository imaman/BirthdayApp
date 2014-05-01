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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        nameEdit = (EditText) findViewById(R.id.edit_name);
        
        datePicker = new DatePickerFragment();
        Bundle extras = this.getIntent().getExtras();
        String name = "???";
        long birthdateMillis = new Date().getTime();
        if (extras.getBoolean("create",  false)) {
            
        } else {
            name = (String) extras.get("name");
            if (name != null)
                nameEdit.setText(name);
            birthdateMillis = (Long) extras.get("birthdate");
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
            resultIntent.putExtra("name", nameEdit.getText().toString());
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
