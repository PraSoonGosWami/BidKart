package com.invaderx.firebasetrigger.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.Models.Transactions;
import com.invaderx.firebasetrigger.Models.UserProfile;
import com.invaderx.firebasetrigger.R;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class PaymentActivity extends AppCompatActivity {

    private String proId;
    private RelativeLayout pay_ui;
    private CardView wal;
    private TextView pay_wallet_amount;
    private TextView pay_pro_name;
    private TextView pay_pro_cost;
    private ImageView pay_pro_img;
    private ProgressBar pay_progressbar;
    private Button pay_amount;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private int amount = 0, userWalletAmount = 0, sellerWalletAmount = 0;
    private String selleruid;
    private String sellerName;
    private ProgressDialog progressDialog;
    String pName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        proId = getIntent().getStringExtra("pid");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Make Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();

        //binding views ----------------------------------------------------------------

        pay_ui = findViewById(R.id.pay_ui);
        wal = findViewById(R.id.wal);
        pay_wallet_amount = findViewById(R.id.pay_wallet_amount);
        pay_pro_name = findViewById(R.id.pay_pro_name);
        pay_pro_cost = findViewById(R.id.pay_pro_cost);
        pay_pro_img = findViewById(R.id.pay_pro_img);
        pay_progressbar = findViewById(R.id.pay_progressbar);
        pay_amount = findViewById(R.id.pay_amount);

        //-------------------------------------------------------------------------------

        //database references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("Almost done\nTransaction in process...");

        pay_ui.setVisibility(View.GONE);
        pay_progressbar.setVisibility(View.VISIBLE);

        wal.setOnClickListener(v -> {
            //open wallet activity
        });

        pay_amount.setOnClickListener(v -> {
            //do payment
            makePayement();
        });
        getProduct(proId);
        getUserWallet();



    }

    public void getProduct(String pid) {

        databaseReference.child("product").orderByChild("pId").equalTo(pid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        pay_progressbar.setVisibility(View.GONE);
                        Products products = null;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren())
                                products = data.getValue(Products.class);
                            pay_pro_name.setText(products.getpName());
                            pay_pro_cost.setText("₹" + Collections.max(products.getpBid().values()));
                            Glide.with(getApplicationContext()).
                                    load(products.getProductListImgURL())
                                    .into(pay_pro_img);
                            pay_ui.setVisibility(View.VISIBLE);
                            pay_amount.setText("PAY ₹" + Collections.max(products.getpBid().values()));

                            amount = Collections.max(products.getpBid().values());
                            selleruid = products.getSellerUID();
                            sellerName = products.getSellerName();
                            pName = products.getpName();
                        } else {
                            Toast.makeText(PaymentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            pay_ui.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(PaymentActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //gets user wallet amount
    public void getUserWallet() {

        databaseReference.child("UserProfile").orderByChild("uid").equalTo(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserProfile userProfile = null;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                userProfile = data.getValue(UserProfile.class);
                            }
                            pay_wallet_amount.setText("₹" + userProfile.getWallet());
                            userWalletAmount = userProfile.getWallet();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    //gets seller wallet amount
    public void getSellerWallet() {

        databaseReference.child("UserProfile").orderByChild("uid").equalTo(selleruid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserProfile userProfile = null;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                userProfile = data.getValue(UserProfile.class);
                            }
                            sellerWalletAmount = userProfile.getWallet();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    public void makePayement() {
        if (userWalletAmount < amount)
            Toast.makeText(this, "Wallet balance low\nAdd funds by tapping the wallet", Toast.LENGTH_SHORT).show();
        else {
            progressDialog.show();
            databaseReference.child("product").child(proId).child("pStatus").setValue("done")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                updateWallet();
                            }

                            else
                                Toast.makeText(PaymentActivity.this, "Try Again later!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


    }

    public void generateTransaction() {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        String tID = sb.toString();
        String id = new Timestamp(System.currentTimeMillis()).getTime() + tID;

        Transactions forSeller = new Transactions(selleruid, user.getUid(), user.getDisplayName(), String.valueOf(amount), pName, id, getDate());
        Transactions forBuyer = new Transactions(selleruid, user.getUid(), sellerName, String.valueOf(amount), pName, id, getDate());

        //for bidder
        databaseReference.child("Transactions").child(user.getUid()).child(id).setValue(forBuyer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            databaseReference.child("Transactions").child(selleruid).child(id).setValue(forSeller)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(PaymentActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }


                                        }
                                    });
                        }
                    }
                });


    }

    public void updateWallet() {
        getSellerWallet();

        databaseReference.child("UserProfile").child(user.getUid()).child("wallet").setValue(userWalletAmount - amount)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if (sellerWalletAmount != 0) {
                            databaseReference.child("UserProfile").child(selleruid).child("wallet").setValue(sellerWalletAmount + amount)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            generateTransaction();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            databaseReference.child("UserProfile").child(user.getUid()).child("wallet").setValue(userWalletAmount + amount);

                                        }
                                    });
                        }
                    }
                });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getDate() {
        Date dt = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        return formatter.format(dt);
    }
}
