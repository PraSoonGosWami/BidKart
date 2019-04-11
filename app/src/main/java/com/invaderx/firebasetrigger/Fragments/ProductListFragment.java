package com.invaderx.firebasetrigger.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Adapters.ProductListAdapter;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class ProductListFragment extends Fragment {

    private RecyclerView product_list_recycler_view;
    private ProgressBar product_list_progress_bar;
    private ArrayList<Products> productList = new ArrayList<>();
    private ProductListAdapter productListAdapter;
    private String catId;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private LinearLayout no_product_frame;
    private LottieAnimationView product_error;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        no_product_frame = view.findViewById(R.id.no_product_frame);
        product_error = view.findViewById(R.id.product_error);
        no_product_frame.setVisibility(View.GONE);
        product_error.cancelAnimation();


        //---------------Recycler View------------------------------------------
        product_list_recycler_view = view.findViewById(R.id.product_list_recycler_view);
        product_list_progress_bar = view.findViewById(R.id.product_list_progress_bar);
        productListAdapter = new ProductListAdapter(productList, getContext());
        product_list_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        product_list_recycler_view.setAdapter(productListAdapter);
        //-----------------------------------------------------------------------

        //firebase Database references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.keepSynced(true);

        //getting bundle category id
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            catId = bundle.getString("category");
            getProductList(catId);
        }
        getProductList(catId);

        product_list_recycler_view.setAdapter(productListAdapter);


        return view;
    }


    //fetches product list for given category
    public void getProductList(final String catId) {

        databaseReference.child("product").orderByChild("catId").equalTo(catId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Products products;
                        productList.clear();

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                products = data.getValue(Products.class);
                                if (products.getpStatus().equals("pending") || products.getpStatus().equals("done"))
                                    continue;
                                productList.add(new Products(products.getpId(), products.getpName(), products.getpCategory(),
                                        products.getpBid(), products.getBidderUID(), products.getProductListImgURL(), products.getSellerName(),
                                        products.getBasePrice(), products.getSellerUID(), products.getCatId(),
                                        products.getNoOfBids(), products.getSearchStr(), products.getExpTime(), products.getpDescription(),
                                        products.getpCondition(), products.getpStatus(), products.getExpDate()));
                            }
                        } else
                            showSnackbar("It's lonely here!!");

                        product_list_progress_bar.setVisibility(View.GONE);
                        productListAdapter.notifyDataSetChanged();
                        if (productList.size() == 0) {
                            no_product_frame.setVisibility(View.VISIBLE);
                            product_error.playAnimation();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //showSnackbar(databaseError.getMessage());

                    }
                });

    }


    //snackbar
    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


}
