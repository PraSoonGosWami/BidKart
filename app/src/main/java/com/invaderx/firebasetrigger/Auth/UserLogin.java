package com.invaderx.firebasetrigger.Auth;

import android.animation.Animator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.invaderx.firebasetrigger.Activity.MainActivity;
import com.invaderx.firebasetrigger.R;

import java.util.Objects;

public class UserLogin extends AppCompatActivity {

    private EditText email_edit_text, password_edit_text;
    private TextInputLayout email, password;
    private FloatingActionButton signin;
    private TextView signup;
    private FirebaseAuth mAuth;
    private TextView passReset;
    private TextView admin_text;
    private ProgressDialog progressDialog;
    private FirebaseUser user;
    private String uToken;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        mAuth = FirebaseAuth.getInstance();
        //binding views----------------------------------------
        email_edit_text = findViewById(R.id.email_edit_text);
        password_edit_text = findViewById(R.id.password_edit_Text);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passReset = findViewById(R.id.passReset);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        admin_text = findViewById(R.id.admin_text);
        //-------------------------------------------

        //gets device token for fcm
        uToken = FirebaseInstanceId.getInstance().getToken();

        //database references-------------------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        admin_text.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminLogin.class));
            finish();
        });

        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setMessage("Almost done\nLogging you in...");
        signin.setOnClickListener(view -> userLogin());
        signup.setOnClickListener(view -> {
            Intent i = new Intent(UserLogin.this, UserSignUp.class);
            startActivity(i);
            finish();
        });
        passReset.setOnClickListener(v -> sendPasswordReset());

        //checks edit text formatting-------------------------
        email_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                email.setErrorEnabled(false);
                checkEmailValidity();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email.setErrorEnabled(false);
                checkEmailValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
                email.setErrorEnabled(false);
                checkEmailValidity();
            }
        });
        password_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                password.setErrorEnabled(false);
                checkPasswordValidity();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password.setErrorEnabled(false);
                checkPasswordValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
                password.setErrorEnabled(false);
                checkPasswordValidity();
            }
        });
        //---------------------------------------------------

    }

    //performs user login
    private void userLogin() {
        String uemail = email_edit_text.getText().toString().trim();
        String upassword = password_edit_text.getText().toString().trim();

        if (uemail.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
            email.setError("Please enter a valid email");
            email.requestFocus();
            return;
        }

        if (upassword.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        progressDialog.show();


        mAuth.signInWithEmailAndPassword(uemail, upassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user = mAuth.getCurrentUser();
                assert user != null;
                if (user.isEmailVerified()) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(UserLogin.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    setUtoken(user);
                    startActivity(intent);
                    finish();
                } else {

                    user.sendEmailVerification().addOnSuccessListener(aVoid -> {
                        showSnackbar("Email is not verified verify email first");
                        FirebaseAuth.getInstance().signOut();
                    });

                }
            } else {
                showSnackbar(Objects.requireNonNull(task.getException()).getMessage());
            }
            progressDialog.dismiss();
        });
    }

    //checks email format
    public void checkEmailValidity() {
        String uemail = email_edit_text.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
            email.setError("Please enter a valid email");
            email.requestFocus();
        }

    }

    //checks password verification
    public void checkPasswordValidity() {
        String upassword = password_edit_text.getText().toString().trim();
        if (upassword.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSplashScreen();


    }

    // Password Reset Method
    public void sendPasswordReset() {
        // [START send_password_reset]
        String uemail = email_edit_text.getText().toString().trim();

        if (uemail.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
            email.setError("Please enter a valid email");
            email.requestFocus();
            return;
        }
        progressDialog.setMessage("Sending reset link...");
        progressDialog.show();


        mAuth.sendPasswordResetEmail(uemail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showSnackbar("We have sent you instructions to reset your password!");
                        } else {
                            showSnackbar(task.getException().getMessage());
                        }
                        progressDialog.dismiss();

                    }
                });
    }

    //snackbar
    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    //shows popup splash screen
    public void showSplashScreen() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View v = factory.inflate(R.layout.splash_screen, null);
        LottieAnimationView lottieAnimationView = v.findViewById(R.id.splash_anim);

        final Dialog dialog = new Dialog(UserLogin.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.splash_screen);
        dialog.setCancelable(true);
        dialog.show();
        lottieAnimationView.playAnimation();

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dialog.dismiss();
                if (mAuth.getCurrentUser() != null) {
                    user = mAuth.getCurrentUser();
                    if (user.isEmailVerified()) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
       /*
        final Handler handler = new Handler();
        final Runnable runnable = () ->
                dialog.dismiss();
        handler.postDelayed(runnable, 4000);*/
    }

    //sets uToken when logged in
    public void setUtoken(FirebaseUser firebaseUser) {
        databaseReference.child("UserProfile").child(firebaseUser.getUid()).child("uToken").setValue(uToken)
                .addOnFailureListener(v -> {
                    Toast.makeText(this, "Please login again\nSomething went wrong", Toast.LENGTH_SHORT).show();
                });
    }


}
