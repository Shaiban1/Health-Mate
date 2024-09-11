package com.example.healthmate.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.healthmate.R;
import com.example.healthmate.activities.SurveyScreen;
import com.google.android.material.textfield.TextInputEditText;


public class NameFragment extends Fragment {
    private EditText nameEditText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_name, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        return view;
    }

    public String getName() {
        return nameEditText.getText().toString();
    }
}