package com.example.healthmate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthmate.R;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private final Handler handler = new Handler();  // Single handler instance
    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Handle edge-to-edge display and insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Delay navigation to next screen by SPLASH_DELAY milliseconds
        handler.postDelayed(() -> {
            navigateNextScreen();
        }, SPLASH_DELAY);
    }

    private void navigateNextScreen() {
        Intent intent;
        if (isUserLoggedIn()) {
            // Redirect to MainActivity if user is logged in
            intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        } else {
            // Redirect to OnboardingActivity if user is not logged in
            intent = new Intent(SplashScreenActivity.this, OnBoardActivity.class);
        }
        startActivity(intent);
        finish();  // Close the SplashScreenActivity
    }

    // Check if the user is logged in via SharedPreferences
    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending posts from the handler to avoid memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}
