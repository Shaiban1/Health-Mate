package com.example.healthmate.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmate.R;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.healthmate.fragment.DietFragment;
import com.example.healthmate.fragment.HomeFragment;
import com.example.healthmate.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestPermissions();
        Toast.makeText(MainActivity.this, "Permission is required", Toast.LENGTH_SHORT).show();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(getResources().getColorStateList(R.color.bottom_nav_icon_color,getTheme()));

        FragmentManager fragmentManager = getSupportFragmentManager();  // Initialize here

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_search) {
                selectedFragment = new DietFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else {
                return false;
            }

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        setStatusBarColor(R.color.logo_color);

        handleFragmentNavigation(getIntent());

    }

    private void requestPermissions() {
        String[] permissions = new String[] {
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.VIBRATE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.MODIFY_AUDIO_SETTINGS // Add this permission
        };



        // For Android 13 and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = Arrays.copyOf(permissions, permissions.length + 1);
            permissions[permissions.length - 1] = Manifest.permission.POST_NOTIFICATIONS;
        }

        boolean isPermissionGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = false;
                break;
            }
        }

        // Request permissions only if they are not already granted
        if (!isPermissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleFragmentNavigation(intent);
    }

    private void handleFragmentNavigation(Intent intent) {
        if (intent != null) {
            String fragmentToOpen = intent.getStringExtra("openFragment");
            if ("home".equals(fragmentToOpen)) {
                // Replace the fragment with the Home fragment
                openHomeFragment();
            }
        }
    }

    private void openHomeFragment() {
        Fragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment) // R.id.fragment_container should be the FrameLayout or container for fragments in MainActivity's layout
                .commit();
    }

    private void setStatusBarColor(@ColorRes int colorRes) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(colorRes, getTheme()));
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now display notifications and schedule exact alarms
                // Set up the reminder alarm and notification code here
                // ...
            } else {
                // Permission denied, you cannot display notifications and schedule exact alarms
                // Handle the permission denial here
                // ...
            }
        }
    }

}
