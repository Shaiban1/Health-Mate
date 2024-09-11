package com.example.healthmate;

import android.app.Application;

import com.example.healthmate.database.ReminderDatabase;


public class MyApp extends Application {


        private ReminderDatabase database;

        @Override
        public void onCreate() {
            super.onCreate();
            database = ReminderDatabase.getDatabase(this);
        }

        public ReminderDatabase getDatabase() {
            return database;
        }
    }

