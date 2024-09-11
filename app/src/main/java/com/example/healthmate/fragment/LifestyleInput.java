package com.example.healthmate.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.healthmate.R;

public class LifestyleInput extends Fragment {

    private RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lifestyle_input, container, false);

        radioGroup = view.findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = view.findViewById(checkedId);
                if (radioButton != null) {
                    // You can display a toast or perform other actions
                    Toast.makeText(getContext(), "Selected: " + radioButton.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // Method to get the selected lifestyle option
    public String getSelectedLifestyle() {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId != -1) {
            RadioButton radioButton = radioGroup.findViewById(checkedId);
            return radioButton != null ? radioButton.getText().toString() : null;
        }
        return null; // Return null or default value if nothing is selected
    }
}
