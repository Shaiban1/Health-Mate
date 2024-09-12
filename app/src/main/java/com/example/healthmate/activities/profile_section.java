package com.example.healthmate.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmate.R;


import de.hdodenhof.circleimageview.CircleImageView;


public class profile_section extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView username;
    private TextView loginMethod;
    private menudialog menuDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_section);

        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.menu_username);
        loginMethod = findViewById(R.id.menu_login_method);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDialog();
            }
        });
    }

    private void showMenuDialog() {
        menuDialog = new menudialog();
        menuDialog.show(getSupportFragmentManager(), "MenuDialog");
    }
}