package com.example.healthmate.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthmate.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_section extends AppCompatActivity {

        private CircleImageView profileImage;
        private TextView username;
        private TextView loginMethod;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile_section);

            profileImage = findViewById(R.id.profile_image);
            username = findViewById(R.id.menu_username);
            loginMethod = findViewById(R.id.menu_login_method);

            // Set up the profile image and username
            // ...

            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenuDialog();
                }
            });
        }

        private void showMenuDialog() {
            // Create a new dialog fragment
            menudialog dialogFragment = new menudialog();
            dialogFragment.show(getSupportFragmentManager(), "MenuDialog");
        }
    }


