package com.example.healthmate.fragment;

import static android.content.ContentValues.TAG;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
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
import com.example.healthmate.adapters.SliderAdapter;
import com.example.healthmate.filesImp.AlarmManagerHelper;
import com.example.healthmate.filesImp.ReminderViewModel;
import com.example.healthmate.filesImp.ReminderViewModelFactory;
import com.example.healthmate.interfaces.TimeDifferenceCallback;
import com.example.healthmate.models.Reminder;
import com.example.healthmate.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AiAssistFragment extends Fragment implements TimeDifferenceCallback, ReminderDialogFragment.ReminderDialogListener {

    private CircularImageView circularImageView;
    private TextView user_name_txtVw;
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
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private SliderAdapter sliderAdapter;
    private int currentSlideIndex = 0;
    private final int SLIDER_DELAY = 3000; // 3 seconds delay for auto slide
    private Handler sliderHandler;

    private int[] sliderImages = {R.drawable.doct1, R.drawable.dcot2, R.drawable.doct3, R.drawable.doct4};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        circularImageView = view.findViewById(R.id.profile_image);
        user_name_txtVw = view.findViewById(R.id.username_main);
        remindersRecyclerView = view.findViewById(R.id.remindersRecyclerView);
        expandButton = view.findViewById(R.id.expandButton);
        chatSuggestionText = view.findViewById(R.id.chat_suggestion_text);
        chatActionButton = view.findViewById(R.id.chat_action_button);
        animationView = view.findViewById(R.id.id_anim_meta);

        animationView.setAnimation(R.raw.metaanim);
        animationView.playAnimation();


        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            loadUserData(userId);  // Fetch and display user data (username and profile picture)
        } else {
            redirectToLogin();
        }

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
            adapter = new ReminderAdapter(reminderViewModel, this); // Pass this fragment as the TimeDifferenceCallback
            remindersRecyclerView.setAdapter(adapter);

            reminderViewModel.getAllRemindersPaged().observe(getViewLifecycleOwner(), pagedList -> {
                if (pagedList != null) {
                    adapter.submitList(pagedList);
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


        TextView usernameTextView = view.findViewById(R.id.username_main);

        // Fetch user details from Firebase
        FirebaseUser curruser = FirebaseAuth.getInstance().getCurrentUser();
        if (curruser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child("name").getValue(String.class);

                    // Safely set the username or show a placeholder if null
                    usernameTextView.setText(username != null ? username : "Unknown");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors here (e.g., log the error)
                    usernameTextView.setText("Error loading username");
                }
            });
        } else {
            // Handle the case where the user is not logged in
            usernameTextView.setText("Guest");
        }


        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        SliderAdapter sliderAdapter = new SliderAdapter(getContext(), sliderImages);
        viewPager.setAdapter(sliderAdapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // This can be left empty since we are just using the tabs as indicators
        }).attach();


        sliderHandler = new Handler(Looper.getMainLooper());

        // Start the auto-slide
        startAutoSlide();



        return view;
    }

    private void startAutoSlide() {
        sliderHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentSlideIndex++;
                if (currentSlideIndex >= sliderImages.length) {
                    currentSlideIndex = 0;
                }

                viewPager.setCurrentItem(currentSlideIndex, true); // True for smooth animation

                // Call this method again after a delay for the next slide
                sliderHandler.postDelayed(this, SLIDER_DELAY);
            }
        }, SLIDER_DELAY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove any pending posts of sliderHandler to avoid memory leaks
        sliderHandler.removeCallbacksAndMessages(null);
    }





    private void updateReminderTimes() {
        new Handler().postDelayed(() -> {
            for (int i = 0; i < remindersRecyclerView.getChildCount(); i++) {
                View itemView = remindersRecyclerView.getChildAt(i);
                TextView reminderTimeTextView = itemView.findViewById(R.id.reminderTime);
                TextView timeRemainingTextView = itemView.findViewById(R.id.time_remaining);

                // Get the reminder time string
                String reminderTimeString = reminderTimeTextView.getText().toString();

                // Calculate and update time remaining
                onCalculateTimeDifference(timeRemainingTextView, reminderTimeString);
            }
            // Update the times every minute
            updateReminderTimes();
        }, 60000);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateReminderTimes(); // Start updating reminder times on fragment start
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
        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            redirectToLogin();
            return;
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", requireActivity().MODE_PRIVATE);
        String savedUsername = prefs.getString("username", ""); // Load from SharedPreferences
        String savedProfileImageUrl = prefs.getString("profileImageUrl", "");

        // Immediately display stored username and profile if available
        if (!savedUsername.isEmpty() && !savedProfileImageUrl.isEmpty()) {
            user_name_txtVw.setText(savedUsername);
            Glide.with(this)
                    .load(savedProfileImageUrl)
                    .placeholder(R.drawable.user)
                    .circleCrop()
                    .into(circularImageView);
        }

        // Fetch the latest data from Firebase
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("name").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    if (username != null && !username.isEmpty()) {
                        user_name_txtVw.setText(username);
                    } else {
                        user_name_txtVw.setText("User");
                    }

                    // Load profile picture
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(AiAssistFragment.this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.user)
                                .circleCrop()
                                .into(circularImageView);
                    } else {
                        circularImageView.setImageResource(R.drawable.user); // Default image
                    }

                    // Save updated values to SharedPreferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", username);
                    editor.putString("profileImageUrl", profileImageUrl);
                    editor.apply();
                } else {
                    // No data exists, fall back to default
                    setDefaultUserData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user data: " + databaseError.getMessage());
                setDefaultUserData();
            }
        });
    }

    private void setDefaultUserData() {
        user_name_txtVw.setText("User not found");
        circularImageView.setImageResource(R.drawable.user); // Default image
    }

    private void updateUserUI(User user) {
        String username = user.getName();
        String profileImageUrl = user.getProfileImageUrl();

        user_name_txtVw.setText(username != null && !username.isEmpty() ? username : "User");

        Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.user) // Placeholder image
                .error(R.drawable.user) // Error image
                .circleCrop() // Crop image into a circular shape
                .into(circularImageView);
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


    @Override
    public void onCalculateTimeDifference(TextView timeRemainingTextView, String reminderTimeString) {
        try {
            // Extract the actual time from the string (assume "Time: " is a fixed prefix)
            String timeOnly = reminderTimeString.replace("Time: ", "").trim();

            // DateTimeFormatter to parse time in AM/PM format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

            // Parse the reminder time
            LocalTime reminderTime = LocalTime.parse(timeOnly, formatter);

            // Get current time in India
            LocalTime currentTime = LocalTime.now(ZoneId.of("Asia/Kolkata"));

            // Calculate the time difference
            long hoursDifference = ChronoUnit.HOURS.between(currentTime, reminderTime);
            long minutesDifference = ChronoUnit.MINUTES.between(currentTime, reminderTime) % 60;

            // Update the TextView to show the time remaining
            if (hoursDifference > 0 || minutesDifference > 0) {
                String timeRemaining = String.format(Locale.ENGLISH, "Alarm in %d hr %d min", hoursDifference, minutesDifference);
                timeRemainingTextView.setText(timeRemaining);
            } else {
                timeRemainingTextView.setText("Alarm is due!");
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            timeRemainingTextView.setText("Invalid time format!");
        }
    }

}
