package com.example.healthmate.activities;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.healthmate.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class menudialog extends DialogFragment {

    private CircleImageView menuProfileImage;
    private TextView menuUsername;
    private TextView menuLoginMethod;
    private ImageView menuEditIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menudialog, container, false);

        menuProfileImage = view.findViewById(R.id.menu_profile_image);
        menuUsername = view.findViewById(R.id.menu_username);
        menuLoginMethod = view.findViewById(R.id.menu_login_method);
        menuEditIcon = view.findViewById(R.id.menu_edit_icon);

        // Set up the views...

        menuEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the profile edit screen...
            }
        });

        return view;
    }
}
