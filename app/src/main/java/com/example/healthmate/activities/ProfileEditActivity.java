package com.example.healthmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileEditActivity extends AppCompatActivity {

    private LinearLayout personalDetailsSection, contactDetailsSection, lifestyleSection;
    private Button nextButton, previousButton, saveButton;
    private int currentSection = 0; // Track the current visible section

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        personalDetailsSection = findViewById(R.id.personal_details_section);
        contactDetailsSection = findViewById(R.id.contact_details_section);
        lifestyleSection = findViewById(R.id.lifestyle_section);

        nextButton = findViewById(R.id.next_button);
        previousButton = findViewById(R.id.previous_button);
        saveButton = findViewById(R.id.save_button);

        // Set up button listeners
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextSection();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousSection();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
            }
        });

        // Initial section setup
        updateSectionVisibility();
    }

    private void showNextSection() {
        if (currentSection < 2) { // Assuming 3 sections
            currentSection++;
            updateSectionVisibility();
        }
    }

    private void showPreviousSection() {
        if (currentSection > 0) {
            currentSection--;
            updateSectionVisibility();
        }
    }

    private void updateSectionVisibility() {
        personalDetailsSection.setVisibility(currentSection == 0 ? View.VISIBLE : View.GONE);
        contactDetailsSection.setVisibility(currentSection == 1 ? View.VISIBLE : View.GONE);
        lifestyleSection.setVisibility(currentSection == 2 ? View.VISIBLE : View.GONE);

        previousButton.setVisibility(currentSection == 0 ? View.GONE : View.VISIBLE);
        nextButton.setVisibility(currentSection == 2 ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(currentSection == 2 ? View.VISIBLE : View.GONE);
    }

    private void saveProfileChanges() {
        // Collect and save data from all sections
        String name = ((EditText) findViewById(R.id.edit_name)).getText().toString();
        String age = ((EditText) findViewById(R.id.edit_age)).getText().toString();
        String email = ((EditText) findViewById(R.id.edit_email)).getText().toString();
        String phone = ((EditText) findViewById(R.id.edit_phone)).getText().toString();
        String lifestyle = ((EditText) findViewById(R.id.edit_lifestyle)).getText().toString();

        // Perform validation if needed
        if (name.isEmpty() || age.isEmpty() || email.isEmpty() || phone.isEmpty() || lifestyle.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Implement saving changes logic (e.g., update Firebase)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.child("name").setValue(name);
            userRef.child("age").setValue(age);
            userRef.child("email").setValue(email);
            userRef.child("phone").setValue(phone);
            userRef.child("lifestyle").setValue(lifestyle);

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
