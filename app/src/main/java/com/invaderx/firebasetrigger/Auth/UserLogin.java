package com.invaderx.firebasetrigger.Auth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.invaderx.firebasetrigger.R;
import com.invaderx.firebasetrigger.Activity.MainActivity;

public class UserLogin extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText email,password;
    private CheckBox checkBox;
    private ImageButton signin;
    private Button signup;
    private FirebaseAuth mAuth;
    private TextView passReset;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        checkBox = findViewById(R.id.checkbox);
        passReset = findViewById(R.id.passReset);
        signin =findViewById(R.id.signin);
        signup = findViewById(R.id.signup);

        progressDialog = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setMessage("Almost done\nLogging you in...");

        signin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                userLogin();

            }
        });
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserLogin.this,UserSignUp.class);
                startActivity(i);
                finish();
            }
        });
        passReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordReset();
            }
        });

        setStatusBarGradiant(this);
    }

    private void userLogin() {
        String uemail = email.getText().toString().trim();
        String upassword = password.getText().toString().trim();

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


        mAuth.signInWithEmailAndPassword(uemail, upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(UserLogin.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    showSnackbar(task.getException().getMessage());
                }
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }


    // Password Reset Method
    public void sendPasswordReset() {
        // [START send_password_reset]
        String uemail = email.getText().toString().trim();

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

    //changes status bar color to gradient
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.background);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    //snackbar
    public void showSnackbar(String msg){
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }




}
