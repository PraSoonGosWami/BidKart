package com.invaderx.firebasetrigger.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.invaderx.firebasetrigger.R;

public class AddProductsActivity extends AppCompatActivity {

    private FloatingActionButton done_pro_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        done_pro_fab = findViewById(R.id.done_pro_fab);

        done_pro_fab.setOnClickListener(v -> {
            finish();
        });
    }


}
