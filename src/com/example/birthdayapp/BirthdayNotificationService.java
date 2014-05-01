package com.example.birthdayapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


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
        showNotification();
    
        Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
        Log.d(this.getClass().getName(), "onCreate");
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(this.getClass().getName(), "onStart");  
    }
    
    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);
        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
        Log.d(this.getClass().getName(), "onDestroy");
    }
    
    private void showNotification() {

        Notification noti = new Notification.Builder(this)
            .setContentTitle("title:Happy birthday")
            .setContentText("content")
            .setSmallIcon(R.drawable.ic_launcher)
            .build();

        mNM.notify(NOTIFICATION, noti);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
}
