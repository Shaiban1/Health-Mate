package com.example.healthmate.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.healthmate.R;
import com.example.healthmate.activities.SurveyScreen;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FragmentBloodGroup extends Fragment {

    private ChipGroup bloodGroupChipGroup;
    private String selectBloodGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_blood_group, container, false);


        bloodGroupChipGroup = view.findViewById(R.id.chipGroupBloodGroup);

        bloodGroupChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId != -1) {
                    selectBloodGroup = getSelectedBloodGroup();
                }
            }
        });


        return view;
    }

    public String getSelectedBloodGroup() {
        int checkedChipId = bloodGroupChipGroup.getCheckedChipId();
        if (checkedChipId != -1) {
            Chip selectedChip = bloodGroupChipGroup.findViewById(checkedChipId);
            return selectedChip != null ? selectedChip.getText().toString() : null;
        }
        return null; // Return null or default value if nothing is selected
    }

}