package com.example.healthmate.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.healthmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileEditActivity extends AppCompatActivity {

    private TextView nameTextView, ageTextView, bloodGroupTextView, lifestyleTextView;
    private ImageView profileImageView;
    private FirebaseAuth auth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        profileImageView = findViewById(R.id.profile_image);
        nameTextView = findViewById(R.id.name_text_view);
        ageTextView = findViewById(R.id.age_text_view);
        bloodGroupTextView = findViewById(R.id.blood_group_text_view);
        lifestyleTextView = findViewById(R.id.lifestyle_text_view);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            loadProfileData();
        } else {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProfileData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String age = snapshot.child("age").getValue(String.class);
                    String bloodGroup = snapshot.child("bloodGroup").getValue(String.class);
                    String lifestyle = snapshot.child("lifestyle").getValue(String.class);
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                    nameTextView.setText(name != null ? name : "N/A");
                    ageTextView.setText(age != null ? age : "N/A");
                    bloodGroupTextView.setText(bloodGroup != null ? bloodGroup : "N/A");
                    lifestyleTextView.setText(lifestyle != null ? lifestyle : "N/A");

                    if (profileImageUrl != null) {
                        Glide.with(ProfileEditActivity.this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.user)
                                .into(profileImageView);
                    }
                } else {
                    Toast.makeText(ProfileEditActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileEditActivity.this, "Error loading profile data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
