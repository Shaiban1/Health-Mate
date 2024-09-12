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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.healthmate.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneVerifyActivity extends AppCompatActivity {

    private TextInputEditText phoneNumberEditText;
    private TextView blockTimeTextView;
    private FirebaseAuth pAuth;
    private LottieAnimationView lottieAnimationView;
    FrameLayout loadingOverlay;
    private static final long BLOCK_DURATION_MS = 5 * 60 * 1000;
    private long blockEndTime = 0;
    private EditText otpBox1, otpBox2, otpBox3, otpBox4, otpBox5, otpBox6;
    private BottomSheetDialog bottomSheetDialog;
    private String verificationId;
    private String autoFilledOtp;




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
        loadingOverlay = findViewById(R.id.loadingOverlay);
        lottieAnimationView = findViewById(R.id.lottieAnimationView_phone_wait);
        pAuth = FirebaseAuth.getInstance();


        sendOtpButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText() != null ? phoneNumberEditText.getText().toString().trim() : "";


            if (validatePhoneNumber(phoneNumber)) {
                showLoadingAnimation(); // Start Lottie animation
                sendVerificationCode(phoneNumber);
            }
        });

        // Check if there is an existing block and start the timer
        checkBlockStatus();

    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            setPhoneNumberError("Phone number cannot be empty");
            return false;
        } else if (!phoneNumber.matches("\\d{10}")) {
            setPhoneNumberError("Enter a valid 10-digit phone number");
            return false;
        } else {
            clearPhoneNumberError();
            return true;
        }
    }

    private void showLoadingAnimation() {
        loadingOverlay.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();
    }

    private void hideLoadingAnimation() {
        lottieAnimationView.cancelAnimation();
        loadingOverlay.setVisibility(View.GONE);
    }

    private void setPhoneNumberError(String error) {
        TextInputLayout phoneNumberLayout = findViewById(R.id.textInputLayoutPhoneNumber);
        phoneNumberLayout.setError(error);
    }

    private void clearPhoneNumberError() {
        TextInputLayout phoneNumberLayout = findViewById(R.id.textInputLayoutPhoneNumber);
        phoneNumberLayout.setError(null);
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
                        hideLoadingAnimation();

                        autoFilledOtp = credential.getSmsCode(); // Store the received OTP

                        if (autoFilledOtp != null) {
                            // Auto-fill the OTP in the OTP fields
                            otpBox1.setText(String.valueOf(autoFilledOtp.charAt(0)));
                            otpBox2.setText(String.valueOf(autoFilledOtp.charAt(1)));
                            otpBox3.setText(String.valueOf(autoFilledOtp.charAt(2)));
                            otpBox4.setText(String.valueOf(autoFilledOtp.charAt(3)));
                            otpBox5.setText(String.valueOf(autoFilledOtp.charAt(4)));
                            otpBox6.setText(String.valueOf(autoFilledOtp.charAt(5)));
                            verifyCode(verificationId, autoFilledOtp);
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        hideLoadingAnimation();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            setPhoneNumberError("Invalid phone number. Please try again.");
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            startBlockTimer();
                            Toast.makeText(PhoneVerifyActivity.this, "Too many requests. Try again later.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PhoneVerifyActivity.this, "Verification failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        hideLoadingAnimation();  // Ensure this method is called to hide the entire overlay
                        PhoneVerifyActivity.this.verificationId = verificationId; // Store verificationId in member variable
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
        bottomSheetDialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_otp, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        otpBox1 = bottomSheetView.findViewById(R.id.otpBox1);
        otpBox2 = bottomSheetView.findViewById(R.id.otpBox2);
        otpBox3 = bottomSheetView.findViewById(R.id.otpBox3);
        otpBox4 = bottomSheetView.findViewById(R.id.otpBox4);
        otpBox5 = bottomSheetView.findViewById(R.id.otpBox5);
        otpBox6 = bottomSheetView.findViewById(R.id.otpBox6);

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