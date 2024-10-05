package com.example.healthmate.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class ProfileEditActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPhone, editAge, editGender, editBloodGroup, editLifestyle;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Initialize UI components
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        editAge = findViewById(R.id.edit_age);
        editGender = findViewById(R.id.edit_gender);
        editBloodGroup = findViewById(R.id.edit_blood_group);
        editLifestyle = findViewById(R.id.edit_lifestyle);
        saveButton = findViewById(R.id.save_button);

        // Fetch user details from Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String ageGroup = dataSnapshot.child("ageGroup").getValue(String.class);
                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    String bloodGroup = dataSnapshot.child("bloodGroup").getValue(String.class);
                    String lifestyle = dataSnapshot.child("lifestyle").getValue(String.class);

                    // Pre-fill data in the fields
                    editName.setText(username);
                    editEmail.setText(email);
                    editPhone.setText(phone);
                    editAge.setText(ageGroup);
                    editGender.setText(gender);
                    editBloodGroup.setText(bloodGroup);
                    editLifestyle.setText(lifestyle);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ProfileEditActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Save button click listener
        saveButton.setOnClickListener(v -> saveProfileChanges());
    }

    private void saveProfileChanges() {
        String name = editName.getText().toString();
        String email = editEmail.getText().toString();
        String phone = editPhone.getText().toString();
        String age = editAge.getText().toString();
        String gender = editGender.getText().toString();
        String bloodGroup = editBloodGroup.getText().toString();
        String lifestyle = editLifestyle.getText().toString();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || age.isEmpty() || gender.isEmpty() || bloodGroup.isEmpty() || lifestyle.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.child("name").setValue(name);
            userRef.child("email").setValue(email);
            userRef.child("phone").setValue(phone);
            userRef.child("ageGroup").setValue(age);
            userRef.child("gender").setValue(gender);
            userRef.child("bloodGroup").setValue(bloodGroup);
            userRef.child("lifestyle").setValue(lifestyle);

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity after saving
        }
    }
}
