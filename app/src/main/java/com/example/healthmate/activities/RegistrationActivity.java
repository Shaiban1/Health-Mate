package com.example.healthmate.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class RegistrationActivity extends AppCompatActivity {

    private EditText regemail, passwordEditText, confirmpasswordEditText;
    private CheckBox checkagreements;
    private Button btnSignUp, verifyMail;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        // Initialize UI components
        regemail = findViewById(R.id.regemail);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmpasswordEditText = findViewById(R.id.confirmpasswordEditText);
        checkagreements = findViewById(R.id.checkagreements);
        btnSignUp = findViewById(R.id.btnSignUp);
        verifyMail = findViewById(R.id.verifyMail);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set up the Verify Email button click listener
        verifyMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail()) {
                    sendFirebaseVerificationEmail();
                }
            }
        });

        // Set up the Register button click listener
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail() && validatePassword() && validateAgreements()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // Reload the user to get the latest data
                        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (user.isEmailVerified()) {
                                    // Proceed with registration
                                    Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    // Continue with registration or navigate to another activity
                                } else {
                                    Toast.makeText(RegistrationActivity.this, "Please verify your email before registering.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegistrationActivity.this, "User not found. Please try again.", Toast.LENGTH_SHORT).show();
                    }
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
        if (password.isEmpty() || password.length() < 4) {
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

    // Method to send a verification email using Firebase
    private void sendFirebaseVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            String email = regemail.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser newUser = mAuth.getCurrentUser();
                            if (newUser != null) {
                                newUser.sendEmailVerification()
                                        .addOnCompleteListener(verificationTask -> {
                                            if (verificationTask.isSuccessful()) {
                                                Toast.makeText(RegistrationActivity.this, "Verification email sent to " + newUser.getEmail(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(RegistrationActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
