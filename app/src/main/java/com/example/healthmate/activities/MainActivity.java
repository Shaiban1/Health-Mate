package com.example.healthmate.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.healthmate.R;
import com.example.healthmate.fragment.AiAssistFragment;
import com.example.healthmate.fragment.DoctorsFragment;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 100;
    private static final String AI_ASSIST_FRAGMENT_TAG = "AiAssistFragment";
    private static final String DOCTORS_FRAGMENT_TAG = "DoctorsFragment";
    private String currentFragmentTag = AI_ASSIST_FRAGMENT_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestPermissions();

        // Restore or initialize fragment
        if (savedInstanceState == null) {
            loadFragment(new AiAssistFragment(), AI_ASSIST_FRAGMENT_TAG);
        } else {
            currentFragmentTag = savedInstanceState.getString("currentFragmentTag", AI_ASSIST_FRAGMENT_TAG);
        }

        // Setup button listeners and styles
        setupButtonListeners();

    }

    private void setupButtonListeners() {
        Button flipkartButton = findViewById(R.id.button_flipkart);
        Button groceryButton = findViewById(R.id.button_grocery);

        flipkartButton.setOnClickListener(v -> {
            if (!currentFragmentTag.equals(AI_ASSIST_FRAGMENT_TAG)) {
                loadFragment(new AiAssistFragment(), AI_ASSIST_FRAGMENT_TAG);
                flipkartButton.setSelected(true); // Mark as selected
                groceryButton.setSelected(false); // Deselect other button
            }
        });

        groceryButton.setOnClickListener(v -> {
            if (!currentFragmentTag.equals(DOCTORS_FRAGMENT_TAG)) {
                loadFragment(new DoctorsFragment(), DOCTORS_FRAGMENT_TAG);
                groceryButton.setSelected(true); // Mark as selected
                flipkartButton.setSelected(false); // Deselect other button
            }
        });
    }




    private void loadFragment(Fragment fragment, String tag) {
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (existingFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment, tag)
                    .commit();
        }

        // Update current fragment tag
        currentFragmentTag = tag;

        // Update UI dynamically based on fragment
        if (tag.equals(AI_ASSIST_FRAGMENT_TAG)) {
            updateStatusBarColor(R.color.colorPrimary);
        } else if (tag.equals(DOCTORS_FRAGMENT_TAG)) {
            updateStatusBarColor(R.color.colorSecondary);
        }
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.VIBRATE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = Arrays.copyOf(permissions, permissions.length + 1);
            permissions[permissions.length - 1] = Manifest.permission.POST_NOTIFICATIONS;
        }

        // Check if permissions are already granted
        if (!hasAllPermissionsGranted(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        } else {
            showToast("All permissions are already granted.");
        }
    }

    private boolean hasAllPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (allPermissionsGranted(grantResults)) {
                showToast("Permissions granted!");
            } else {
                showToast("Permissions denied. Some features may not work.");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean allPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Save the current fragment tag for state restoration
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentFragmentTag", currentFragmentTag);
    }

    // Dynamically update the status bar color
    private void updateStatusBarColor(@ColorRes int colorRes) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, colorRes));
    }
}
