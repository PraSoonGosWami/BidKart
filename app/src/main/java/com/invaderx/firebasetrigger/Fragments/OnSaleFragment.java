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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Adapters.ProductListAdapter;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class OnSaleFragment extends Fragment {

    private RecyclerView on_sale_rView;
    private ArrayList<Products> productList = new ArrayList<>();
    private ProductListAdapter productListAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private TextView sale_error;
    private String uid;

    public OnSaleFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_sale, container, false);

        //---------------Recycler View------------------------------------------
        on_sale_rView = view.findViewById(R.id.on_sale_rView);
        productListAdapter = new ProductListAdapter(productList, getContext());
        on_sale_rView.setLayoutManager(new LinearLayoutManager(getContext()));
        on_sale_rView.setAdapter(productListAdapter);
        sale_error = view.findViewById(R.id.sale_error);
        sale_error.setVisibility(View.GONE);
        //-----------------------------------------------------------------------

        //firebase Database references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        getProductList(uid);

        on_sale_rView.setAdapter(productListAdapter);

        return view;
    }

    //fetches product list for given category
    public void getProductList(String uid) {

        databaseReference.child("product").orderByChild("sellerUID").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Products products;
                        productList.clear();

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                products = data.getValue(Products.class);
                                if (products.getpStatus().equals("live")) {
                                    productList.add(new Products(products.getpId(), products.getpName(), products.getpCategory(),
                                            products.getpBid(), products.getBidderUID(), products.getProductListImgURL(), products.getSellerName(),
                                            products.getBasePrice(), products.getSellerUID(), products.getCatId(),
                                            products.getNoOfBids(), products.getSearchStr(), products.getExpTime(), products.getpDescription(),
                                            products.getpCondition(), products.getpStatus(), products.getExpDate()));
                                }
                            }
                        } else
                            showSnackbar("No products found");

                        productListAdapter.notifyDataSetChanged();
                        if (productList.size() == 0) {
                            sale_error.setVisibility(View.VISIBLE);
                        } else
                            sale_error.setVisibility(View.GONE);
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
