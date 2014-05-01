package com.example.birthdayapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.birthdayapp.ContactEntryContract.ContactEntry;


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
        Notification noti = new Notification.Builder(this)
            .setContentTitle("Birthday")
            .setContentText(text == null ? "No upcoming birthdays" : text)
            .setSmallIcon(R.drawable.ic_launcher)
            .build();

        mNM.notify(NOTIFICATION, noti);
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
            long thresholdInMillis = params[0];
            
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            
            long closest = Long.MAX_VALUE;
            ContactEntry candidate = null;
            Calendar nextBirthday = null;
            String s = "";
            Set<String> set = new HashSet<String>();
            
            for (ContactEntry curr : contactDbHelper.getContacts()) {
                long dob = curr.getBirthDate();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(dob);
                cal.set(Calendar.YEAR, now.get(Calendar.YEAR));
                
                set.add(cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH));
                
                long diff = cal.getTimeInMillis() - now.getTimeInMillis();
                if (diff < 0) {
                    cal.set(Calendar.YEAR, now.get(Calendar.YEAR) + 1);
                    diff = cal.getTimeInMillis() - now.getTimeInMillis();
                }
                s += cal.getTime() + ", ";
                
                if (candidate == null || diff < closest) {
                    closest = diff;
                    candidate = curr;
                    nextBirthday = cal;
                }
            }

/*            ArrayList<String> temp = new ArrayList<String>(set);
            Collections.sort(temp);
            return temp.toString();
*/            
            if (candidate == null)
                return null;
            
            long days = Math.round(closest / DAY_IN_MILLIS);
            return "Upcoming birthday: " + candidate.getName() + " in " + days + " day" + ((days == 1) ? "" : "s");  
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(BirthdayNotificationService.this, result, Toast.LENGTH_LONG).show();
            showNotification(result);
        }
    }
}


