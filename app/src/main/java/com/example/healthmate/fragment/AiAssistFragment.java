package com.example.healthmate.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.net.ParseException;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.healthmate.R;
import com.example.healthmate.activities.AiChatActivity;
import com.example.healthmate.activities.LoginActivity;
import com.example.healthmate.activities.menudialog;
import com.example.healthmate.adapters.ReminderAdapter;
import com.example.healthmate.filesImp.AlarmManagerHelper;
import com.example.healthmate.filesImp.ReminderViewModel;
import com.example.healthmate.filesImp.ReminderViewModelFactory;
import com.example.healthmate.models.Reminder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;

public class AiAssistFragment extends Fragment implements ReminderDialogFragment.ReminderDialogListener {

    private CircularImageView circularImageView;
    private TextView user_name_txtVw;
    private Button logout_button;
    private RecyclerView remindersRecyclerView;
    private ImageView expandButton;
    private ReminderAdapter adapter;
    private ReminderViewModel reminderViewModel;
    private boolean isExpanded = false;
    private TextView chatSuggestionText;
    private ImageButton chatActionButton;
    private ArrayList<String> suggestions;
    private int currentIndex = 0;
    private LottieAnimationView animationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        circularImageView = view.findViewById(R.id.profile_image);
        user_name_txtVw = view.findViewById(R.id.username_main);
        logout_button = view.findViewById(R.id.logout_button);
        remindersRecyclerView = view.findViewById(R.id.remindersRecyclerView);
        expandButton = view.findViewById(R.id.expandButton);
        chatSuggestionText = view.findViewById(R.id.chat_suggestion_text);
        chatActionButton = view.findViewById(R.id.chat_action_button);
        animationView = view.findViewById(R.id.id_anim_meta);

        animationView.setAnimation(R.raw.metaanim);
        animationView.playAnimation();

        // List of AI suggestions
        suggestions = new ArrayList<>();
        suggestions.add("How can I help you today?");
        suggestions.add("What can I do for you?");
        suggestions.add("Need assistance with something?");
        suggestions.add("Do you have any questions?");

        // Start the text animation
        animateChatSuggestions();

        // Animate the button at the end of the card

        FloatingActionButton fab = view.findViewById(R.id.fab_home);

        ReminderViewModelFactory factory = new ReminderViewModelFactory(requireActivity().getApplication());
        reminderViewModel = new ViewModelProvider(this, factory).get(ReminderViewModel.class);

        if (reminderViewModel != null) {
            // Initialize RecyclerView and Adapter
            remindersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ReminderAdapter(reminderViewModel);
            remindersRecyclerView.setAdapter(adapter);

            reminderViewModel.getAllRemindersPaged().observe(getViewLifecycleOwner(), pagedList -> {
                if (pagedList != null) {
                    adapter.submitList(pagedList);
                } else {
                    // Handle the case where the list is empty or null
                    // You can show an error message, a loading indicator, or a default state
                }
            });
        }

