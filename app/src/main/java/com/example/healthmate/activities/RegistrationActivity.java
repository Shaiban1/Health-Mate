package com.example.healthmate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthmate.R;
import com.example.healthmate.fragment.VerificationBottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private EditText regemail, passwordEditText, confirmpasswordEditText;
    private CheckBox checkagreements;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private TextView redirectLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        regemail = findViewById(R.id.regemail);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmpasswordEditText = findViewById(R.id.confirmpasswordEditText);
        checkagreements = findViewById(R.id.checkagreements);
        btnSignUp = findViewById(R.id.btnSignUp);
        redirectLogin = findViewById(R.id.redirect_to_login);




        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        btnSignUp.setOnClickListener(v -> {
            if (validateEmail() && validatePassword() && validateAgreements()) {
                createAccountAndSendVerificationEmail();
            }
        });

        redirectLogin.setOnClickListener(v -> {
            Intent i = new Intent(RegistrationActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
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
                                            showVerificationBottomSheet();
                                        } else {
                                            Toast.makeText(RegistrationActivity.this, "Failed to send verification email: " + verificationTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegistrationActivity.this, "Your account is already registered. Please log in.", Toast.LENGTH_LONG).show();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(RegistrationActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Show the verification bottom sheet
    private void showVerificationBottomSheet() {
        VerificationBottomSheetDialogFragment bottomSheet = new VerificationBottomSheetDialogFragment();
        bottomSheet.setCancelable(false);
        bottomSheet.show(getSupportFragmentManager(), "VerificationBottomSheet");
    }
}
