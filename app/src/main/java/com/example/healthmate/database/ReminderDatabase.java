package com.example.healthmate.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.healthmate.interfaces.ReminderDao;
import com.example.healthmate.models.Reminder;

@Database(entities = {Reminder.class}, version = 2, exportSchema = false)
public abstract class ReminderDatabase extends RoomDatabase {

    private static ReminderDatabase INSTANCE;

    public abstract ReminderDao reminderDao();

    public static ReminderDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ReminderDatabase.class, "app_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
