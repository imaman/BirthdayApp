package com.example.birthdayapp;

import java.util.Calendar;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;


public class BirthdayNotificationService extends Service {

    private NotificationManager mNM;
    
    private int NOTIFICATION = R.string.local_service_started;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification(null);
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
            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            int secs = cal.get(Calendar.SECOND) % 30;
            if (secs < 15) {
                return "YES";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            showNotification(result);
        }
    }
}


