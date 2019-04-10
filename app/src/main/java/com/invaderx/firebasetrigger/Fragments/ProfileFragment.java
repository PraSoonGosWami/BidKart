package com.invaderx.firebasetrigger.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        //--------
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
        databaseReference.child("UserProfile").orderByChild("uid").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserProfile userProfile1 = null;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                userProfile1 = dataSnapshot1.getValue(UserProfile.class);
                            }

                            if (userProfile1.getPhone().equals("0"))
                                phoneTextView.setText("No Phone Number Added");
                            else
                                phoneTextView.setText(userProfile1.getPhone());
                            if (userProfile1.getWallet() == 0)
                                phoneTextView.setText("No Money");
                            else
                                phoneTextView.setText(userProfile1.getWallet());
                        } else {
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Something went wrong", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "" + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                });


    }
}
