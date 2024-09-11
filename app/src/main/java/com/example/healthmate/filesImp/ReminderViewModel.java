package com.example.healthmate.filesImp;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.healthmate.interfaces.ReminderDao;
import com.example.healthmate.models.Reminder;

import java.util.List;

public class ReminderViewModel extends ViewModel {
    private ReminderRepository repository;
    private LiveData<List<Reminder>> allReminders;
    private ReminderDao reminderDao;

    public ReminderViewModel(Application application) {
        repository = new ReminderRepository(application);
        reminderDao = repository.getReminderDao();
        allReminders = repository.getAllReminders();
    }

    public LiveData<List<Reminder>> getAllReminders() {
        return allReminders;
    }

    public void insert(Reminder reminder) {
        repository.insert(reminder);
    }

    public void update(Reminder reminder) {
        repository.update(reminder);
    }

    public void delete(Reminder reminder) {
        repository.delete(reminder);
    }

    public LiveData<PagedList<Reminder>> getAllRemindersPaged() {
        return new LivePagedListBuilder<>(reminderDao.getAllRemindersPaged(), 20).build();
    }
}