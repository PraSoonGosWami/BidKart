package com.invaderx.firebasetrigger.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Auth.UserLogin;
import com.invaderx.firebasetrigger.Fragments.HomeFragment;
import com.invaderx.firebasetrigger.Fragments.NotificationFragment;
import com.invaderx.firebasetrigger.Fragments.ProfileFragment;
import com.invaderx.firebasetrigger.Fragments.SearchFragment;
import com.invaderx.firebasetrigger.Models.UserProfile;
import com.invaderx.firebasetrigger.R;
import com.roughike.bottombar.BottomBar;

public class MainActivity extends AppCompatActivity {

    private ImageButton action_bar_menu;
    private ImageView action_bar_appicon;
    private TextView action_bar_wallet;
    private EditText action_bar_search;
    private TextView action_bar_title;
    private DrawerLayout drawerLayout;
    private TextView nav_profile_name;
    private NavigationView navigationView;
    private String uToken = "logged out";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        //database references-------------------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.nav_sell:
                    drawerLayout.closeDrawers();
                    startActivity(new Intent(this, SellerActivity.class));
                    finish();
                    return true;
                case R.id.nav_logout:
                    logout();
                    return true;

            }
            return false;
        });

        //sets user name to sidenav drawer
        getDisplayName();

        //Action Bar custom View-------------------------------------
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_appbar);
        getSupportActionBar().setElevation(0);

        View view= getSupportActionBar().getCustomView();
        action_bar_menu = view.findViewById(R.id.action_bar_menu);
        action_bar_appicon = view.findViewById(R.id.action_bar_appicon);
        action_bar_wallet = view.findViewById(R.id.action_bar_notification);
        action_bar_search = view.findViewById(R.id.action_bar_search);
        action_bar_title = view.findViewById(R.id.action_bar_title);


        //------------------------------------------------------------

        //Bottom Nav Bar----------------------------------------------
        BottomBar bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(tabId -> {

            switch (tabId) {
                case R.id.bottom_home:
                    action_bar_appicon.setVisibility(View.VISIBLE);
                    action_bar_wallet.setVisibility(View.VISIBLE);
                    action_bar_search.setVisibility(View.GONE);
                    action_bar_title.setVisibility(View.GONE);
                    swapFragments(new HomeFragment());
                    break;
                case R.id.bottom_search:
                    action_bar_appicon.setVisibility(View.GONE);
                    action_bar_wallet.setVisibility(View.GONE);
                    action_bar_search.setVisibility(View.VISIBLE);
                    action_bar_title.setVisibility(View.GONE);
                    swapFragments(new SearchFragment());
                    break;

                case R.id.bottom_notification:
                    action_bar_appicon.setVisibility(View.GONE);
                    action_bar_wallet.setVisibility(View.GONE);
                    action_bar_search.setVisibility(View.GONE);
                    action_bar_title.setVisibility(View.VISIBLE);
                    action_bar_title.setText("Notifications");
                    swapFragments(new NotificationFragment());
                    break;

                case R.id.bottom_profile:
                    action_bar_appicon.setVisibility(View.GONE);
                    action_bar_wallet.setVisibility(View.GONE);
                    action_bar_search.setVisibility(View.GONE);
                    action_bar_title.setVisibility(View.VISIBLE);
                    action_bar_title.setText("Profile");
                    swapFragments(new ProfileFragment());
                    break;

                default:
                    action_bar_appicon.setVisibility(View.VISIBLE);
                    action_bar_wallet.setVisibility(View.VISIBLE);
                    action_bar_search.setVisibility(View.GONE);
                    action_bar_title.setVisibility(View.GONE);
                    swapFragments(new HomeFragment());
                    break;
            }


        });
        //---------------------------------------------------------------


        //hamburger icon for opening drawer
        action_bar_menu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.START))
                drawerLayout.closeDrawers();
            else
                drawerLayout.openDrawer(Gravity.START);


        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                            action_bar_wallet.setText("₹" + userProfile.getWallet());
                        } else
                            action_bar_wallet.setText("₹0");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawers();
        else if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            builder.setMessage("Are you sure you want exit?")
                    .setPositiveButton("Yes", (dialog, id) -> finish())
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
            builder.create().show();
        } else
            super.onBackPressed();
    }

    //performs logout
    public void logout() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setMessage("Are you sure you want logout?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    removesUtoken();
                    finish();
                    startActivity(new Intent(getApplicationContext(), UserLogin.class));
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.dismiss();
                    navigationView.setCheckedItem(R.id.nav_sell);
                });
        builder.create().show();


    }

    //replaces the container with fragments
    public void swapFragments(Fragment fragment){
        android.support.v4.app.FragmentTransaction fragmentTransaction;
        fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out).replace(R.id.container_frame,fragment);
        fragmentTransaction.commit();
    }

    //gets user display name
    public void getDisplayName(){
        View view = navigationView.getHeaderView(0);
        nav_profile_name=view.findViewById(R.id.nav_profile_name);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            Log.v("Username",name);
            // getImage(user.getUid(),imageView);

            nav_profile_name.setText(name);
        } else {
            nav_profile_name.setText("No user name");
        }
    }

    //removes uToken when logout
    public void removesUtoken() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("UserProfile").child(firebaseUser.getUid()).child("uToken").setValue(uToken)
                .addOnSuccessListener(v -> {
                    FirebaseAuth.getInstance().signOut();
                });
    }



}
