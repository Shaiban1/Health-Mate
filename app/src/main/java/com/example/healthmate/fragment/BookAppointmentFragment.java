package com.example.healthmate.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.healthmate.R;
import com.example.healthmate.models.Doctor;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

public class BookAppointmentFragment extends BottomSheetDialogFragment {

    private static final String ARG_DOCTOR = "doctor";
    private Doctor doctor;
    private DatePicker datePicker;
    private TimePicker timePicker;

    public static BookAppointmentFragment newInstance(Doctor doctor) {
        BookAppointmentFragment fragment = new BookAppointmentFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DOCTOR, doctor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doctor = (Doctor) getArguments().getSerializable(ARG_DOCTOR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_appointment, container, false);

        // Set doctor name
        TextView doctorNameTextView = view.findViewById(R.id.doctor_name);
        doctorNameTextView.setText("Booking Appointment with " + doctor.getName());

        // Initialize DatePicker and TimePicker
        datePicker = view.findViewById(R.id.appointment_date_picker);
        timePicker = view.findViewById(R.id.appointment_time_picker);
        timePicker.setIs24HourView(true);

        // Confirm button click listener
        Button confirmButton = view.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(v -> confirmAppointment());

        return view;
    }

    private void confirmAppointment() {
        // Get selected date
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        // Get selected time
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Format date and time
        String appointmentDate = day + "/" + (month + 1) + "/" + year;
        String appointmentTime = String.format("%02d:%02d", hour, minute);

        // Show confirmation message
        String confirmationMessage = "Appointment booked with " + doctor.getName() +
                " on " + appointmentDate + " at " + appointmentTime;
        Toast.makeText(getContext(), confirmationMessage, Toast.LENGTH_LONG).show();

        // Close the fragment after confirmation
        dismiss();
    }
}
