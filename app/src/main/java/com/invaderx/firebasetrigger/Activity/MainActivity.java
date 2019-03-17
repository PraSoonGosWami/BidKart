package com.invaderx.firebasetrigger.Activity;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
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
import com.invaderx.firebasetrigger.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity {

    private ImageButton action_bar_menu;
    private ImageView action_bar_appicon;
    private ImageButton action_bar_notification;
    private EditText action_bar_search;
    private TextView action_bar_title;
    private DrawerLayout drawerLayout;
    private TextView nav_profile_name;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.nav_sell:
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    //startActivity(new Intent(this,SellerActivity.class));
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
        action_bar_notification = view.findViewById(R.id.action_bar_notification);
        action_bar_search = view.findViewById(R.id.action_bar_search);
        action_bar_title = view.findViewById(R.id.action_bar_title);
        //------------------------------------------------------------

        //Bottom Nav Bar----------------------------------------------
        final BottomBar bottomBar =findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(tabId -> {

            switch (tabId) {
                case R.id.bottom_home:
                    action_bar_appicon.setVisibility(View.VISIBLE);
                    action_bar_notification.setVisibility(View.VISIBLE);
                    action_bar_search.setVisibility(View.GONE);
                    action_bar_title.setVisibility(View.GONE);
                    swapFragments(new HomeFragment());
                    break;
                case R.id.bottom_search:
                    action_bar_appicon.setVisibility(View.GONE);
                    action_bar_notification.setVisibility(View.GONE);
                    action_bar_search.setVisibility(View.VISIBLE);
                    action_bar_title.setVisibility(View.GONE);
                    swapFragments(new SearchFragment());
                    break;

                case R.id.bottom_notification:
                    action_bar_appicon.setVisibility(View.GONE);
                    action_bar_notification.setVisibility(View.GONE);
                    action_bar_search.setVisibility(View.GONE);
                    action_bar_title.setVisibility(View.VISIBLE);
                    action_bar_title.setText("Notifications");
                    swapFragments(new NotificationFragment());
                    break;

                case R.id.bottom_profile:
                    action_bar_appicon.setVisibility(View.GONE);
                    action_bar_notification.setVisibility(View.GONE);
                    action_bar_search.setVisibility(View.GONE);
                    action_bar_title.setVisibility(View.VISIBLE);
                    action_bar_title.setText("Profile");
                    swapFragments(new ProfileFragment());
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

        //show splash screen
        showSplashScreen();

    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawers();
        else if (getFragmentManager().getBackStackEntryCount() != 0) {
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
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), UserLogin.class));
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
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

    //shows popup splash screen
    public void showSplashScreen() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View v = factory.inflate(R.layout.splash_screen, null);
        LottieAnimationView lottieAnimationView = v.findViewById(R.id.splash_anim);

        final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.splash_screen);
        dialog.setCancelable(true);
        dialog.show();
        lottieAnimationView.playAnimation();

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dialog.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
       /*
        final Handler handler = new Handler();
        final Runnable runnable = () ->
                dialog.dismiss();
        handler.postDelayed(runnable, 4000);*/
    }


}
