package com.example.birthdayapp;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.example.birthdayapp.ContactEntryContract.Contact;


public class BirthdayNotificationService extends Service {

    private NotificationManager mNM;
    
    private int NOTIFICATION = R.string.local_service_started;

    private ContactDbHelper contactDbHelper;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification(null);
        contactDbHelper = new ContactDbHelper(this);
        Log.d(this.getClass().getName(), "onCreate");
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(this.getClass().getName(), "onStart");  
    }
    
    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);
        Log.d(this.getClass().getName(), "onDestroy");
    }
    
    private void showNotification(String text) {
        Builder builder = new Notification.Builder(this)
            .setContentTitle("Birthday")
            .setContentText(text == null ? "No upcoming birthdays" : text)
            .setSmallIcon(R.drawable.ic_launcher);
        
        Intent resultIntent = new Intent(this, ContactsActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        
        mNM.notify(NOTIFICATION, builder.build());
    }
    
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.

        
        new CheckUpcomingBirthdaysTask().execute(400 * DAY_IN_MILLIS);
        
        return START_STICKY;
    }
    
    class CheckUpcomingBirthdaysTask extends AsyncTask<Long, Integer, String> {

        @Override
        protected String doInBackground(Long... params) {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            
            List<Contact> contacts = contactDbHelper.getContacts();
            if (contacts.size() == 0)
                return null;
            
            Contact candidate = contacts.get(0);
            long days = candidate.daysTillNextBirthday(Calendar.getInstance());
            return "Upcoming birthday: " + candidate.getName() + " in " + days + " day" + ((days == 1) ? "" : "s");  
        }

        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(BirthdayNotificationService.this, result, Toast.LENGTH_LONG).show();
            showNotification(result);
        }
    }
}


