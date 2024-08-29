package com.example.healthmate.fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.healthmate.R;
import com.example.healthmate.models.Reminder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class ReminderDialogFragment extends DialogFragment {

    private EditText reminderName;
    private EditText pillsCount;
    private CheckBox dailyCheckbox;
    private List<CheckBox> dayCheckBoxes;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminder_dialog, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }

        reminderName = view.findViewById(R.id.reminder_name);
        pillsCount = view.findViewById(R.id.pills_count);
        dailyCheckbox = view.findViewById(R.id.daily_checkbox);

        dayCheckBoxes = new ArrayList<>();
        dayCheckBoxes.add(view.findViewById(R.id.monday));
        dayCheckBoxes.add(view.findViewById(R.id.tuesday));
        dayCheckBoxes.add(view.findViewById(R.id.wednesday));
        dayCheckBoxes.add(view.findViewById(R.id.thursday));
        dayCheckBoxes.add(view.findViewById(R.id.friday));
        dayCheckBoxes.add(view.findViewById(R.id.saturday));
        dayCheckBoxes.add(view.findViewById(R.id.sunday));

        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_btn_dialog);

        saveButton.setOnClickListener(v -> saveReminder());
        cancelButton.setOnClickListener(v -> dismiss());


        return view;
    }

    private void saveReminder() {

        String name = reminderName.getText().toString().trim();
        int pills = Integer.parseInt(pillsCount.getText().toString().trim());
        boolean isDaily = dailyCheckbox.isChecked();
        List<String> selectedDays = new ArrayList<>();

        if (!isDaily) {
            for (CheckBox dayCheckBox : dayCheckBoxes) {
                if (dayCheckBox.isChecked()) {
                    selectedDays.add(dayCheckBox.getText().toString());
                }
            }
        }

        String reminderId = FirebaseDatabase.getInstance().getReference("Reminders").push().getKey();
        Reminder reminder = new Reminder(reminderId, name, pills, isDaily, selectedDays, "morning");
        saveReminderToFirebase(reminder);
    }

    private void saveReminderToFirebase(Reminder reminder) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Reminders").child(userId).child(reminder.getReminderId());
        database.setValue(reminder).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(getContext(),"Reminder saved successfully", Toast.LENGTH_SHORT).show();
                dismiss();

                if(getTargetFragment() != null) {
                    ((MorningFragment) getTargetFragment()).loadReminderFromFireBase();
                }
            } else {
                Toast.makeText(getContext(), "Failed to save reminder", Toast.LENGTH_SHORT).show();
            }
        });
    }
}