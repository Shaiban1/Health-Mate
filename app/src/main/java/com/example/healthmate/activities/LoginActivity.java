package com.example.healthmate.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/** @noinspection ALL*/
public class LoginActivity extends AppCompatActivity {

    private EditText email_login, password_login;
    private TextView forgot_pass_tv;


    FirebaseAuth mAuth;
    FirebaseUser current_user;
    private GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;
    LottieAnimationView lottieAnimationView;


    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();

                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();

                if(account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            }
        }
    });




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        WindowInsetsControllerCompat insetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (insetsController != null) {
            insetsController.hide(WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.navigationBars());
            insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
        setContentView(R.layout.activity_login);



        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setAnimation(R.raw.lottieflow_login); // Replace with your animation file name
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mAuth = FirebaseAuth.getInstance();

        Button sign_in_Btn = findViewById(R.id.button_Login);
        email_login = findViewById(R.id.emailEditText);
        password_login = findViewById(R.id.passwordEditText);
        ImageButton google_Sign_in_btn = findViewById(R.id.google_login_btn);
        ImageButton phone_verify_activity = findViewById(R.id.phone_Login);
        forgot_pass_tv = findViewById(R.id.forgotPasswordTextView);













        sign_in_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmailPasswordLogin();
            }
        });



        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        google_Sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInMethod();
            }
        });


        phone_verify_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PhoneVerifyActivity.class);
                startActivity(intent);
                finish();
            }
        });


        forgot_pass_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openforgotDialog();
            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();

        current_user = mAuth.getCurrentUser();
        if(current_user != null){
            reload();
        }
    }






    public void EmailPasswordLogin() {
        String email = email_login.getText().toString().trim();
        String password = password_login.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG,"signInWithEmail:Success");
                    current_user = mAuth.getCurrentUser();
                    updateUI(current_user);
                } else {
                    Log.w(TAG,"signInWithEmail:Failure",task.getException());
                    Toast.makeText(LoginActivity.this,"Authentication failed.",Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }



    public void GoogleSignInMethod() {
        Intent signinIntent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(signinIntent);

    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            current_user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this,"Sign in successful.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            updateUI(current_user);
                        } else {
                            Log.w(TAG,"Something went wrong...",task.getException());
                        }
                    }
                });
    }

    private void openforgotDialog() {

    }



    private void reload() {
        if(current_user == null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            reload();
        } else {
            Toast.makeText(LoginActivity.this, "Something went wrong",Toast.LENGTH_SHORT).show();
        }
    }
}