package com.example.healthmate.adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.interfaces.OnBookAppointmentListener;
import com.example.healthmate.models.Doctor;

import java.util.List;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {

    private List<Doctor> doctorList;
    private OnBookAppointmentListener listener;

    public DoctorsAdapter(List<Doctor> doctorList, OnBookAppointmentListener listener) {
        this.doctorList = doctorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.doctorName.setText(doctor.getName());
        holder.doctorSpecialization.setText(doctor.getSpecialization());
        holder.doctorImage.setImageResource(doctor.getImageResource());

        // Handle "Book Appointment" button click
        holder.bookAppointmentButton.setOnClickListener(v -> listener.onBookAppointment(doctor));
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public void updateDoctorList(List<Doctor> updatedDoctorList) {
        this.doctorList = updatedDoctorList;
        notifyDataSetChanged();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {

        ImageView doctorImage;
        TextView doctorName;
        TextView doctorSpecialization;
        Button bookAppointmentButton;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorImage = itemView.findViewById(R.id.doctor_image);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialization = itemView.findViewById(R.id.doctor_specialization);
            bookAppointmentButton = itemView.findViewById(R.id.book_appointment_button);
        }
    }
}
