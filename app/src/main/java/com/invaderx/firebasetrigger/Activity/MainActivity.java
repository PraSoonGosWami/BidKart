package com.invaderx.firebasetrigger.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Auth.UserLogin;
import com.invaderx.firebasetrigger.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity {

    private ImageButton action_bar_menu;
    private ImageView action_bar_appicon;
    private ImageButton action_bar_notification;
    private EditText action_bar_search;
    private FrameLayout container_frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        container_frame = findViewById(R.id.container_frame);

        //Action Bar custom View-------------------------------------
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_appbar);

        View view= getSupportActionBar().getCustomView();
        action_bar_menu = view.findViewById(R.id.action_bar_menu);
        action_bar_appicon = view.findViewById(R.id.action_bar_appicon);
        action_bar_notification = view.findViewById(R.id.action_bar_notification);
        action_bar_search = view.findViewById(R.id.action_bar_search);
        //------------------------------------------------------------

        //Bottom Nav Bar----------------------------------------------
        BottomBar bottomBar =findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {

                switch (tabId){
                    case R.id.bottom_home:
                        action_bar_appicon.setVisibility(View.VISIBLE);
                        action_bar_notification.setVisibility(View.VISIBLE);
                        action_bar_search.setVisibility(View.GONE);
                        break;
                    case R.id.bottom_search:
                        action_bar_appicon.setVisibility(View.GONE);
                        action_bar_notification.setVisibility(View.GONE);
                        action_bar_search.setVisibility(View.VISIBLE);
                        break;
                }


            }
        });
        //---------------------------------------------------------------


    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, UserLogin.class));
    }


}
