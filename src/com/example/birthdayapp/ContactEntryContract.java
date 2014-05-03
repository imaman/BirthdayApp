package com.example.birthdayapp;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.database.Cursor;

public final class ContactEntryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ContactEntryContract() {}

    /* Inner class that defines the table contents */
    public static class Contact {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_BIRTH_DATE = "birthdate";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_EMAIL = "email";
        private long id;
        private String name;
        private String email;
        private long birthDate;
        private String imageFileName;
        
        public Contact(Cursor cursor) {
        	this.id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_ENTRY_ID));
        	this.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME));
        	this.birthDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_BIRTH_DATE));
        	this.email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_EMAIL));
        	this.imageFileName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_IMAGE));      	
        }
        
        public Contact(String name, long birthDate, String email, 
                String imageFileName) {
            this(null, name, birthDate, email, imageFileName);
        }
        
        public Contact(Long id, String name, long birthDate, String email, String imageFileName) {            
        	this.id = id == null ? System.currentTimeMillis() : id;
        	this.name = name;
        	this.birthDate = birthDate;
        	this.email = email;
        	this.imageFileName = imageFileName;
        }
        
        public String toString() {
            return this.name + " #" + this.id;
        }
        
        public long id() {
        	return id;
        }
        
        public String getName() {
        	return name;
        }
        
        public String getEmail() {
        	return email;
        }
        
        public String getBirthDateAsString() {
        	return DateFormat.getDateInstance().format(new Date(birthDate));
        }
        
        public long getBirthDate() {
        	return birthDate;
        }
        
        public String getImageFileName() {
        	return imageFileName;
        }
        
        public void setName(String name) {
        	this.name = name;
        }
        
        public void setEmail(String email) {
        	this.email = email;
        }

        public void setBirthDate(long birthDate) {
        	this.birthDate = birthDate;
        }

        Calendar nextBirthday(Calendar now) {
            long dob = getBirthDate();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dob);
            cal.set(Calendar.YEAR, now.get(Calendar.YEAR));
            if (cal.before(now)) {
                cal.add(Calendar.YEAR, 1);
            }
            return cal;
        }

        public long daysTillNextBirthday(Calendar now) {
            Calendar nextBirthday = nextBirthday(now);
            long result = daysDiff(now, nextBirthday);
            if (result < 0) {
                result = nextBirthday.getTimeInMillis() - now.getTimeInMillis();
                result = Math.round(result / (1000 * 60 * 60 * 24));
            }
            return result;
        }

        int daysDiff(Calendar begin, Calendar end) {
            return end.get(Calendar.DAY_OF_YEAR) - begin.get(Calendar.DAY_OF_YEAR);
        }
     }
}
