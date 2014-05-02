package com.example.birthdayapp;

import java.util.Calendar;
import java.util.Comparator;

import com.example.birthdayapp.ContactEntryContract.Contact;

public class ContactComparator implements Comparator<Contact> {
    private final Calendar now = Calendar.getInstance();

    @Override
    public int compare(Contact lhs, Contact rhs) {
        long lhsDays = lhs.daysTillNextBirthday(now);
        long rhsDays = rhs.daysTillNextBirthday(now);
        return Long.compare(lhsDays, rhsDays);
    }
}