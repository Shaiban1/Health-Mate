package com.example.healthmate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;


import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.healthmate.R;
import com.example.healthmate.fragment.AfternoonFragment;
import com.example.healthmate.fragment.EveningFragment;
import com.example.healthmate.fragment.MorningFragment;
import com.example.healthmate.fragment.NightFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    CircularImageView circularImageView;
    TextView user_name_txtVw;
    TextView time_of_day_textview;
    private final Handler handler = new Handler();
    LottieAnimationView lottieAnimationView;
    Button logout_button;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();


        circularImageView = findViewById(R.id.profile_image);
        user_name_txtVw = findViewById(R.id.username_main);
        time_of_day_textview = findViewById(R.id.time_of_day);
        logout_button = findViewById(R.id.logout_button);
        lottieAnimationView = findViewById(R.id.lottie_day_animation);

        findViewById(R.id.morning_grid).setOnClickListener(v -> loadFragment(new MorningFragment()));
        findViewById(R.id.afternoon_grid).setOnClickListener(v -> loadFragment(new AfternoonFragment()));
        findViewById(R.id.evening_grid).setOnClickListener(v -> loadFragment(new EveningFragment()));
        findViewById(R.id.night_grid).setOnClickListener(v -> loadFragment(new NightFragment()));







        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            updateUI(user);
        } else {
            redirectToLogin();
        }

        logout_button.setOnClickListener(view -> {
            mAuth.signOut();
            redirectToLogin();
        });

        updateTimeAndAnimation();
        startDayNightCycleUpdate();
    }

    private void loadFragment(Fragment fragment) {
        // Hide profile image and logout button when a fragment is loaded
        findViewById(R.id.profile_image).setVisibility(View.GONE);
        findViewById(R.id.logout_button).setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Show the profile image and logout button when no fragment is displayed
            findViewById(R.id.profile_image).setVisibility(View.VISIBLE);
            findViewById(R.id.logout_button).setVisibility(View.VISIBLE);

            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    private void updateUI(FirebaseUser user) {
        if(user != null) {
            Uri photoUrl = user.getPhotoUrl();
            if(photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .into(circularImageView);
            }

            String userName = user.getDisplayName();
            if(userName != null ) {
                String greeting = "Hey, " + userName;
                user_name_txtVw.setText(greeting);
            }
        }
    }



    private void updateTimeAndAnimation() {
        String timeOfDay = getTimeOfDay();
        time_of_day_textview.setText(timeOfDay);

        applyLottieAnim(timeOfDay);
    }

    private String getTimeOfDay() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if(hour >= 5 && hour <12) {
            return "Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Afternoon";
        } else if (hour>=17 && hour <21) {
            return "Evening";
        } else {
            return "Night";
        }
    }


    private void applyLottieAnim(String timeOfDay) {
        int animationResId;

        switch (timeOfDay) {
            case "Morning":
                animationResId = R.raw.morning;
                break;
            case "Afternoon":
                animationResId = R.raw.afternoon;
                break;

            case "Evening" :
                animationResId = R.raw.evening;
                break;

            case "Night":
                animationResId = R.raw.night;
                break;

            default:
                return;
        }

        lottieAnimationView.setAnimation(animationResId);
        lottieAnimationView.playAnimation();

    }

    private void startDayNightCycleUpdate() {
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                updateTimeAndAnimation();

                handler.postDelayed(this,60000);
            }
        };
        handler.post(updateTask);
    }

    private void redirectToLogin() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();


        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}