package com.example.birthdayapp;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
        private long entryId;
        private String name;
        private String email;
        private long birthDate;
        private String imageFileName;
        
        public Contact(Cursor cursor) {
        	this.entryId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_ENTRY_ID));
        	this.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME));
        	this.birthDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_BIRTH_DATE));
        	this.email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_EMAIL));
        	this.imageFileName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_IMAGE));      	
        }
        
        public Contact(Context context, String name, long birthDate, String email, 
        		Bitmap image) {
        	this.entryId = System.currentTimeMillis();
        	this.name = name;
        	this.birthDate = birthDate;
        	this.email = email;
        	setImage(context, image);
        }
        
        public long getEntryId() {
        	return entryId;
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

        public void setImage(Context context, Bitmap image) {
	    	if (image != null) {
	      	  try {
	      		String fileName = String.valueOf(entryId)+".png";
	      	    FileOutputStream outputStream = 
	      	    		context.openFileOutput(fileName, Context.MODE_PRIVATE);
	      		image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
	     			outputStream.close();
	     			this.imageFileName = fileName;      			
	          } catch (Exception e) {
	        	e.printStackTrace();
	          }
	      	} else {
	      		if (imageFileName != null && imageFileName.length() > 0) {
	      			new File(imageFileName).delete();
	      		}
	      	}
        }
        
        public Bitmap getImage() {
        	if (imageFileName != null && imageFileName.length() > 0)
        		return BitmapFactory.decodeFile(imageFileName);
        	return null;
        }

        Calendar nextBirthday(Calendar now) {
            long dob = getBirthDate();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dob);
            cal.set(Calendar.YEAR, now.get(Calendar.YEAR));
            
            if (cal.before(now)) {
                cal.set(Calendar.YEAR, now.get(Calendar.YEAR) + 1);
            }
            return cal;
        }
     }
}
