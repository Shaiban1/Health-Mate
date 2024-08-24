package com.example.healthmate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthmate.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneVerifyActivity extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private TextView blockTimeTextView;
    private FirebaseAuth pAuth;

    private static final long BLOCK_DURATION_MS = 5 * 60 * 1000; // Example: 5 minutes block duration
    private long blockEndTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phone_verify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        phoneNumberEditText = findViewById(R.id.etPhoneNumber);
        Button sendOtpButton = findViewById(R.id.btnSubmit);
        blockTimeTextView = findViewById(R.id.blockTimeTextView);
        pAuth = FirebaseAuth.getInstance();


        sendOtpButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString();
            if (!phoneNumber.isEmpty()) {
                sendVerificationCode(phoneNumber);
            } else {
                Toast.makeText(PhoneVerifyActivity.this, "Enter phone number", Toast.LENGTH_SHORT).show();
            }
        });

        // Check if there is an existing block and start the timer
        checkBlockStatus();

    }

    private void sendVerificationCode(String phoneNumber) {
        // Prepend country code
        String completePhoneNumber = "+91" + phoneNumber;


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                completePhoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Auto verification code is received
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            startActivity(new Intent(PhoneVerifyActivity.this,LoginActivity.class));
                            finish();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // Too many requests
                            startBlockTimer();
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        showOtpBottomSheet(verificationId);
                    }
                }
        );
    }

    private void startBlockTimer() {
        blockEndTime = System.currentTimeMillis() + BLOCK_DURATION_MS;
        updateBlockTime();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateBlockTime() {
        long remainingTime = blockEndTime - System.currentTimeMillis();
        if (remainingTime > 0) {
            long minutes = (remainingTime / 1000) / 60;
            long seconds = (remainingTime / 1000) % 60;
            blockTimeTextView.setText(String.format("You can try again in: %02d:%02d", minutes, seconds));
            blockTimeTextView.postDelayed(this::updateBlockTime, 1000);
        } else {
            blockTimeTextView.setText("You can try again now.");
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkBlockStatus() {
        // This method should be called to check if the block timer is running
        // and if yes, update the blockTimeTextView accordingly
        long currentTime = System.currentTimeMillis();
        if (blockEndTime > currentTime) {
            updateBlockTime();
        } else {
            blockTimeTextView.setText("You can try again now.");
        }
    }

    private void showOtpBottomSheet(String verificationId) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_otp, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        EditText otpBox1 = bottomSheetView.findViewById(R.id.otpBox1);
        EditText otpBox2 = bottomSheetView.findViewById(R.id.otpBox2);
        EditText otpBox3 = bottomSheetView.findViewById(R.id.otpBox3);
        EditText otpBox4 = bottomSheetView.findViewById(R.id.otpBox4);
        EditText otpBox5 = bottomSheetView.findViewById(R.id.otpBox5);
        EditText otpBox6 = bottomSheetView.findViewById(R.id.otpBox6);
        Button verifyOtpButton = bottomSheetView.findViewById(R.id.verifyOtpButton);

        setOtpBoxListeners(otpBox1, otpBox2);
        setOtpBoxListeners(otpBox2, otpBox3);
        setOtpBoxListeners(otpBox3, otpBox4);
        setOtpBoxListeners(otpBox4, otpBox5);
        setOtpBoxListeners(otpBox5, otpBox6);

        verifyOtpButton.setOnClickListener(v -> {
            String otp = otpBox1.getText().toString() + otpBox2.getText().toString() +
                    otpBox3.getText().toString() + otpBox4.getText().toString() +
                    otpBox5.getText().toString() + otpBox6.getText().toString();

            if (otp.length() == 6) {
                verifyCode(verificationId, otp);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(PhoneVerifyActivity.this, "Enter full OTP", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    private void setOtpBoxListeners(EditText currentBox, EditText nextBox) {
        currentBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    nextBox.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void verifyCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        pAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(PhoneVerifyActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();  // Optionally finish PhoneLoginActivity
                    } else {
                        Toast.makeText(PhoneVerifyActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}