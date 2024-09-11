package com.example.healthmate.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.healthmate.R;
import com.example.healthmate.database.ReminderDatabase;
import com.example.healthmate.interfaces.ReminderDao;
import com.example.healthmate.models.Reminder;

import java.util.Locale;

public class ReminderDialogFragment extends DialogFragment {

    private EditText reminderName;
    private TextView pillCountTextView;
    private Spinner dayPicker;
    private Button saveButton;
    private Button cancelButton;
    private Button selectTimeButton;
    private Spinner ringtoneSpinner;
    private int pillCount = 0;
    private String selectedTime = "";
    private String selectedRingtone = "Ringtone 1";
    private String[] ringtoneOptions = {"Ringtone 1", "Ringtone 2", "Ringtone 3", "Ringtone 4", "Ringtone 5"};
    private MediaPlayer mediaPlayer;
    ImageButton playRingtoneButton;

    public interface ReminderDialogListener {
        void onReminderSaved(Reminder reminder);
    }

    private ReminderDialogListener listener;

    public ReminderDialogFragment(ReminderDialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_dialog, container, false);

        reminderName = view.findViewById(R.id.reminder_name);
        pillCountTextView = view.findViewById(R.id.pill_count_textview);
        dayPicker = view.findViewById(R.id.days_spinner);
        saveButton = view.findViewById(R.id.save_button);
        cancelButton = view.findViewById(R.id.cancel_btn_dialog);
        selectTimeButton = view.findViewById(R.id.select_time_button);
        playRingtoneButton = view.findViewById(R.id.play_ringtone_button);

        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.days_array,
                android.R.layout.simple_spinner_item
        );
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayPicker.setAdapter(dayAdapter);

        ringtoneSpinner = view.findViewById(R.id.ringtone_spinner);
        ArrayAdapter<String> ringtoneAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_spinner_item,
                ringtoneOptions
        );
        ringtoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ringtoneSpinner.setAdapter(ringtoneAdapter);
        ringtoneSpinner.setSelection(-1);
        ringtoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Store the selected ringtone in SharedPreferences
                if (position != -1) { // Only play the ringtone when a ringtone is selected
                    SharedPreferences prefs = getContext().getSharedPreferences("app_prefs", MODE_PRIVATE);
                    prefs.edit().putString("selected_ringtone", ringtoneOptions[position]).apply();
                    selectedRingtone = ringtoneOptions[position];

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected
            }
        });


        selectTimeButton.setOnClickListener(v -> showTimePickerDialog());

        view.findViewById(R.id.increment_button).setOnClickListener(v -> {
            pillCount++;
            updatePillCount();
        });

        view.findViewById(R.id.decrement_button).setOnClickListener(v -> {
            if (pillCount > 0) pillCount--;
            updatePillCount();
        });

        playRingtoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRingtone != null && !selectedRingtone.isEmpty()) {
                    int ringtoneResId = getRingtoneResId(selectedRingtone);
                    playRingtone(ringtoneResId);
                }
            }
        });

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        saveButton.setOnClickListener(v -> saveReminder());

        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private void updatePillCount() {
        pillCountTextView.setText(String.valueOf(pillCount));
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                            (hourOfDay % 12 == 0 ? 12 : hourOfDay % 12),
                            minute,
                            (hourOfDay < 12) ? "AM" : "PM");
                    selectTimeButton.setText(selectedTime);
                },
                12, 0, false);
        timePickerDialog.show();
    }

    private void saveReminder() {
        String name = reminderName.getText().toString();
        String day = dayPicker.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(selectedTime)) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ringtoneSpinner != null) { // check if ringtoneSpinner is null before accessing it
            selectedRingtone = (String) ringtoneSpinner.getSelectedItem();
        } else {
            selectedRingtone = "Ringtone 1"; // set a default ringtone if ringtoneSpinner is null
        }

        if (reminderName != null) { // check if reminderName is null before accessing it
            name = reminderName.getText().toString();
        } else {
            name = ""; // set a default name if reminderName is null
        }

        if (dayPicker != null) { // check if dayPicker is null before accessing it
            day = dayPicker.getSelectedItem().toString();
        } else {
            day = ""; // set a default day if dayPicker is null
        }

        String ringtoneResId = String.valueOf(getRingtoneResId(selectedRingtone));

        Reminder reminder = new Reminder(name, pillCount, selectedTime, day, ringtoneResId);

        new SaveReminderTask(getContext(), reminder).execute();
    }

    private int getRingtoneResId(String ringtoneName) {
        if (ringtoneName == null) {
            return R.raw.default_ringtone; // return a default value if ringtoneName is null
        }
        if (ringtoneName.equals("Ringtone 1")) {
            return R.raw.ringtone_one;
        } else if (ringtoneName.equals("Ringtone 2")) {
            return R.raw.ringtone_two;
        } else if (ringtoneName.equals("Ringtone 3")) {
            return R.raw.ringtone_three;
        } else if (ringtoneName.equals("Ringtone 4")) {
            return R.raw.ringtone_four;
        } else if (ringtoneName.equals("Ringtone 5")) {
            return R.raw.ringtone_five;
        } else {
            return R.raw.default_ringtone;
        }
    }

    private void playRingtone(int ringtoneResId) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getContext(), ringtoneResId);
        mediaPlayer.start();
    }



    private class SaveReminderTask extends AsyncTask<Reminder, Void, Void> {
        private Reminder reminder;
        private ReminderDao reminderDao;

        public SaveReminderTask(Context context, Reminder reminder) {
            this.reminder = reminder;
            this.reminderDao = ReminderDatabase.getDatabase(context).reminderDao();
        }

        @Override
        protected Void doInBackground(Reminder... reminders) {
            if (reminders.length > 0) {
                reminderDao.insert(reminders[0]);
            } else {
                Log.e("SaveReminderTask", "No reminder data to save.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.onReminderSaved(reminder);
            dismiss();
        }
    }
}