package com.invaderx.firebasetrigger.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Adapters.ProductListAdapter;
import com.invaderx.firebasetrigger.Adapters.SearchAdapter;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private RecyclerView search_recycler;
    private ProgressBar search_progressBar;
    private ArrayList<Products> searchList = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private String catId;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EditText action_bar_search;
    private LinearLayout search_error_frame;
    private LottieAnimationView search_error;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_search, container, false);


        //---------------Recycler View------------------------------------------
        search_recycler = view.findViewById(R.id.search_recycler);
        search_progressBar = view.findViewById(R.id.search_progressBar);
        searchAdapter = new SearchAdapter(searchList, getContext());
        search_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        search_recycler.setAdapter(searchAdapter);
        //-----------------------------------------------------------------------


        search_progressBar.setVisibility(View.INVISIBLE);
        //firebase Database references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        //lottie view for error in search
        search_error = view.findViewById(R.id.search_error);
        search_error_frame = view.findViewById(R.id.search_error_frame);
        search_error_frame.setVisibility(View.GONE);


        //getting the search bar view from custom_appbar layout
        View v = ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();
        action_bar_search = v.findViewById(R.id.action_bar_search);
        //action_bar_search.setText("c");
        action_bar_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_progressBar.setVisibility(View.VISIBLE);
                getSearchItems(action_bar_search.getText().toString().toLowerCase());


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        search_recycler.setAdapter(searchAdapter);

        return view;
    }


    public void getSearchItems(final String search) {

        if (!search.isEmpty()) {
            databaseReference.child("product").orderByChild("searchStr").startAt(search).endAt(search + "\uf8ff")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Products products;
                            searchList.clear();
                            if (dataSnapshot.exists()) {
                                search_error_frame.setVisibility(View.GONE);
                                search_error.cancelAnimation();
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    products = data.getValue(Products.class);
                                    if (products.getpStatus().equals("pending") || products.getpStatus().equals("done")) {
                                        continue;
                                    }
                                    searchList.add(new Products(products.getpId(), products.getpName(), products.getpCategory(),
                                            products.getpBid(), products.getBidderUID(), products.getProductListImgURL(), products.getSellerName(),
                                            products.getBasePrice(), products.getSellerUID(),
                                            products.getCatId(), products.getNoOfBids(), products.getSearchStr(), products.getExpTime(), products.getpDescription(),
                                            products.getpCondition(), products.getpStatus(), products.getExpDate()));
                                }
                            } else {
                                search_error_frame.setVisibility(View.VISIBLE);
                                search_error.playAnimation();
                            }

                            searchAdapter.notifyDataSetChanged();
                            search_progressBar.setVisibility(View.INVISIBLE);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            searchList.clear();
            search_progressBar.setVisibility(View.INVISIBLE);
            search_error_frame.setVisibility(View.INVISIBLE);
            search_error.cancelAnimation();
        }
    }

}
