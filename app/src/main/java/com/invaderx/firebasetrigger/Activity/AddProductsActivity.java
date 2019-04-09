package com.invaderx.firebasetrigger.Activity;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
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


import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AddProductsActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    private FloatingActionButton done_pro_fab;
    private EditText sell_pName, sell_pPrice, sell_pDetails, sell_pExpTime;
    private Button upload_pImg;
    private CardView pImageCard;
    private ImageView sell_pImage;
    private Spinner cat_spinner, condition_spinner;
    private ArrayList<String> pCategoryList = new ArrayList<>();
    private ArrayList<String> pConditionList = new ArrayList<>();
    private String sell_pCategory, sell_pCondition, getSell_pCategoryId;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Uri downloadUri, uploadUri;
    private ProgressDialog progressDialog;
    private String uPid;
    private FirebaseUser user;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        getSupportActionBar().setElevation(0);

        progressDialog = new ProgressDialog(this);
        user = FirebaseAuth.getInstance().getCurrentUser();

        //binding views----------------------------------------------------------------
        done_pro_fab = findViewById(R.id.done_pro_fab);
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

        //database references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //storage reference
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        pImageCard.setVisibility(View.GONE);

        //managing spinners
        spinners();


        //click listener for upload image
        upload_pImg.setOnClickListener(v -> {
            imagePicker();
        });


        //click listener for add product FAB
        done_pro_fab.setOnClickListener(v -> {
            validiateAndUpload();

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

    //genares random secured pid
    public String generatePid() {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    //snackbar
    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    //adds product
    public void addProduct(String pid) {


        HashMap<String, Integer> map = new HashMap<>();
        map.put("no bids", Integer.parseInt(sell_pPrice.getText().toString()));
        int noOfDays = Integer.parseInt(sell_pExpTime.getText().toString());
        Products uProducts = new Products(
                pid,
                sell_pName.getText().toString(),
                sell_pCategory,
                map,
                "bidderUID",
                downloadUri.toString(),
                user.getDisplayName(),
                sell_pPrice.getText().toString(),
                user.getUid(),
                getSell_pCategoryId,
                0,
                sell_pName.getText().toString().toLowerCase(),
                noOfDays,
                sell_pDetails.getText().toString(),
                sell_pCondition,
                "pending", getDate(noOfDays));


        databaseReference.child("product").child(pid).setValue(uProducts)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    finish();
                    Toast.makeText(AddProductsActivity.this, "Your product has been Successfully added\nPlease wait for admin's approval", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showSnackbar(e.getMessage());

                });



    }

    //opens image picker dialog
    public void imagePicker() {
        BSImagePicker picker = new BSImagePicker.Builder("com.invaderx.firebasetrigger.fileprovider")
                .setMaximumDisplayingImages(24)
                .setSpanCount(3)
                .setGridSpacing(Utils.dp2px(2))
                .setPeekHeight(Utils.dp2px(360))
                .setTag("A request ID")
                .build();
        picker.show(getSupportFragmentManager(), "picker");
    }

    //returns file path
    @Override
    public void onSingleImageSelected(Uri uri, String tag) {

        pImageCard.setVisibility(View.VISIBLE);

        Glide.with(this).
                load(uri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        uploadUri = uri;
                        return false;
                    }
                })
                .centerCrop()
                .into(sell_pImage);
    }

    //manages spinner selection
    public void spinners() {
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
                        getSell_pCategoryId = "p001";
                        break;
                    case 2:
                        sell_pCategory = pCategoryList.get(2);
                        getSell_pCategoryId = "p002";
                        break;
                    case 3:
                        sell_pCategory = pCategoryList.get(3);
                        getSell_pCategoryId = "p003";
                        break;
                    case 4:
                        sell_pCategory = pCategoryList.get(4);
                        getSell_pCategoryId = "p004";
                        break;
                    case 5:
                        sell_pCategory = pCategoryList.get(5);
                        getSell_pCategoryId = "p005";
                        break;
                    case 6:
                        sell_pCategory = pCategoryList.get(6);
                        getSell_pCategoryId = "p006";
                        break;
                    case 7:
                        sell_pCategory = pCategoryList.get(7);
                        getSell_pCategoryId = "p007";
                        break;
                    default:
                        sell_pCategory = pCategoryList.get(0);
                        getSell_pCategoryId = "p007";
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
    }

    //uploads image to cloud and gets download URL
    public void validiateAndUpload() {
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

        if (Integer.parseInt(sell_pExpTime.getText().toString().trim()) > 5) {
            sell_pExpTime.setError("Days cannot be greater than 5");
            sell_pExpTime.requestFocus();
            return;
        }

        uPid = generatePid();

        if (uploadUri != null) {
            uploadImage(uploadUri);

        } else {
            showSnackbar("Something went wrong\nTry uploading image again");
        }

    }

    //uploads image and calls add product
    public void uploadImage(Uri link) {
        progressDialog.setTitle("Getting things ready");
        progressDialog.show();
        if (uPid.isEmpty())
            uPid = generatePid();
        storageReference.child(user.getUid()).child("p" + uPid)
                .putFile(link)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.setMessage("Getting things done...");
                    storageReference.child(user.getUid()).child("p" + uPid).getDownloadUrl().addOnSuccessListener(uri1 -> {
                        downloadUri = uri1;
                        if (downloadUri != null)
                            addProduct(uPid);
                        else
                            showSnackbar("Technical Issue\tTry again");

                    });

                })
                .addOnFailureListener(exception -> {
                    progressDialog.dismiss();
                    showSnackbar(exception.getMessage());

                })
                .addOnProgressListener(taskSnapshot -> {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                });
    }


    //
    public String getDate(int days) {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, days);
        dt = c.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        return formatter.format(dt);
    }

}
