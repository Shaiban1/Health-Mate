package com.example.healthmate.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText editUsername, editEmail, editPhone;
    private Spinner editAgeGroup, editBloodGroup;
    private RadioGroup editGenderGroup, editLifestyleGroup;
    private Button saveButton, cancelButton;
    private CircleImageView editProfileImage;

    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Initialize Firebase components
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

        // Initialize UI components
        editProfileImage = findViewById(R.id.edit_profile_image);
        editUsername = findViewById(R.id.edit_username);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        editAgeGroup = findViewById(R.id.edit_age_group);
        editBloodGroup = findViewById(R.id.edit_blood_group);
        editGenderGroup = findViewById(R.id.edit_gender_group);
        editLifestyleGroup = findViewById(R.id.edit_lifestyle_group);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);

        // Load existing user data from Firebase
        loadUserProfile();

        // Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        // Cancel button click listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Simply close the activity without saving changes
            }
        });
    }

    private void loadUserProfile() {
        // Fetch user data from Firebase
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user details and populate the fields
                    String username = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String ageGroup = dataSnapshot.child("ageGroup").getValue(String.class);
                    String bloodGroup = dataSnapshot.child("bloodGroup").getValue(String.class);
                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    String lifestyle = dataSnapshot.child("lifestyle").getValue(String.class);

                    // Set the data to the input fields
                    editUsername.setText(username);
                    editEmail.setText(email);
                    editPhone.setText(phone);
                    setSpinnerValue(editAgeGroup, ageGroup);
                    setSpinnerValue(editBloodGroup, bloodGroup);
                    setRadioGroupValue(editGenderGroup, gender);
                    setRadioGroupValue(editLifestyleGroup, lifestyle);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileEditActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserProfile() {
        // Validate input
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String ageGroup = editAgeGroup.getSelectedItem().toString();
        String bloodGroup = editBloodGroup.getSelectedItem().toString();
        String gender = getSelectedRadioValue(editGenderGroup);
        String lifestyle = getSelectedRadioValue(editLifestyleGroup);

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please fill out all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update user profile in Firebase
        userRef.child("name").setValue(username);
        userRef.child("email").setValue(email);
        userRef.child("phone").setValue(phone);
        userRef.child("ageGroup").setValue(ageGroup);
        userRef.child("bloodGroup").setValue(bloodGroup);
        userRef.child("gender").setValue(gender);
        userRef.child("lifestyle").setValue(lifestyle)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileEditActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after saving
                    } else {
                        Toast.makeText(ProfileEditActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Utility methods to set spinner and radio group values based on user data
    private void setSpinnerValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setRadioGroupValue(RadioGroup radioGroup, String value) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.getText().toString().equals(value)) {
                radioButton.setChecked(true);
                break;
            }
        }
    }

    private String getSelectedRadioValue(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        return selectedRadioButton.getText().toString();
    }
}
