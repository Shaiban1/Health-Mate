        package com.example.healthmate.fragment;

        import android.os.Bundle;

        import androidx.fragment.app.Fragment;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.example.healthmate.R;
        import com.example.healthmate.activities.SurveyScreen;
        import com.google.android.material.textfield.TextInputEditText;

        import de.hdodenhof.circleimageview.CircleImageView;

        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.core.content.FileProvider;
        import androidx.fragment.app.Fragment;

        import com.example.healthmate.R;
        import com.google.android.material.dialog.MaterialAlertDialogBuilder;

        import java.io.File;
        import java.io.IOException;


        public class NameFragment extends Fragment {
            private static final int PICK_IMAGE = 1;
            private static final int REQUEST_CAMERA = 2;
            private Uri imageUri;

            private EditText firstName, middleName, lastName;
            private CircleImageView profileImageView;
            private ImageView fabCamera;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                View view = inflater.inflate(R.layout.fragment_name, container, false);

                // Initialize views
                firstName = view.findViewById(R.id.FirstName);
                middleName = view.findViewById(R.id.MiddleName);
                lastName = view.findViewById(R.id.LastName);
                profileImageView = view.findViewById(R.id.profile_image);
                fabCamera = view.findViewById(R.id.fab_camera);

                // Set click listener on the fabCamera
                fabCamera.setOnClickListener(v -> showImagePickerDialog());

                return view;
            }

            private void showImagePickerDialog() {
                String[] options = {"Choose from Gallery", "Take Photo"};
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Select Image")
                        .setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                // Open gallery
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, PICK_IMAGE);
                            } else {
                                // Open camera
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePicture.resolveActivity(requireActivity().getPackageManager()) != null) {
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                    if (photoFile != null) {
                                        imageUri = FileProvider.getUriForFile(requireContext(),
                                                "com.example.healthmate.fileprovider", photoFile);
                                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                        startActivityForResult(takePicture, REQUEST_CAMERA);
                                    }
                                }
                            }
                        }).show();
            }

            @Override
            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
                        // Handle image picked from gallery
                        imageUri = data.getData();
                        profileImageView.setImageURI(imageUri);
                    } else if (requestCode == REQUEST_CAMERA) {
                        // Handle image taken with the camera
                        profileImageView.setImageURI(imageUri);
                    }
                }
            }

            private File createImageFile() throws IOException {
                String imageFileName = "profile_image";
                File storageDir = requireActivity().getExternalFilesDir(null);
                return File.createTempFile(imageFileName, ".jpg", storageDir);
            }

            public String getName() {
                String fN = firstName.getText().toString().trim();
                String mN = middleName.getText().toString().trim();
                String lN = lastName.getText().toString().trim();

                // Combine the names while handling optional middle or last names
                StringBuilder fullName = new StringBuilder(fN);

                if (!mN.isEmpty()) {
                    fullName.append(" ").append(mN);
                }

                if (!lN.isEmpty()) {
                    fullName.append(" ").append(lN);
                }

                return fullName.toString();
            }

            public Uri getImageUri() {
                return imageUri;
            }
        }
