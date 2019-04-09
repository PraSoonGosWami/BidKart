package com.invaderx.firebasetrigger.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.invaderx.firebasetrigger.Activity.AdminActivity;
import com.invaderx.firebasetrigger.R;

import java.util.Objects;

public class AdminLogin extends AppCompatActivity {

    private EditText email, password;
    private FloatingActionButton button_signin;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressDialog progressDialog;
    private TextView user_signin;
    private static final String ADMIN_EMAIL = "admin@bidkart.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);


        mAuth = FirebaseAuth.getInstance();

        //binding views----------------------------------------
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button_signin = findViewById(R.id.button_signin);
        user_signin = findViewById(R.id.user_signin);
        //-------------------------------------------

        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setMessage("Almost done\nLogging you in...");

        button_signin.setOnClickListener(v -> {
            signin();
        });

        user_signin.setOnClickListener(v -> {
            startActivity(new Intent(this, UserLogin.class));
            finish();
        });
    }

    public void signin() {

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


        if (uemail.equals(ADMIN_EMAIL)) {
            mAuth.signInWithEmailAndPassword(uemail, upassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();
                    progressDialog.dismiss();
                    Intent intent = new Intent(AdminLogin.this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            });
        } else {
            Toast.makeText(this, "You are not admin", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(AdminLogin.this, AdminActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
