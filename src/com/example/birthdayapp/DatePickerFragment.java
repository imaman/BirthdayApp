package com.example.birthdayapp;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;


public class DatePickerFragment extends DialogFragment
                            implements DatePickerDialog.OnDateSetListener {
    
    
    private long timeInMillis = 0;

    public void setTime(long millis) {
        this.timeInMillis = millis;
    }

    public long getTime() {
        return timeInMillis;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        timeInMillis = c.getTimeInMillis();
        
        EditActivity callingActivity = (EditActivity) getActivity();
        callingActivity.birthdateChanged(timeInMillis);        
    }
}