package com.example.healthmate.filesImp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class ReminderAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ReminderAlarmReceiver", "Received broadcast intent");
        try {
            String ringtoneUri = intent.getStringExtra("ringtoneUri");
            if (ringtoneUri != null) {
                Uri ringtoneUriObject = Uri.parse(ringtoneUri);
                Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUriObject);
                ringtone.play();
            }
        } catch (Exception e) {
            Log.e("ReminderAlarmReceiver", "Error playing ringtone", e);
        }

        NotificationHelper.createNotification(context, "Reminder", "Take your medicine!");
    }
}