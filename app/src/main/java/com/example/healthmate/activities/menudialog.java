package com.example.healthmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import com.example.healthmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class menudialog extends DialogFragment {

    private CircleImageView menuProfileImage;
    private TextView menuUsername, menuEmail, menuPhone, menuAge, menuGender, menuBloodGroup, menuLifestyle, logoutText;
    private ImageView menuEditIcon, menuSettingsIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menudialog, container, false);

        // Initialize views
        menuProfileImage = view.findViewById(R.id.menu_profile_image);
        menuUsername = view.findViewById(R.id.menu_username);
        menuEmail = view.findViewById(R.id.menu_email);
        menuPhone = view.findViewById(R.id.menu_phone);
        menuAge = view.findViewById(R.id.menu_age);
        menuGender = view.findViewById(R.id.menu_gender);
        menuBloodGroup = view.findViewById(R.id.menu_blood_group);
        menuLifestyle = view.findViewById(R.id.menu_lifestyle);
        menuEditIcon = view.findViewById(R.id.menu_edit_icon);
        menuSettingsIcon = view.findViewById(R.id.settings_icon); // Settings Icon
        logoutText = view.findViewById(R.id.logout_text); // Logout Text

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

                    // Set the values in the menu dialog
                    menuUsername.setText(username);
                    menuEmail.setText(email);
                    menuPhone.setText(phone);
                    menuAge.setText(ageGroup);
                    menuGender.setText(gender);
                    menuBloodGroup.setText(bloodGroup);
                    menuLifestyle.setText(lifestyle);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        }

        // Edit profile listener
        menuEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.example.healthmate.activities.ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        // Settings icon click listener
        menuSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open settings activity
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        // Logout click listener
        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out from Firebase and redirect to MainActivity (or Login screen)
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish(); // Close the current activity
            }
        });

        return view;
    }
}
