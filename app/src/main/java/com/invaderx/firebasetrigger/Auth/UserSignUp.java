package com.invaderx.firebasetrigger.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.invaderx.firebasetrigger.Models.UserProfile;
import com.invaderx.firebasetrigger.R;

public class UserSignUp extends AppCompatActivity {

    private TextInputLayout email_layout, name_layout, phone_layout, password_layout;
    private EditText sEmail_edit_text, sName_edit_text, sPhone_edit_text, sPassword_edit_Text;
    private FloatingActionButton sSignup;
    private TextView sSignin;
    private String uToken;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String uname, uphone, uemail, upassword;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);
        mAuth = FirebaseAuth.getInstance();

        email_layout = findViewById(R.id.email_layout);
        name_layout = findViewById(R.id.name_layout);
        phone_layout = findViewById(R.id.phone_layout);
        password_layout = findViewById(R.id.password_layout);

        sEmail_edit_text = findViewById(R.id.sEmail_edit_text);
        sName_edit_text = findViewById(R.id.sName_edit_text);
        sPhone_edit_text = findViewById(R.id.sPhone_edit_text);
        sPassword_edit_Text = findViewById(R.id.sPassword_edit_Text);
        sSignup = findViewById(R.id.sSignup);
        sSignin = findViewById(R.id.sSignin);

        //gets device token for fcm
        uToken = FirebaseInstanceId.getInstance().getToken();

        //database references-------------------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //progress dialog box
        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setMessage("Almost done!\nRegistering User");


        sSignup.setOnClickListener(view -> registerUser());

        sSignin.setOnClickListener(view -> {
            Intent i = new Intent(UserSignUp.this, UserLogin.class);
            startActivity(i);
            finish();
        });
        sName_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                name_layout.setErrorEnabled(false);
                checkNameValidity();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name_layout.setErrorEnabled(false);
                checkNameValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
                name_layout.setErrorEnabled(false);
                checkNameValidity();
            }
        });
        sEmail_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                email_layout.setErrorEnabled(false);
                checkEmailValidity();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email_layout.setErrorEnabled(false);
                checkEmailValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
                email_layout.setErrorEnabled(false);
                checkEmailValidity();
            }
        });
        sPhone_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                phone_layout.setErrorEnabled(false);
                checkPhoneValidity();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone_layout.setErrorEnabled(false);
                checkPhoneValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
                phone_layout.setErrorEnabled(false);
                checkPhoneValidity();
            }
        });
        sPassword_edit_Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                password_layout.setErrorEnabled(false);
                checkPasswordValidity();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password_layout.setErrorEnabled(false);
                checkPasswordValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
                password_layout.setErrorEnabled(false);
                checkPasswordValidity();
            }
        });


    }

    //registers new user
    private void registerUser() {
        uname = sName_edit_text.getText().toString().trim();
        uemail = sEmail_edit_text.getText().toString().trim().toLowerCase();
        upassword = sPassword_edit_Text.getText().toString().trim();
        uphone = sPhone_edit_text.getText().toString().trim();

        if (uemail.isEmpty()) {
            email_layout.setError("Email is required");
            email_layout.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
            email_layout.setError("Please enter a valid email");
            email_layout.requestFocus();
            return;
        }

        if (uname.isEmpty()) {
            name_layout.setError("Name is required");
            name_layout.requestFocus();
            return;
        }
        if (uphone.isEmpty()) {
            phone_layout.setError("Phone no is required");
            phone_layout.requestFocus();
            return;
        }

        if (uphone.length() != 10) {
            phone_layout.setError("Phone no is invalid");
            phone_layout.requestFocus();
            return;
        }

        if (upassword.isEmpty()) {
            password_layout.setError("Password is required");
            password_layout.requestFocus();
            return;
        }

        if (upassword.length() < 6) {
            password_layout.setError("Minimum lenght of password should be 6");
            password_layout.requestFocus();
            return;
        }


        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(uemail, upassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                //Adding user name
                user = mAuth.getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(uname).build();
                user.sendEmailVerification()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Verification Email Sent", Toast.LENGTH_SHORT).show();
                                user.updateProfile(profileUpdates).addOnSuccessListener(aVoid -> {
                                    userDeatails(user);
                                    progressDialog.dismiss();
                                    //User Created
                                    startActivity(new Intent(getApplicationContext(), UserLogin.class));
                                    finish();
                                });
                            } else
                                showSnackbar("Registration failed\nTry again");
                        });

            } else {

                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    showSnackbar("You are already registered");

                } else {
                    showSnackbar(task.getException().getMessage());
                }

                progressDialog.dismiss();
            }

        });
    }

    //saves user details on board
    public void userDeatails(FirebaseUser firebaseUser) {
        UserProfile userProfile = new UserProfile(firebaseUser.getUid(), "+91" + uphone, 0, uToken);
        databaseReference.child("UserProfile").child(firebaseUser.getUid()).setValue(userProfile)
                .addOnSuccessListener(aVoid -> showSnackbar(
                        "You are registered, Now Login"));
    }


    //snackbar
    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void checkEmailValidity() {
        uemail = sEmail_edit_text.getText().toString().trim().toLowerCase();
        if (uemail.isEmpty()) {
            email_layout.setError("Email is required");
            email_layout.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
            email_layout.setError("Please enter a valid email");
            email_layout.requestFocus();
        }
    }

    public void checkNameValidity() {
        uname = sName_edit_text.getText().toString().trim();
        if (uname.isEmpty()) {
            name_layout.setError("Name is required");
            name_layout.requestFocus();
        }
    }

    public void checkPhoneValidity() {
        uphone = sPhone_edit_text.getText().toString().trim();
        if (uphone.isEmpty()) {
            phone_layout.setError("Phone no is required");
            phone_layout.requestFocus();
            return;
        }

        if (uphone.length() != 10) {
            phone_layout.setError("Phone no is less than 10 digits");
            phone_layout.requestFocus();
        }
    }

    public void checkPasswordValidity() {
        upassword = sPassword_edit_Text.getText().toString().trim();
        if (upassword.isEmpty()) {
            password_layout.setError("Password is required");
            password_layout.requestFocus();
            return;
        }

        if (upassword.length() < 6) {
            password_layout.setError("Minimum lenght of password should be 6");
            password_layout.requestFocus();
            return;
        }
    }


}
