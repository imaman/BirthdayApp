package com.example.birthdayapp;

import java.util.Calendar;

import com.example.birthdayapp.ContactEntryContract.Contact;

public class Ui {

    public static String daysLeftMessage(Calendar now, Contact contact) {
        long days = contact.daysTillNextBirthday(now);
        return days == 0 ? "Today" : ("In " + days + " day" + (days == 1 ? "" : "s"));
    }

}
