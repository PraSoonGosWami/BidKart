package com.invaderx.firebasetrigger.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Auth.UserLogin;
import com.invaderx.firebasetrigger.Fragments.HomeFragment;
import com.invaderx.firebasetrigger.Fragments.MyBidsFragment;
import com.invaderx.firebasetrigger.Fragments.ProfileFragment;
import com.invaderx.firebasetrigger.Fragments.SearchFragment;
import com.invaderx.firebasetrigger.Fragments.TranscationFragment;
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
    private ImageView nav_profile_pic;
    private NavigationView navigationView;
    private String uToken = "logged out";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private PopupWindow popWindow;
    private int wallet;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        //database references-------------------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    swapFragments(new HomeFragment());
                    return true;
                case R.id.nav_sell:
                    drawerLayout.closeDrawers();
                    startActivity(new Intent(this, SellerActivity.class));
                    finish();
                    return true;
                case R.id.nav_logout:
                    drawerLayout.closeDrawers();
                    logout();
                    return true;

                case R.id.nav_share:
                    drawerLayout.closeDrawers();
                    shareApp();
                    return true;

                case R.id.nav_contactUs:
                    drawerLayout.closeDrawers();
                    contactUs();
                    return true;
                case R.id.nav_myProducts:
                    swapFragments(new TranscationFragment(), "dark");
                    drawerLayout.closeDrawers();
                    action_bar_appicon.setVisibility(View.GONE);
                    action_bar_wallet.setVisibility(View.GONE);
                    action_bar_search.setVisibility(View.GONE);
                    action_bar_title.setVisibility(View.VISIBLE);
                    action_bar_title.setText("Transactions");
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

        View view = getSupportActionBar().getCustomView();
        action_bar_menu = view.findViewById(R.id.action_bar_menu);
        action_bar_appicon = view.findViewById(R.id.action_bar_appicon);
        action_bar_wallet = view.findViewById(R.id.action_bar_notification);
        action_bar_search = view.findViewById(R.id.action_bar_search);
        action_bar_title = view.findViewById(R.id.action_bar_title);

        action_bar_wallet.setOnClickListener(v -> {
            addMoneyWallet(FirebaseAuth.getInstance().getCurrentUser().getUid());
        });


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
                    action_bar_title.setText("My Bids");
                    swapFragments(new MyBidsFragment());
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
                            wallet = userProfile.getWallet();
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
        if (drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawers();
        else if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            builder.setMessage("Are you sure you want exit?")
                    .setPositiveButton("Yes", (dialog, id) -> finish())
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
            builder.create().show();
        } else {
            super.onBackPressed();
            navigationView.setCheckedItem(R.id.nav_home);
            action_bar_appicon.setVisibility(View.VISIBLE);
            action_bar_wallet.setVisibility(View.VISIBLE);
            action_bar_search.setVisibility(View.GONE);
            action_bar_title.setVisibility(View.GONE);
        }
    }

    //performs logout
    public void logout() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setMessage("Are you sure you want logout?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    removesUtoken();
                    finish();
                    startActivity(new Intent(getApplicationContext(), UserLogin.class));
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.dismiss();
                    navigationView.setCheckedItem(R.id.nav_home);
                });
        builder.create().show();


    }

    //replaces the container with fragments
    public void swapFragments(Fragment fragment) {
        android.support.v4.app.FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.container_frame, fragment);
        fragmentTransaction.commit();
    }

    //replaces the container with fragments and sends intent
    public void swapFragments(Fragment fragment, String id) {
        Bundle bundle = new Bundle();
        bundle.putString("theme", id);
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.container_frame, fragment);
        fragmentTransaction.addToBackStack("home").commit();
    }

    //gets user display name and dp
    public void getDisplayName() {
        View view = navigationView.getHeaderView(0);
        nav_profile_name = view.findViewById(R.id.nav_profile_name);
        nav_profile_pic = view.findViewById(R.id.nav_profile_pic);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            nav_profile_name.setText(name);
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .error(R.drawable.ic_verify)
                    .centerCrop()
                    .into(nav_profile_pic);

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

    //adds money to wallet
    public void addMoneyWallet(String uid) {

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
        Display display = getWindowManager().getDefaultDisplay();
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
        });

        doneFillup.setOnClickListener(v -> {
            if (refillAmount.getText().toString().isEmpty())
                refillAmount.setError("Enter a valid amount");
            else {
                int amount = Integer.parseInt(refillAmount.getText().toString());
                databaseReference.child("UserProfile").child(uid).child("wallet").setValue(amount + wallet)
                        .addOnSuccessListener(m -> {
                            popWindow.dismiss();
                            Toast.makeText(this, "Money Added Successfully", Toast.LENGTH_SHORT).show();
                        });
            }

        });

    }

    public void shareApp() {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! I have found a cool app download it now from PlayStore\n" + "http://bit.ly/bidkart");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        navigationView.setCheckedItem(R.id.nav_home);

    }

    public void contactUs() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        if (user != null) {
            uid = user.getUid();
        }
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"admin@fablogger.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Edit this to enter your Query Title");
        intent.putExtra(Intent.EXTRA_TEXT, "Enter your Query Here(Remove this line).\n\n\n\nDon't Remove this line.\nSent From: BidKart (ver: 1.0)\n (UID: " + uid + ")");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }


}
