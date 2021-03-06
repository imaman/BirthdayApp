package com.example.birthdayapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        startNotificationService(context);
    }

    public static void startNotificationService(Context context) {
        Intent newIntent = new Intent(context, BirthdayNotificationService.class);
        newIntent.putExtra("FROM", "Alarm");
        context.startService(newIntent);
    }
}