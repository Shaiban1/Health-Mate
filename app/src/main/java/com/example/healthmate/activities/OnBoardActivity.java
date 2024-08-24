package com.example.healthmate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.viewpager.widget.ViewPager;

import com.example.healthmate.R;

import com.example.healthmate.adapters.OnboardingPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class OnBoardActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button getStartedButton;
    private int[] layouts;
    OnboardingPagerAdapter onboardingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        getStartedButton = findViewById(R.id.getStartedButton);

        TextView skipTextView = findViewById(R.id.skipTextView);

        skipTextView.setOnClickListener(view ->  {

                Intent intent = new Intent(OnBoardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
        });


        layouts = new int[] {
                R.layout.fragment_onboard1,
                R.layout.fragment_on_board2,
                R.layout.fragment_on_board3
        };
onboardingPagerAdapter = new OnboardingPagerAdapter(this, layouts);
        viewPager.setAdapter(onboardingPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        getStartedButton.setOnClickListener(v -> {

           int current = getItem();
           if(current < layouts.length) {
               viewPager.setCurrentItem(current);
           } else {
               launchLoginActivity();
           }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int position) {
                if(position == layouts.length-1) {
                   getStartedButton.setText("Get Started");
                } else {
                    getStartedButton.setText("Next");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }




    private void launchLoginActivity() {
        // Launch your main activity here
        Intent intent = new Intent(OnBoardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private int getItem() {
        return viewPager.getCurrentItem()+ 1;
    }




}