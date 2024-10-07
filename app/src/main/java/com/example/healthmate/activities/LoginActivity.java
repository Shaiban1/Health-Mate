package com.example.healthmate.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.healthmate.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin, passwordLogin;
    private TextView forgotPassTv, redirectToSignup;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient googleSignInClient;

    // Google Sign-in result handler
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult();
                    if (account != null) {
                        firebaseAuthWithGoogle(account.getIdToken());
                    } else {
                        Log.w(TAG, "Google sign-in failed: Account is null");
                        showToast("Google sign-in failed");
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        hideSystemBars();
        initViews();
        setupGoogleSignIn();

        findViewById(R.id.button_Login).setOnClickListener(v -> emailPasswordLogin());
        findViewById(R.id.google_login_btn).setOnClickListener(v -> signInWithGoogle());
        findViewById(R.id.phone_Login).setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, PhoneVerifyActivity.class)));
        forgotPassTv.setOnClickListener(v -> showForgotPasswordDialog());
        redirectToSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));

        mAuth = FirebaseAuth.getInstance();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        // Add insets listener to adjust UI padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        if (user != null) {
            navigateToMainActivity();
        }
    }

    // Email/password login
    private void emailPasswordLogin() {
        String email = emailLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill in both fields");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail: Success");
                        user = mAuth.getCurrentUser();
                        updateLoginStatus(true);
                        navigateToMainActivity();
                    } else {
                        Log.w(TAG, "signInWithEmail: Failure", task.getException());
                        showToast("Authentication failed.");
                    }
                });
    }

    // Google sign-in initiation
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    // Handle Firebase authentication with Google
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        updateLoginStatus(true);
                        showToast("Sign in successful.");
                        navigateToMainActivity();
                    } else {
                        Log.w(TAG, "Google sign-in failed", task.getException());
                        showToast("Google sign-in failed.");
                    }
                });
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id)) // Ensure that web_client_id is set in strings.xml
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }


    // Show forgot password dialog
    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reset_password, null);
        builder.setView(dialogView);

        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogView.findViewById(R.id.submitBtn).setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (!isValidEmail(email)) {
                showToast("Please enter a valid email");
                return;
            }
            resetPassword(email);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.cancelBtn).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Check for valid email format
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Reset password through Firebase
    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Password reset email sent.");
                    } else {
                        showToast("Failed to send reset email.");
                    }
                });
    }

    // Initialize views
    private void initViews() {
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setAnimation(R.raw.lottieflow_login);
        lottieAnimationView.playAnimation();

        emailLogin = findViewById(R.id.emailEditText);
        passwordLogin = findViewById(R.id.passwordEditText);
        forgotPassTv = findViewById(R.id.forgotPasswordTextView);
        redirectToSignup = findViewById(R.id.signUpTextView_redirect);
    }

    // Hide system bars for immersive experience
    private void hideSystemBars() {
        WindowInsetsControllerCompat insetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (insetsController != null) {
            insetsController.hide(WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.navigationBars());
            insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    // Navigate to main activity
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Update login status in SharedPreferences
    private void updateLoginStatus(boolean isLoggedIn) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("isLoggedIn", isLoggedIn).apply();
    }

    // Show toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
