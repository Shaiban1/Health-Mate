package com.example.healthmate.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.healthmate.R;
import com.example.healthmate.adapters.SurveyPagerAdapter;
import com.example.healthmate.filesImp.StatusBarView;
import com.example.healthmate.fragment.FragmentAges;
import com.example.healthmate.fragment.FragmentBloodGroup;
import com.example.healthmate.fragment.LifestyleInput;
import com.example.healthmate.fragment.NameFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyScreen extends AppCompatActivity {

    private ViewPager viewPager;
    private StatusBarView statusBarView;
    private Button previousButton, nextButton, submitButton;
    private TextView skipTextview;
    private SurveyPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_screen);

        // Apply system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        statusBarView = findViewById(R.id.status_bar);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);
        submitButton = findViewById(R.id.submit_button);
        skipTextview = findViewById(R.id.skip_button);

        setupViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateNavigationButtons(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        previousButton.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1));
        nextButton.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1));
        submitButton.setOnClickListener(v -> submitSurvey());
        skipTextview.setOnClickListener(v -> {
            Intent intent = new Intent(SurveyScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new NameFragment());
        fragments.add(new FragmentAges());
        fragments.add(new FragmentBloodGroup());
        fragments.add(new LifestyleInput());

        adapter = new SurveyPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        statusBarView.setCurrentStep(1); // Update status bar to show the first step
    }

    private void updateNavigationButtons(int position) {
        int lastPosition = viewPager.getAdapter().getCount() - 1;
        previousButton.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        nextButton.setVisibility(position == lastPosition ? View.GONE : View.VISIBLE);
        skipTextview.setVisibility(position == lastPosition ? View.GONE : View.VISIBLE);
        submitButton.setVisibility(position == lastPosition ? View.VISIBLE : View.GONE);
        statusBarView.setCurrentStep(position + 1); // Update the step in the status bar view
    }

    private void submitSurvey() {
        // Retrieve the fragments from the adapter
        NameFragment nameFragment = (NameFragment) adapter.getItem(0);
        FragmentAges ageFragment = (FragmentAges) adapter.getItem(1);
        FragmentBloodGroup bloodGroupFragment = (FragmentBloodGroup) adapter.getItem(2);
        LifestyleInput lifestyleInput = (LifestyleInput) adapter.getItem(3);

        // Retrieve data from fragments
        String fullName = nameFragment.getName();
        String age = ageFragment.getSelectedAgeGroup();
        String bloodGroup = bloodGroupFragment.getSelectedBloodGroup();
        String lifestyle = lifestyleInput.getSelectedLifestyle();
        Uri profileImageUri = nameFragment.getImageUri();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            DatabaseReference userSurveyRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(user.getUid()).child("survey");

            // Create a map for the survey data
            Map<String, Object> surveyData = new HashMap<>();
            surveyData.put("name", fullName);
            surveyData.put("ageGroup", age);
            surveyData.put("bloodGroup", bloodGroup);
            surveyData.put("lifestyle", lifestyle);

            // Upload image and handle profile image URL
            if (profileImageUri != null) {
                uploadImageToFirebase(profileImageUri, user.getUid(), surveyData);
            } else {
                // Push the survey data to Firebase Database without image URL
                userSurveyRef.setValue(surveyData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SurveyScreen.this, "Survey submitted successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SurveyScreen.this, MainActivity.class));
                        finish();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Log.e("FirebaseError", "Failed to submit survey: " + errorMessage);
                        Toast.makeText(SurveyScreen.this, "Failed to submit survey: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(this, "User is not authenticated. Please sign in to submit the survey.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebase(Uri imageUri, String uid, Map<String, Object> surveyData) {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("profileImages").child(uid + ".jpg");

            storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Add profile image URL to survey data
                    surveyData.put("profileImageUrl", uri.toString());

                    // Save survey data to Firebase
                    DatabaseReference userSurveyRef = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(uid).child("survey");

                    userSurveyRef.setValue(surveyData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Survey data submitted successfully with image URL!");
                            Toast.makeText(SurveyScreen.this, "Survey submitted successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SurveyScreen.this, MainActivity.class));
                            finish();
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Log.e("FirebaseError", "Failed to submit survey: " + errorMessage);
                            Toast.makeText(SurveyScreen.this, "Failed to submit survey: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }).addOnFailureListener(e -> {
                Log.e("FirebaseStorage", "Image upload failed: " + e.getMessage());
                Toast.makeText(SurveyScreen.this, "Image upload failed!", Toast.LENGTH_SHORT).show();
            });
        }
    }



    private boolean validateInputs(String fullName, String age, String bloodGroup, String lifestyle) {
        if (fullName == null || fullName.isEmpty()) {
            Toast.makeText(this, "Please provide your name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (age == null || age.isEmpty()) {
            Toast.makeText(this, "Please select your age group", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (bloodGroup == null || bloodGroup.isEmpty()) {
            Toast.makeText(this, "Please select your blood group", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (lifestyle == null || lifestyle.isEmpty()) {
            Toast.makeText(this, "Please select your lifestyle", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    private void submitSurveyData(Map<String, Object> surveyData) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            DatabaseReference userSurveyRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(user.getUid()).child("surveys");

            // Push survey data to Firebase Database
            userSurveyRef.push().setValue(surveyData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SurveyScreen.this, "Survey submitted successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SurveyScreen.this, MainActivity.class));
                    finish();
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Log.e("FirebaseError", "Failed to submit survey: " + errorMessage);
                    Toast.makeText(SurveyScreen.this, "Failed to submit survey: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
