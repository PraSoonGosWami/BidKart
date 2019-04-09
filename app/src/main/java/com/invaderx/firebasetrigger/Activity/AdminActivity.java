package com.invaderx.firebasetrigger.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.invaderx.firebasetrigger.Auth.UserLogin;
import com.invaderx.firebasetrigger.Fragments.AdminAllProductsFragment;
import com.invaderx.firebasetrigger.Fragments.AdminPendingFragment;
import com.invaderx.firebasetrigger.Fragments.OnSaleFragment;
import com.invaderx.firebasetrigger.Fragments.PendingFragment;
import com.invaderx.firebasetrigger.Fragments.SoldFragment;
import com.invaderx.firebasetrigger.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setTitle("Admin");
        getSupportActionBar().setElevation(0);

        SmartTabLayout viewPagerTab = findViewById(R.id.admin_tab);
        ViewPager viewPager = findViewById(R.id.viewpager_admin);

        //TabLayout Initialization
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Pending Products", AdminPendingFragment.class)
                .add("All Products", AdminAllProductsFragment.class)

                .create());

        //setting view pager adapter
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);


    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage("Are you sure you want exit?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    startActivity(new Intent(this, UserLogin.class));
                    FirebaseAuth.getInstance().signOut();
                    finish();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }


}
