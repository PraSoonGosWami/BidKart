package com.invaderx.firebasetrigger.Fragments;

import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.invaderx.firebasetrigger.Models.UserProfile;
import com.invaderx.firebasetrigger.R;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 234;
    private ImageView profileImage;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView walletTextView;
    private FloatingActionButton floatingActionButton;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri filePath;
    private int wallet;
    private LinearLayout walletLayout;
    private PopupWindow popWindow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);//clearing back stack for fragment

        //Binding views---------
        profileImage = view.findViewById(R.id.profileImageView);
        floatingActionButton = view.findViewById(R.id.editProfileDetails);
        nameTextView = view.findViewById(R.id.profile_nameView);
        emailTextView = view.findViewById(R.id.profile_emailView);
        phoneTextView = view.findViewById(R.id.profile_phoneView);
        walletTextView = view.findViewById(R.id.profile_walletView);
        walletLayout = view.findViewById(R.id.wallet_layout);
        walletLayout.setEnabled(true);
        //--------

        walletTextView.setOnClickListener(v -> addMoneyWallet(user.getUid()));
        //database references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Log.v("Username", name);
            nameTextView.setText(name);
            emailTextView.setText(email);
            getUserDetails(user.getUid());
        } else {
            nameTextView.setText("No user name");
        }
        return view;
    }

    //gets user details
    public void getUserDetails(String uid) {
        //setting wallet amount
        databaseReference.child("UserProfile").orderByChild("uid").equalTo(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserProfile userProfile = null;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                userProfile = data.getValue(UserProfile.class);
                            }
                            wallet = userProfile.getWallet();
                            walletTextView.setText("₹" + userProfile.getWallet());
                            phoneTextView.setText(userProfile.getPhone());
                        } else
                            walletTextView.setText("₹0");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    //adds money to wallet
    public void addMoneyWallet(String uid) {
        walletLayout.setEnabled(true);

        EditText refillAmount;
        ImageView cancelRefill;
        FloatingActionButton doneFillup;

        View inflatedView = getLayoutInflater().inflate(R.layout.wallet_add_popup, null, false);
        popWindow = new PopupWindow(inflatedView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        refillAmount = inflatedView.findViewById(R.id.refillAmount);
        cancelRefill = inflatedView.findViewById(R.id.cancelRefill);
        doneFillup = inflatedView.findViewById(R.id.doneFillup);


        // get device size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        //mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, width, height - 60, true);
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(inflatedView, Gravity.BOTTOM, 0, 100);


        cancelRefill.setOnClickListener(v -> {
            popWindow.dismiss();
            walletLayout.setEnabled(true);
        });

        doneFillup.setOnClickListener(v -> {
            if (refillAmount.getText().toString().isEmpty())
                refillAmount.setError("Enter a valid amount");
            else {
                int amount = Integer.parseInt(refillAmount.getText().toString());
                databaseReference.child("UserProfile").child(uid).child("wallet").setValue(amount + wallet)
                        .addOnSuccessListener(m -> {
                            popWindow.dismiss();
                            Toast.makeText(getActivity(), "Money Added Successfully", Toast.LENGTH_SHORT).show();
                        });
            }
            walletLayout.setEnabled(true);

        });

    }
}
