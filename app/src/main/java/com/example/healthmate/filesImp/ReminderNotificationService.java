package com.example.healthmate.filesImp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ReminderNotificationService extends Service {
    public ReminderNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Display the notification using the NotificationHelper
        NotificationHelper.createNotification(this, "Reminder", "Take your medicine!");
        return super.onStartCommand(intent, flags, startId);
    }
}