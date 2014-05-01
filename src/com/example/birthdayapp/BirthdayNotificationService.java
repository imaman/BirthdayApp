package com.example.birthdayapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class BirthdayNotificationService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    @Override
    public void onCreate() {
    
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
        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
        Log.d(this.getClass().getName(), "onDestroy");
    }
}

/*
public class BirthdayNotificationService extends IntentService {

    private NotificationManager mNM;
    
    private int NOTIFICATION = R.string.local_service_started;
    @Override
    public IBinder onBind(Intent arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
    }

    
    
    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);
    }
    
    public BirthdayNotificationService() {
        super("BIRTHDAY_SERVICE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        showNotification();
    }
    
    private void showNotification() {

        Notification noti = new Notification.Builder(this)
            .setContentTitle("title:Happy birthday")
            .setContentText("content")
            .setSmallIcon(R.drawable.ic_launcher)
            .build();

        mNM.notify(NOTIFICATION, noti);
    }
}
*/