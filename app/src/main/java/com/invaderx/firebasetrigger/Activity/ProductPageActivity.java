package com.invaderx.firebasetrigger.Activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ProductPageActivity extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private String proId;
    private AppBarLayout appBarLayout;
    private NestedScrollView nestedScrollView;
    private ImageView pro_image, pro_seller_image;
    private TextView pro_title, pro_currentbid, pro_expDay,
            pro_expHrs, pro_expMin, pro_expSec, pro_category,
            pro_condition, pro_bidsNo, pro_base_price, pro_description, pro_seller;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button place_bid;
    private ArrayList<String> time = new ArrayList<>();
    private LottieAnimationView pro_error_anim, pro_loading;
    private LinearLayout pro_error_frame;
    private PopupWindow popWindow;
    private CoordinatorLayout product_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), "AppBarTransition");
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        proId = getIntent().getStringExtra("pid");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //enables back button------------------------------------
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);


        //app bar layout----------------------------------------------------------------------------
        nestedScrollView = findViewById(R.id.scroll);
        appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (i == 0)
                    nestedScrollView.setBackgroundResource(R.drawable.plane_white);
                else
                    nestedScrollView.setBackgroundResource(R.drawable.slideup_background);
            }
        });

        //firebase Database references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        //------- Getting views-------------------------
        pro_image = findViewById(R.id.pro_image);
        pro_title = findViewById(R.id.pro_title);
        pro_currentbid = findViewById(R.id.pro_currentbid);
        pro_expDay = findViewById(R.id.pro_expDay);
        pro_expHrs = findViewById(R.id.pro_expHrs);
        pro_expMin = findViewById(R.id.pro_expMin);
        pro_expSec = findViewById(R.id.pro_expSec);
        pro_category = findViewById(R.id.pro_category);
        pro_condition = findViewById(R.id.pro_condition);
        pro_bidsNo = findViewById(R.id.pro_bidsCount);
        pro_base_price = findViewById(R.id.pro_base_price);
        pro_description = findViewById(R.id.pro_description);
        pro_seller_image = findViewById(R.id.pro_seller_image);
        pro_seller = findViewById(R.id.pro_seller);
        place_bid = findViewById(R.id.place_bid);
        pro_error_frame = findViewById(R.id.pro_error_frame);
        pro_error_anim = findViewById(R.id.pro_error);
        pro_loading = findViewById(R.id.pro_loading);
        product_container = findViewById(R.id.product_container);
        //-------------------------------------------------------------------


        pro_error_frame.setVisibility(View.GONE);
        pro_error_anim.cancelAnimation();
        appBarLayout.setVisibility(View.INVISIBLE);
        nestedScrollView.setVisibility(View.INVISIBLE);
        place_bid.setVisibility(View.INVISIBLE);
        pro_loading.setVisibility(View.VISIBLE);

        //getting product page
        getProducts();

        product_container.setAlpha(1f);
        place_bid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product_container.setAlpha(0.4f);
                bidPopup();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            super.onBackPressed(); //replaced
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parentIntent = NavUtils.getParentActivityIntent(this);
                if (parentIntent == null) {
                    finish();
                    return true;
                } else {
                    parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(parentIntent);
                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }


    public void getProducts() {

        databaseReference.child("product").orderByChild("pId").equalTo(proId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Products products = null;
                        pro_loading.setVisibility(View.INVISIBLE);
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                products = data.getValue(Products.class);
                            }
                            Glide.with(getApplicationContext()).
                                    load(products.getProductListImgURL())
                                    .into(pro_image);
                            pro_title.setText(products.getpName());
                            pro_currentbid.setText("₹" + products.getpBid());
                            pro_category.setText("Category: " + products.getpCategory());
                            pro_condition.setText("Condition: Good");
                            pro_base_price.setText("Base Price: ₹" + products.getBasePrice());
                            pro_bidsNo.setText("Total Bids: " + products.getNoOfBids());
                            pro_description.setText(products.getpDescription());
                            pro_seller.setText("BidKart");

                            expiryTime(products.getExpTime());

                            pro_expDay.setText(time.get(0) + " Days :");
                            pro_expHrs.setText(time.get(1) + " Hrs :");
                            pro_expMin.setText(time.get(2) + " Min :");
                            pro_expSec.setText(time.get(3) + " Sec");
                            collapsingToolbarLayout.setTitle(products.getpName());

                            place_bid.setVisibility(View.VISIBLE);
                            appBarLayout.setVisibility(View.VISIBLE);
                            nestedScrollView.setVisibility(View.VISIBLE);

                        } else {
                            pro_error_frame.setVisibility(View.VISIBLE);
                            pro_error_anim.playAnimation();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProductPageActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void expiryTime(long milliseconds) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);

        String d = String.valueOf(days);
        String h = String.valueOf(hours);
        String m = String.valueOf(minutes);
        String s = String.valueOf(seconds);

        if (d.length() == 1)
            d = "0" + d;
        if (h.length() == 1)
            h = "0" + h;
        if (m.length() == 1)
            m = "0" + m;
        if (s.length() == 1)
            s = "0" + s;

        time.clear();
        time.add(d);
        time.add(h);
        time.add(m);
        time.add(s);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void bidPopup() {


        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflatedView = layoutInflater.inflate(R.layout.payment_popup, null, false);
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
        popWindow.setFocusable(true);
        popWindow.setElevation(10f);
        popWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(inflatedView, Gravity.CENTER, 0, 100);

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                product_container.setAlpha(1f);
            }
        });
    }

}
