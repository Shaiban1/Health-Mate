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

import com.bumptech.glide.Glide;
import com.example.healthmate.R;
import com.example.healthmate.activities.AiChatActivity;
import com.example.healthmate.activities.GeminiResp;
import com.example.healthmate.activities.LoginActivity;
import com.example.healthmate.adapters.ReminderAdapter;
import com.example.healthmate.filesImp.AlarmManagerHelper;
import com.example.healthmate.filesImp.ReminderViewModel;
import com.example.healthmate.filesImp.ReminderViewModelFactory;
import com.example.healthmate.models.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class HomeFragment extends Fragment implements ReminderDialogFragment.ReminderDialogListener {

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

        // List of AI suggestions
        suggestions = new ArrayList<>();
        suggestions.add("How can I help you today?");
        suggestions.add("What can I do for you?");
        suggestions.add("Need assistance with something?");
        suggestions.add("Do you have any questions?");

        // Start the text animation
        animateChatSuggestions();

        // Animate the button at the end of the card
        animateChatButton();

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

        // Handle logout button click
        logout_button.setOnClickListener(v -> {
            mAuth.signOut();
            redirectToLogin();
        });

        chatActionButton.setOnClickListener(view1 -> {
            circularRevealAnimation(view);
        });

        // Update UI with Firebase user data
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            updateUI(user);
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

    private void animateChatButton() {
        // Create a pulsing animation for the button
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1f);

        // Apply the PropertyValuesHolder to the FloatingActionButton
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(chatActionButton, scaleX, scaleY);
        scaleDown.setDuration(1000);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
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

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .into(circularImageView);
            }

            String userName = user.getDisplayName();
            if (userName != null) {
                String greeting = "Hey, " + userName;
                user_name_txtVw.setText(greeting);
            }
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
