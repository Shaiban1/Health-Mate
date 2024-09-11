package com.example.healthmate.fragment;

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


public class FragmentAges extends Fragment {

    private ChipGroup ageChipGroup;
    private String selectedAgeGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ages, container, false);
        ageChipGroup = view.findViewById(R.id.ageChipGroup);

        // Handle selection
        ageChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId != -1) {
                    selectedAgeGroup = getSelectedAgeGroup();
                }
            }
        });

        return view;
    }

    public String getSelectedAgeGroup() {
        int checkedChipId = ageChipGroup.getCheckedChipId();
        if (checkedChipId != -1) {
            Chip selectedChip = ageChipGroup.findViewById(checkedChipId);
            return selectedChip != null ? selectedChip.getText().toString() : null;
        }
        return null; // Return null or default value if nothing is selected
    }

}
