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
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.invaderx.firebasetrigger.Models.UserProfile;
import com.invaderx .firebasetrigger.R;

public class UserSignUp extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText email,password,name,phone;
    private CheckBox checkBox;
    private ImageButton signup;
    private Button signin;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String uname,uphone;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);
        mAuth = FirebaseAuth.getInstance();
        toolbar =findViewById(R.id.toolbar);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        checkBox =findViewById(R.id.checkbox);
        signup =findViewById(R.id.signup);
        signin = findViewById(R.id.signin);

        //database references-------------------------------
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        //progress dialog box
        progressDialog = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setMessage("Almost done!\nRegistering User");


        signup.setOnClickListener(view -> registerUser());

        signin.setOnClickListener(view -> {
            Intent i = new Intent(UserSignUp.this, UserLogin.class);
            startActivity(i);
            finish();
        });

        setStatusBarGradiant(this);

    }

    //registers new user
    private void registerUser() {
        uname = name.getText().toString().trim();
        String uemail = email.getText().toString().trim().toLowerCase();
        String upassword = password.getText().toString().trim();
        uphone = phone.getText().toString().trim();

        if (uname.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
            return;
        }

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

        if (upassword.length() < 6) {
            password.setError("Minimum lenght of password should be 6");
            password.requestFocus();
            return;
        }


        if (uphone.isEmpty()) {
            phone.setError("Phone no is required");
            phone.requestFocus();
            return;
        }

        if (uphone.length()!=10) {
            phone.setError("Phone no is invalid");
            phone.requestFocus();
            return;
        }

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(uemail, upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //Adding user name
                    user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(uname).build();
                    user.updateProfile(profileUpdates);

                    userDeatails(user);
                    progressDialog.dismiss();
                    //User Created
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), UserLogin.class));
                    finish();

                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        showSnackbar("You are already registered");

                    } else {
                        showSnackbar(task.getException().getMessage());
                    }

                    progressDialog.dismiss();
                }

            }
        });
    }

    //saves user details on board
    public void userDeatails(FirebaseUser firebaseUser){
        UserProfile userProfile=new UserProfile(firebaseUser.getUid(),"+91"+uphone,0);
        databaseReference.child("UserProfile").child(firebaseUser.getUid()).setValue(userProfile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showSnackbar("You are registered, Now Login");
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
