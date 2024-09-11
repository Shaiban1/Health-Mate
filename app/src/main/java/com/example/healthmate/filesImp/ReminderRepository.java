    package com.example.healthmate.filesImp;

    import android.app.Application;
    import android.os.AsyncTask;
    import android.util.Log;

    import androidx.lifecycle.LiveData;
    import androidx.paging.LivePagedListBuilder;
    import androidx.paging.PagedList;

    import com.example.healthmate.MyApp;
    import com.example.healthmate.database.ReminderDatabase;
    import com.example.healthmate.interfaces.ReminderDao;
    import com.example.healthmate.models.Reminder;

    import java.util.List;

    public class ReminderRepository {
        private ReminderDao reminderDao;
        private LiveData<List<Reminder>> allReminders;

        public ReminderRepository(Application application) {
            ReminderDatabase database = ReminderDatabase.getDatabase(application);
            reminderDao = database.reminderDao();
            allReminders = reminderDao.getAllReminders();
            reminderDao = ((MyApp) application).getDatabase().reminderDao();
        }

        public LiveData<PagedList<Reminder>> getAllRemindersPaged() {
            return new LivePagedListBuilder<>(reminderDao.getAllRemindersPaged(), 20).build();
        }

        public LiveData<List<Reminder>> getAllReminders() {
            return allReminders;
        }

        public void insert(Reminder reminder) {
            new InsertAsyncTask(reminderDao).execute(reminder);
        }

        public void update(Reminder reminder) {
            new UpdateAsyncTask(reminderDao).execute(reminder);
        }

        public void delete(Reminder reminder) {
            new DeleteAsyncTask(reminderDao).execute(reminder);
        }

        public ReminderDao getReminderDao() {
            return reminderDao;
        }

        private static class InsertAsyncTask extends AsyncTask<Reminder, Void, Void> {
            private ReminderDao reminderDao;

            public InsertAsyncTask(ReminderDao reminderDao) {
                this.reminderDao = reminderDao;
            }

            @Override
            protected Void doInBackground(Reminder... reminders) {
                reminderDao.insert(reminders[0]);
                return null;
            }
        }

        private static class UpdateAsyncTask extends AsyncTask<Reminder, Void, Void> {
            private ReminderDao reminderDao;

            public UpdateAsyncTask(ReminderDao reminderDao) {
                this.reminderDao = reminderDao;
            }

            @Override
            protected Void doInBackground(Reminder... reminders) {
                reminderDao.update(reminders[0]);
                return null;
            }
        }

        private static class DeleteAsyncTask extends AsyncTask<Reminder, Void, Void> {
            private ReminderDao reminderDao;

            public DeleteAsyncTask(ReminderDao reminderDao) {
                this.reminderDao = reminderDao;
            }

            @Override
            protected Void doInBackground(Reminder... reminders) {
                reminderDao.delete(reminders[0]);
                return null;
            }
        }


    }