        // Define menuDialog once
        menudialog menuDialog = new menudialog();

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getChildFragmentManager().findFragmentByTag("menuDialog") == null) {
                    // Show the dialog only if it's not already displayed
                    menuDialog.show(getChildFragmentManager(), "menuDialog");
                }
            }
        });


        // Handle FAB click
        fab.setOnClickListener(v -> openReminderDialog());

        // Handle expand/collapse button click
        expandButton.setOnClickListener(v -> {
            if (isExpanded) {
                ViewAnimation.collapse(remindersRecyclerView);
                expandButton.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
            } else {
                ViewAnimation.expand(remindersRecyclerView);
                expandButton.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
            }
            isExpanded = !isExpanded;
        });



        chatActionButton.setOnClickListener(view1 -> {
            circularRevealAnimation(view);
        });

        // Update UI with Firebase user data
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            loadUserData(userId);
        } else {
            redirectToLogin();
        }

        return view;
    }


    private void circularRevealAnimation(final View view) {
        // Get the center for the clipping circle
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

        // Get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // Create the circular reveal animation
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius);
        revealAnimator.setDuration(300); // Adjust the duration for smoothness

        // Create the fade-out animation for the original view
        AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(300); // Match the duration of the circular reveal

        // Start both animations
        view.startAnimation(fadeOutAnimation);
        revealAnimator.start();

        // Start AiChatActivity after the animation ends
        revealAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                // Start AiChatActivity
                Intent intent = new Intent(getActivity(), AiChatActivity.class);
                startActivity(intent);

                // Optional: Override pending transition
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }


    private void animateChatSuggestions() {
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Animate TextView to slide in from the top
                ObjectAnimator slideIn = ObjectAnimator.ofFloat(chatSuggestionText, "translationY", -200f, 0f);
                slideIn.setInterpolator(new LinearInterpolator());
                slideIn.setDuration(500);

                // Set new suggestion text
                chatSuggestionText.setText(suggestions.get(currentIndex));
                currentIndex = (currentIndex + 1) % suggestions.size();

                // Start the animation
                slideIn.start();

                // Delay for 2 seconds before showing the next text
                handler.postDelayed(this, 2500);
            }
        };

        handler.post(runnable);
    }



    private void openReminderDialog() {
        ReminderDialogFragment dialog = new ReminderDialogFragment(this);
        dialog.show(getChildFragmentManager(), "ReminderDialogFragment");
    }

    @Override
    public void onReminderSaved(Reminder reminder) {
        // Insert the reminder into the database via ViewModel
        reminderViewModel.insert(reminder);

        final String TIME_FORMAT = "hh:mm a"; // Specify the correct format for the time string

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

        LocalTime time = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                time = LocalTime.parse(reminder.getTime(), formatter); // Use the formatter to parse the time string
            } catch (DateTimeParseException e) {
                Log.e("ParseException", "Error parsing time: " + reminder.getTime(), e);
                // You can also throw a custom exception or return an error value here
                return; // or throw new ParseException("Error parsing time", e);
            }
        }

        long triggerAtMillis = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            OffsetTime offsetTime = time.atOffset(ZoneOffset.UTC);
            OffsetDateTime offsetDateTime = offsetTime.atDate(LocalDate.now());
            Instant instant = offsetDateTime.toInstant();
            triggerAtMillis = instant.toEpochMilli();
        } else {
            // For Android versions before Oreo (API 26), use the older approach
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
            try {
                Date date = sdf.parse(reminder.getTime());
                triggerAtMillis = date.getTime();
            } catch (ParseException e) {
                Log.e("ParseException", "Error parsing time: " + reminder.getTime(), e);
                // You can also set a default value for triggerAtMillis here
                triggerAtMillis = System.currentTimeMillis(); // or some other default value
            } catch (java.text.ParseException e) {
                throw new RuntimeException(e);
            }
        }

        int requestCode = 1; // Unique request code for the alarm
        long repeatInterval = AlarmManager.INTERVAL_DAY; // Repeat every day

        AlarmManagerHelper.setAlarm(getContext(), triggerAtMillis, requestCode, repeatInterval, reminder.getRingtoneResId());
    }


    private void loadUserData(String userId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            redirectToLogin();
            return;
        }

        boolean isGoogleSignIn = false;

        // Check if the user signed in with Google
        for (com.google.firebase.auth.UserInfo profile : user.getProviderData()) {
            if (profile.getProviderId().equals("google.com")) {
                isGoogleSignIn = true;
                break;
            }
        }

        if (isGoogleSignIn) {
            // User signed in with Google, so get Google profile information
            String displayName = user.getDisplayName();
            Uri profileImageUrl = user.getPhotoUrl();

            if (displayName != null) {
                user_name_txtVw.setText("Hey, " + displayName);
            } else {
                user_name_txtVw.setText("Hey, User");
            }

            if (profileImageUrl != null) {
                // Load profile image using Glide
                Glide.with(AiAssistFragment.this)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.user)  // Optional placeholder
                        .into(circularImageView);
            } else {
                circularImageView.setImageResource(R.drawable.user);  // Default user image
            }
        } else {
            // User signed in through registration and survey, load data from Firebase
            DatabaseReference surveyReference = FirebaseDatabase.getInstance().getReference("surveys");

            surveyReference.orderByChild("userId").equalTo(userId).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot surveySnapshot : snapshot.getChildren()) {
                            // Get the full name from the survey node
                            String fullName = surveySnapshot.child("name").getValue(String.class);
                            if (fullName != null) {
                                user_name_txtVw.setText("Hey, " + fullName);
                            } else {
                                user_name_txtVw.setText("Hey, User");
                            }
                        }
                    } else {
                        Log.e("FirebaseError", "Survey data not found");
                        user_name_txtVw.setText("Hey, User");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error fetching survey data", error.toException());
                }
            });

            // Reference to users node for the profile image URL
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Listen for changes in user profile image URL
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Get profile image URL
                        String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                        // Load profile image into CircularImageView using Glide
                        if (profileImageUrl != null) {
                            Glide.with(AiAssistFragment.this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.user)  // Optional placeholder
                                    .into(circularImageView);
                        } else {
                            circularImageView.setImageResource(R.drawable.user);  // Default user image
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error fetching user data", error.toException());
                }
            });
        }
    }





    private void redirectToLogin() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", requireActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }



}
