package com.invaderx.firebasetrigger.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.fabtransitionactivity.SheetLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.invaderx.firebasetrigger.Auth.UserLogin;
import com.invaderx.firebasetrigger.Fragments.OnSaleFragment;
import com.invaderx.firebasetrigger.Fragments.PendingFragment;
import com.invaderx.firebasetrigger.Fragments.SoldFragment;
import com.invaderx.firebasetrigger.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class SellerActivity extends AppCompatActivity implements SheetLayout.OnFabAnimationEndListener {

    private static int REQUEST_CODE = 101;
    private DrawerLayout drawerLayout;
    private TextView nav_profile_name;
    private NavigationView navigationView;
    private FloatingActionButton add_pro_fab;
    private SheetLayout sheetLayout;
    private String uToken = "logged out";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);
        Toolbar toolbar = findViewById(R.id.toolbar_white);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black);
        actionbar.setElevation(0);
        actionbar.setTitle("Seller");

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        //binding views-------------------------------------------
        drawerLayout = findViewById(R.id.drawer_layout_white);
        navigationView = findViewById(R.id.nav_view_white);
        add_pro_fab = findViewById(R.id.add_pro_fab);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        ViewPager viewPager = findViewById(R.id.viewpager);
        sheetLayout = findViewById(R.id.bottom_sheet);
        //-------------------------------------------------------------------


        //database references-------------------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        navigationView.setCheckedItem(R.id.nav_sell);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    return true;
                case R.id.nav_logout:
                    logout();
                    return true;

                case R.id.nav_share:
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! I have found a cool app download it now from PlayStore\n" + "http://bit.ly/bidkart");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    return true;

                case R.id.nav_contactUs:
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    if (user != null) {
                        uid = user.getUid();
                    }
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"admin@fablogger.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Edit this to enter your Query Title");
                    intent.putExtra(Intent.EXTRA_TEXT, "Enter your Query Here(Remove this line).\n\n\n\nDon't Remove this line.\nSent From: BidKart (ver: 1.0)\n UID: " + uid);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    return true;

            }
            return false;
        });
        sheetLayout.setFab(add_pro_fab);
        sheetLayout.setFabAnimationEndListener(this);
        add_pro_fab.setOnClickListener(v -> sheetLayout.expandFab());


        //sets user name to side nav drawer
        getDisplayName();

        //TabLayout Initialization
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("On-Sale", OnSaleFragment.class)
                .add("Pending", PendingFragment.class)
                .add("Sold", SoldFragment.class)
                .create());

        //setting view pager adapter
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawers();
        else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setMessage("Are you sure you want exit?")
                    .setPositiveButton("Yes", (dialog, id) -> finish())
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
            builder.create().show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(Gravity.START))
                    drawerLayout.closeDrawers();
                else
                    drawerLayout.openDrawer(Gravity.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //performs logout
    public void logout() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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

    //gets user display name
    public void getDisplayName() {
        View view = navigationView.getHeaderView(0);
        nav_profile_name = view.findViewById(R.id.nav_profile_name_white);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            Log.v("Username", name);
            // getImage(user.getUid(),imageView);

            nav_profile_name.setText(name);
        } else {
            nav_profile_name.setText("No user name");
        }
    }


    //animates fab for reveal layout
    @Override
    public void onFabAnimationEnd() {
        startActivityForResult(new Intent(this, AddProductsActivity.class), REQUEST_CODE);
    }

    //ontracts fab if closed
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            sheetLayout.contractFab();
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
