package com.example.healthmate.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.healthmate.R;
import com.example.healthmate.adapters.DoctorsAdapter;
import com.example.healthmate.interfaces.OnBookAppointmentListener;
import com.example.healthmate.models.Doctor;
import com.google.android.material.chip.ChipGroup;


import java.util.ArrayList;
import java.util.List;

public class DoctorsFragment extends Fragment implements OnBookAppointmentListener {

    private RecyclerView doctorsRecyclerView;
    private DoctorsAdapter doctorsAdapter;
    private List<Doctor> doctorList;
    private List<Doctor> filteredDoctorList;
    private EditText searchBar;
    private ImageView filterButton;
    private ChipGroup chipGroupSpecialization;
    private boolean isFilterVisible = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize doctor list with static data
        doctorList = new ArrayList<>();
        doctorList.add(new Doctor("Dr. Devi Prasad Shetty", "Cardiologist", R.drawable.doct1));
        doctorList.add(new Doctor("Dr. Naresh Trehan", "Dermatologist", R.drawable.dcot2));
        doctorList.add(new Doctor("Dr. P. Raghu Ram", "Neurologist", R.drawable.doct3));
        doctorList.add(new Doctor("Dr. Ashok Seth", "Orthopedist", R.drawable.doct4));
        doctorList.add(new Doctor("Dr. Nandkishore Kapadia", "Pediatrician", R.drawable.doct5));
        doctorList.add(new Doctor("Dr. B.K. Misra", "Breast Surgery", R.drawable.doct6));
        doctorList.add(new Doctor("Dr. Subhash Gupta", " Interventional Cardiology", R.drawable.doct7));
        doctorList.add(new Doctor("Dr. Shashank Joshi", " Neurosurgery", R.drawable.doct8));
        doctorList.add(new Doctor("Dr. Suresh H. Advani", "Orthopedist", R.drawable.doct9));
        doctorList.add(new Doctor("Dr. Ashok Rajgopal", "Endocrinology", R.drawable.doct10));


        filteredDoctorList = new ArrayList<>(doctorList);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor, container, false);

        // Set up RecyclerView
        doctorsRecyclerView = view.findViewById(R.id.doctors_recycler_view);
        doctorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Pass this (DoctorsFragment) as the listener to the adapter
        doctorsAdapter = new DoctorsAdapter(doctorList, this);
        doctorsRecyclerView.setAdapter(doctorsAdapter);

        searchBar = view.findViewById(R.id.search_bar);
        filterButton = view.findViewById(R.id.filter_button);
        chipGroupSpecialization = view.findViewById(R.id.chip_group_specialization);

        // Set up search functionality
        setupSearchBar();

        // Set up filter button to toggle visibility of chips
        setupFilterButton();

        // Set up chip filtering
        setupFilterChips();



        return view;
    }


    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDoctors(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void filterDoctors(String query) {
        List<Doctor> filteredList = new ArrayList<>();
        for (Doctor doctor : doctorList) {
            if (doctor.getName().toLowerCase().contains(query.toLowerCase()) ||
                    doctor.getSpecialization().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(doctor);
            }
        }
        // Update the adapter with the filtered list
        doctorsAdapter.updateDoctorList(filteredList);
    }



    // Toggle filter chip visibility
    private void setupFilterButton() {
        filterButton.setOnClickListener(v -> {
            isFilterVisible = !isFilterVisible;
            chipGroupSpecialization.setVisibility(isFilterVisible ? View.VISIBLE : View.GONE);
        });
    }

    // Set up specialization chip click listeners to filter the list
    private void setupFilterChips() {
        chipGroupSpecialization.setOnCheckedChangeListener((group, checkedId) -> {
            String filter = "";
            if (checkedId == R.id.chip_cardiologist) {
                filter = "Cardiologist";
            } else if (checkedId == R.id.chip_dermatologist) {
                filter = "Dermatologist";
            } else if (checkedId == R.id.chip_neurologist) {
                filter = "Neurologist";
            }
            applyChipFilter(filter);
        });
    }

    private void applyChipFilter(String specialization) {
        List<Doctor> filteredList = new ArrayList<>();
        for (Doctor doctor : doctorList) {
            if (doctor.getSpecialization().equalsIgnoreCase(specialization)) {
                filteredList.add(doctor);
            }
        }
        // Update the adapter with the filtered list
        doctorsAdapter.updateDoctorList(filteredList);
    }



    @Override
    public void onBookAppointment(Doctor doctor) {
        // Handle the book appointment action here
        BookAppointmentFragment bookAppointmentFragment = BookAppointmentFragment.newInstance(doctor);
        bookAppointmentFragment.show(getParentFragmentManager(), "bookAppointment");
    }


}
