package com.invaderx.firebasetrigger.Activity;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class AddProductsActivity extends AppCompatActivity {

    private FloatingActionButton done_pro_fab;
    private EditText sell_pImgUrl, sell_pName, sell_pPrice, sell_pDetails, sell_pExpTime;
    private Button upload_pImg;
    private CardView pImageCard;
    private ImageView sell_pImage;
    private Spinner cat_spinner, condition_spinner;
    private ArrayList<String> pCategoryList = new ArrayList<>();
    private ArrayList<String> pConditionList = new ArrayList<>();
    private String sell_pCategory, sell_pCondition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        getSupportActionBar().setElevation(0);

        //binding views----------------------------------------------------------------
        done_pro_fab = findViewById(R.id.done_pro_fab);
        sell_pImgUrl = findViewById(R.id.sell_pImgUrl);
        sell_pName = findViewById(R.id.sell_pName);
        sell_pPrice = findViewById(R.id.sell_pPrice);
        sell_pDetails = findViewById(R.id.sell_pDetails);
        sell_pExpTime = findViewById(R.id.sell_pExpTime);
        upload_pImg = findViewById(R.id.upload_pImg);
        pImageCard = findViewById(R.id.pImageCard);
        sell_pImage = findViewById(R.id.sell_pImage);
        cat_spinner = findViewById(R.id.cat_spinner);
        condition_spinner = findViewById(R.id.condition_spinner);
        //---------------------------------------------------------------------------
        pCategoryList.add("---Select Category---");
        pCategoryList.add("Phone & Accessories");
        pCategoryList.add("Computer & Accessories");
        pCategoryList.add("Home & Appliances");
        pCategoryList.add("Software & Gaming");
        pCategoryList.add("Fashion & Clothing");
        pCategoryList.add("Books & Knowledge");
        pCategoryList.add("Others");

        pConditionList.add("---Select Condition---");
        pConditionList.add("Excellent");
        pConditionList.add("Good");
        pConditionList.add("Fair");
        pConditionList.add("Vintage");
        pConditionList.add("Working");

        ArrayAdapter catAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pCategoryList);
        ArrayAdapter conAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pConditionList);

        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cat_spinner.setAdapter(catAdapter);
        condition_spinner.setAdapter(conAdapter);
        cat_spinner.setSelection(0);
        condition_spinner.setSelection(0);


        cat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        sell_pCategory = pCategoryList.get(1);
                        break;
                    case 2:
                        sell_pCategory = pCategoryList.get(2);
                        break;
                    case 3:
                        sell_pCategory = pCategoryList.get(3);
                        break;
                    case 4:
                        sell_pCategory = pCategoryList.get(4);
                        break;
                    case 5:
                        sell_pCategory = pCategoryList.get(5);
                        break;
                    case 6:
                        sell_pCategory = pCategoryList.get(6);
                        break;
                    case 7:
                        sell_pCategory = pCategoryList.get(7);
                        break;
                    default:
                        sell_pCategory = pCategoryList.get(0);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showSnackbar("Please select a Category");
            }
        });

        condition_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        sell_pCondition = pConditionList.get(1);
                        break;
                    case 2:
                        sell_pCondition = pConditionList.get(2);
                        break;
                    case 3:
                        sell_pCondition = pConditionList.get(3);
                        break;
                    case 4:
                        sell_pCondition = pConditionList.get(4);
                        break;
                    case 5:
                        sell_pCondition = pConditionList.get(5);
                        break;
                    default:
                        sell_pCondition = pConditionList.get(0);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showSnackbar("Please select product Condition");
            }
        });





        done_pro_fab.setOnClickListener(v -> {
            addProduct();

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //snackbar
    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    //adds product
    public void addProduct() {

        if (sell_pName.getText().toString().trim().isEmpty()) {
            sell_pName.setError("Enter product name");
            sell_pName.requestFocus();
            return;
        }
        if (sell_pPrice.getText().toString().trim().isEmpty()) {
            sell_pPrice.setError("Enter product base price");
            sell_pPrice.requestFocus();
            return;
        }
        if (sell_pDetails.getText().toString().trim().isEmpty()) {
            sell_pDetails.setError("Enter product details");
            sell_pDetails.requestFocus();
            return;
        }
        if (sell_pDetails.getText().toString().trim().length() < 15) {
            sell_pDetails.setError("Product details too short");
            sell_pDetails.requestFocus();
            return;
        }

        if (cat_spinner.getSelectedItemPosition() == 0) {
            showSnackbar("Please select a Category");
            return;
        }
        if (condition_spinner.getSelectedItemPosition() == 0) {
            showSnackbar("Please select product condition");
            return;
        }

        if (sell_pExpTime.getText().toString().trim().isEmpty()) {
            sell_pExpTime.setError("Enter product expiry time");
            sell_pExpTime.requestFocus();
            return;
        }

        if (Integer.parseInt(sell_pExpTime.getText().toString().trim()) > 4) {
            sell_pExpTime.setError("Days cannot be greater than 4");
            sell_pExpTime.requestFocus();
            return;
        } else
            finish();


    }


}
