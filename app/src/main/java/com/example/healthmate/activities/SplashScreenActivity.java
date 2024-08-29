package com.example.healthmate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthmate.R;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private  Runnable runnable;
    private static final int SPLASH_DELAY = 2000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });





                new Handler().postDelayed(() -> {

                    if (isUserLoggedIn()) {
                        // Redirect to MainActivity if user is logged in
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // Redirect to OnboardingActivity if user is not logged in
                        Intent intent = new Intent(SplashScreenActivity.this, OnBoardActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }, SPLASH_DELAY); // Delay of 2 seconds
            }

            private boolean isUserLoggedIn() {

                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                return prefs.getBoolean("isLoggedIn", false);



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}