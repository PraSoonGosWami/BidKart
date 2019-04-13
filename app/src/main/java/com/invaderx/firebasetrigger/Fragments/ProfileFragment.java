package com.invaderx.firebasetrigger.Fragments;

import android.app.ProgressDialog;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.invaderx.firebasetrigger.Models.UserProfile;
import com.invaderx.firebasetrigger.R;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static com.airbnb.lottie.L.TAG;

public class ProfileFragment extends Fragment implements BSImagePicker.OnSingleImageSelectedListener {
    private ImageView profileImage, profileBackgroundImageView;
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
    private int wallet;
    private LinearLayout walletLayout;
    private PopupWindow popWindow;
    private ProgressDialog progressDialog;

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
        profileBackgroundImageView = view.findViewById(R.id.profileBackgroundImageView);
        walletLayout.setEnabled(true);
        //--------

        walletTextView.setOnClickListener(v -> addMoneyWallet(user.getUid()));
        //database references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        progressDialog = new ProgressDialog(getContext(), ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setMessage("Uploading..");

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Log.v("Username", name);
            nameTextView.setText(name);
            emailTextView.setText(email);
            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .error(R.drawable.ic_verify)
                    .centerCrop()
                    .into(profileImage);
            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .centerCrop()
                    .into(profileBackgroundImageView);
            getUserDetails(user.getUid());

        } else {
            nameTextView.setText("No user name");
        }
        floatingActionButton.setOnClickListener(v -> {

            imagePicker();
        });
        return view;
    }

    //gets user details
    public void getUserDetails(String uid) {
        //setting wallet amount
        databaseReference.child("UserProfile").orderByChild("uid").equalTo(uid)
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

    //add updates profile pic

    public void profilePic(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Glide.with(getContext())
                                    .load(user.getPhotoUrl())
                                    .error(R.drawable.ic_verify)
                                    .centerCrop()
                                    .into(profileImage);
                            Glide.with(getContext())
                                    .load(user.getPhotoUrl())
                                    .centerCrop()
                                    .into(profileBackgroundImageView);
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Successful", Snackbar.LENGTH_SHORT).show();
                            progressDialog.dismiss();


                        }
                    }
                });
    }


    //opens image picker dialog
    public void imagePicker() {
        BSImagePicker picker = new BSImagePicker.Builder("com.invaderx.firebasetrigger.fileprovider")
                .setMaximumDisplayingImages(24)
                .setSpanCount(3)
                .setGridSpacing(Utils.dp2px(2))
                .setPeekHeight(Utils.dp2px(360))
                .setTag("A request ID")
                .build();
        picker.show(getChildFragmentManager(), "picker");
    }

    //returns file path
    @Override
    public void onSingleImageSelected(Uri uri, String tag) {


        progressDialog.show();
        storageReference.child(user.getUid()).child("profilepic")
                .putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.child(user.getUid()).child("profilepic").getDownloadUrl().addOnSuccessListener(uri1 -> {
                        if (uri1 != null)
                            profilePic(uri1);
                        else
                            Toast.makeText(getContext(), "Technical Issue\tTry again", Toast.LENGTH_SHORT).show();

                    });

                })
                .addOnFailureListener(exception -> {
                    progressDialog.dismiss();

                })
                .addOnProgressListener(taskSnapshot -> {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                });
    }
}
