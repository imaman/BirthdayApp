package com.example.birthdayapp;

import java.util.Calendar;

import android.content.Context;
import android.os.Bundle;

import com.example.birthdayapp.ContactEntryContract.Contact;

public class Ui {

    public static String daysLeftMessage(Calendar now, Contact contact) {
        long days = contact.daysTillNextBirthday(now);
        return days == 0 ? "Today" : ("In " + days + " day" + (days == 1 ? "" : "s"));
    }

    private static void storeContactInBundle(final Contact contact, Bundle bundle) {
        long id = contact.getEntryId();
        bundle.putLong(Items.CONTACT_ID, id);
        bundle.putString(Items.ITEM_NAME, contact.getName());
        bundle.putLong(Items.ITEM_BIRTHDATE, contact.getBirthDate());
        bundle.putString(Items.ITEM_EMAIL, contact.getEmail());
    }

    public static Bundle bundleFromContact(Contact contact) {
        Bundle bundle = new Bundle();
        storeContactInBundle(contact, bundle);
        return bundle;
    }
    
    static Contact contactFromBundle(Context context, Bundle extras) {
        Long id = extras.getLong(Items.CONTACT_ID);
        String name = extras.getString(Items.ITEM_NAME, "???");            
        String emailAddress = extras.getString(Items.ITEM_EMAIL, "???");            
        long birthdateMillis = extras.getLong(Items.ITEM_BIRTHDATE);
        return new Contact(context, id, name, birthdateMillis, emailAddress, null);
    }


}
