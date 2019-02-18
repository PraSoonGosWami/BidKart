package com.invaderx.firebasetrigger.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        checkBox = findViewById(R.id.checkbox);
        passReset = findViewById(R.id.passReset);
        signin =findViewById(R.id.signin);
        signup = findViewById(R.id.signup);

        progressDialog = new ProgressDialog(this);
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
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();

                    }
                });
    }



}
