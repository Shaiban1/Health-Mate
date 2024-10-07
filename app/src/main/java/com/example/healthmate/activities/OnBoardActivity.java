package com.example.healthmate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthmate.R;
import com.example.healthmate.adapters.OnboardingPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnBoardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button getStartedButton;
    private int[] layouts;
    private OnboardingPagerAdapter onboardingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        // Handle edge-to-edge display and insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        getStartedButton = findViewById(R.id.getStartedButton);
        TextView skipTextView = findViewById(R.id.skipTextView);

        layouts = new int[]{
                R.layout.fragment_onboard1,
                R.layout.fragment_on_board2,
                R.layout.fragment_on_board3
        };

        // Set up ViewPager2 adapter
        onboardingPagerAdapter = new OnboardingPagerAdapter(this, layouts);
        viewPager.setAdapter(onboardingPagerAdapter);

        // Attach TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

        // Handle skip button click
        skipTextView.setOnClickListener(view -> launchLoginActivity());

        // Handle the "Next" or "Get Started" button
        getStartedButton.setOnClickListener(v -> {
            int current = getCurrentPage();
            if (current < layouts.length) {
                viewPager.setCurrentItem(current);
            } else {
                launchLoginActivity();
            }
        });

        // Page change listener to handle button text change
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int position) {
                if (position == layouts.length - 1) {
                    getStartedButton.setText("Get Started");
                } else {
                    getStartedButton.setText("Next");
                }
            }
        });
    }

    // Launch LoginActivity
    private void launchLoginActivity() {
        Intent intent = new Intent(OnBoardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Get the next page index
    private int getCurrentPage() {
        return viewPager.getCurrentItem() + 1;
    }
}
