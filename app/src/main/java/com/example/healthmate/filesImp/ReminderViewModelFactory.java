package com.example.healthmate.filesImp;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ReminderViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application application;

    public ReminderViewModelFactory(Application application) {
        this.application = application;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ReminderViewModel(application);
    }
}