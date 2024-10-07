package com.example.healthmate.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.example.healthmate.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
    private TextView menuUsername, menuEmail, menuPhone, menuAge, menuBloodGroup, menuLifestyle, logoutText;
    private ImageView menuEditIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menudialog, container, false);

        // Initialize views
        menuProfileImage = view.findViewById(R.id.menu_profile_image);
        menuUsername = view.findViewById(R.id.menu_username);
        menuEmail = view.findViewById(R.id.menu_email);
        menuPhone = view.findViewById(R.id.menu_phone);
        menuAge = view.findViewById(R.id.menu_age);
        menuBloodGroup = view.findViewById(R.id.menu_blood_group);
        menuLifestyle = view.findViewById(R.id.menu_lifestyle);
        menuEditIcon = view.findViewById(R.id.menu_edit_icon);
        logoutText = view.findViewById(R.id.logout_text); // Logout Text

        // Fetch user details from Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("survey");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Retrieve all survey-related data
                    String username = dataSnapshot.child("name").getValue(String.class);
                    String ageGroup = dataSnapshot.child("ageGroup").getValue(String.class);
                    String bloodGroup = dataSnapshot.child("bloodGroup").getValue(String.class);
                    String lifestyle = dataSnapshot.child("lifestyle").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);  // Assuming the profile image URL is stored here

                    // Safely set the values or show placeholders if null
                    menuUsername.setText(username != null ? username : "Unknown");
                    menuAge.setText(ageGroup != null ? ageGroup : "N/A");
                    menuBloodGroup.setText(bloodGroup != null ? bloodGroup : "N/A");
                    menuLifestyle.setText(lifestyle != null ? lifestyle : "N/A");

                    // Load profile image if available
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(requireContext())
                                .load(profileImageUrl)
                                .placeholder(R.drawable.user) // Replace with your default image resource
                                .into(menuProfileImage);
                    }

                    // Fetch email and phone from FirebaseAuth
                    menuEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
                    menuPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Edit profile listener
        menuEditIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.healthmate.activities.ProfileEditActivity.class);
            startActivity(intent);
        });

        // Logout click listener
        logoutText.setOnClickListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            // Google sign-out setup
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))  // Ensure this is correct
                    .requestEmail()
                    .build();

            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

            // First, sign out from Firebase
            mAuth.signOut();

            // Then sign out from Google
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    googleSignInClient.revokeAccess().addOnCompleteListener(revokeTask -> {
                        redirectToLogin();
                    });
                } else {
                    redirectToLogin(); // Proceed to login screen even if sign-out failed
                }
            });
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupDialog();
    }

    // Setup the dialog size and background
    private void setupDialog() {
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(window.getAttributes());

                layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9); // 90% of screen width
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Wrap content for height

                window.setAttributes(layoutParams);

                // Optional: Set transparent background for rounded corners
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    // Redirect to the login screen after logging out
    private void redirectToLogin() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", requireActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish(); // Close the current activity
    }
}
