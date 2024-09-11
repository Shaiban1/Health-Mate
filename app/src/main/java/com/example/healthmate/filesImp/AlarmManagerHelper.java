package com.example.healthmate.filesImp;

// AlarmManagerHelper.java
import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class AlarmManagerHelper {
    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntent;

    public static void setAlarm(Context context, long triggerAtMillis, int requestCode, long repeatInterval, String ringtoneUri) {
        Log.d("AlarmManagerHelper", "Setting alarm for " + triggerAtMillis);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderAlarmReceiver.class);
        intent.putExtra("ringtoneUri", ringtoneUri);
        pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                // Schedule the alarm
                try {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                } catch (SecurityException e) {
                    // Handle exception if something goes wrong
                    Log.e("AlarmError", "Cannot schedule exact alarm", e);
                }
            } else {
            }
        }
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, repeatInterval, pendingIntent);
    }


    public static void cancelAlarm(Context context, int requestCode) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("AlarmManagerHelper", "Alarm canceled for requestCode " + requestCode);
        } else {
            Log.d("AlarmManagerHelper", "No alarm found for requestCode " + requestCode);
        }
    }

}