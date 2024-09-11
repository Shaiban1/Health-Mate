package com.example.healthmate.interfaces;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.healthmate.models.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("SELECT * FROM reminders")
    LiveData<List<Reminder>> getAllReminders();

    @Query("SELECT * FROM reminders WHERE day = :day")
    LiveData<List<Reminder>> getRemindersByDay(String day);

    @Query("SELECT * FROM reminders WHERE id = :id")
    LiveData<Reminder> getReminderById(int id);

    @Query("SELECT * FROM reminders")
    DataSource.Factory<Integer, Reminder> getAllRemindersPaged();
}