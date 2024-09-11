package com.example.healthmate.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class registration_activity extends AppCompatActivity {

    private EditText regemail, passwordEditText, confirmpasswordEditText;
    private CheckBox checkagreements, showpwd;
    private Button btnSignUp, verifyMail;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registration_activity);

        // Initialize UI components
        regemail = findViewById(R.id.regemail);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmpasswordEditText = findViewById(R.id.confirmpasswordEditText);
        checkagreements = findViewById(R.id.checkagreements);
        btnSignUp = findViewById(R.id.btnSignUp);
        showpwd = findViewById(R.id.showpwd);
        verifyMail = findViewById(R.id.verifyMail);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        showpwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confirmpasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmpasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Move the cursor to the end after toggling the password visibility
            passwordEditText.setSelection(passwordEditText.getText().length());
            confirmpasswordEditText.setSelection(confirmpasswordEditText.getText().length());
        });

        // Set up the Verify Email button click listener
        verifyMail.setOnClickListener(v -> {
            if (validateEmail()) {
                createAccountAndSendVerificationEmail();
            }
        });

        // Set up the Register button click listener
        btnSignUp.setOnClickListener(v -> {
            if (validateEmail() && validatePassword() && validateAgreements()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    refreshUserData(user);
                } else {
                    Toast.makeText(registration_activity.this, "No user found. Please verify your email first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle window insets to adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Email validation method
    private boolean validateEmail() {
        String email = regemail.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            regemail.setError("Invalid email address");
            return false;
        }
        return true;
    }

    // Password validation method
    private boolean validatePassword() {
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmpasswordEditText.getText().toString().trim();
        if (password.isEmpty() || password.length() < 8) {
            passwordEditText.setError("Password must be at least 8 characters long");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmpasswordEditText.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    // Agreement checkbox validation method
    private boolean validateAgreements() {
        if (!checkagreements.isChecked()) {
            Toast.makeText(this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Method to create a new account and send a verification email using Firebase
    private void createAccountAndSendVerificationEmail() {
        String email = regemail.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verificationTask -> {
                                        if (verificationTask.isSuccessful()) {
                                            Toast.makeText(registration_activity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(registration_activity.this, "Failed to send verification email: " + verificationTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Check for specific Firebase exceptions
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            // The email address is already in use by another account
                            Toast.makeText(registration_activity.this, "Your account is already registered. Please log in.", Toast.LENGTH_LONG).show();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(registration_activity.this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Refreshes the user data to ensure up-to-date information
    private void refreshUserData(FirebaseUser user) {
        user.reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (user.isEmailVerified()) {
                    // Proceed with registration
                    Toast.makeText(registration_activity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    // Additional registration logic here
                } else {
                    Toast.makeText(registration_activity.this, "Please verify your email before registering.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(registration_activity.this, "Failed to refresh user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
