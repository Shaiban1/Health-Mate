package com.example.healthmate.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.healthmate.R;


public class RegistrationActivity extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView; // Declare LottieAnimationView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        // Initialize the LottieAnimationView
        lottieAnimationView = findViewById(R.id.lottieAnimationView);

        // Load the animation from the assets folder
        lottieAnimationView.setAnimation(R.raw.lottieflow_login);

        // Start the animation
        lottieAnimationView.loop(true);

        lottieAnimationView.playAnimation();

        // Handle window insets to adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause animation when activity is paused
        if (lottieAnimationView != null) {
            lottieAnimationView.pauseAnimation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume animation when activity is resumed
        if (lottieAnimationView != null) {
            lottieAnimationView.resumeAnimation();
        }
    }
}
