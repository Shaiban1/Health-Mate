package com.example.healthmate.filesImp;

// NotificationHelper.java
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.healthmate.R;

public class NotificationHelper {
    private static NotificationManager notificationManager;

    public static void createNotification(Context context, String title, String message) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "reminder_channel",
                    "Reminder Channel",
                    NotificationManager.IMPORTANCE_HIGH); // You can increase importance level if required
            channel.setDescription("Notifications for medication reminders");
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(context, "reminder_channel")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.outline_notifications_24)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH);  // Add priority to ensure it shows as an alert
        } else {
            notificationBuilder = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.outline_notifications_24)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH);
        }

        notificationManager.notify(1, notificationBuilder.build());
    }
}