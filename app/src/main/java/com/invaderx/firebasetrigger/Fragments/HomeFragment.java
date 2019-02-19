package com.invaderx.firebasetrigger.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Adapters.CategoryAdapter;
import com.invaderx.firebasetrigger.Adapters.ProductAdapter;
import com.invaderx.firebasetrigger.Models.Category;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView trending_recycler_view,category_recycler_view;
    private ArrayList<Products> TrendingList = new ArrayList<>();
    private ArrayList<Category> categoryList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressBar categoryProgressBar,trendingProductProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_home, container, false);

        categoryProgressBar = view.findViewById(R.id.categoryProgressBar);
        trendingProductProgressBar = view.findViewById(R.id.trendingProductProgressBar);


        //trending product recycler ------------------------------------
        trending_recycler_view = view.findViewById(R.id.trending_recycler_view);
        productAdapter= new ProductAdapter(TrendingList,getContext());

        trending_recycler_view.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, true));
        //--------------------------------------------------------------

        //category recycler----------------------------------------
        category_recycler_view = view.findViewById(R.id.category_recycler_view);
        categoryAdapter = new CategoryAdapter(categoryList,getContext());
        category_recycler_view.setLayoutManager(new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL));
        //-----------------------------------------------------------


        //firebase Database references
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();



        TrendingList.add(new Products("p1","Apple iPhone 7","52000",47000));
        TrendingList.add(new Products("p1","Apple iPhone 7","52000",47000));
        TrendingList.add(new Products("p1","Apple iPhone 7","52000",47000));
        TrendingList.add(new Products("p1","Apple iPhone 7","52000",47000));

        trending_recycler_view.setAdapter(productAdapter);

        trendingProductProgressBar.setVisibility(View.VISIBLE);
        categoryProgressBar.setVisibility(View.VISIBLE);

        //-----------------------------------------------
        getCategory();
        getTrendingProduct();
        //-----------------------------------------------

        return view;
    }

    //gets category
    public void getCategory(){
        databaseReference.child("category")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Category category = null;
                        if(dataSnapshot.exists()){
                            for(DataSnapshot data : dataSnapshot.getChildren()){
                                category = data.getValue(Category.class);
                                categoryList.add(new Category(category.getImgURL(),category.getCategoryId(),category.getCategoryName()));
                            }
                        }else
                            Toast.makeText(getContext(), "Something went wrong!\nTry agian in a bit", Toast.LENGTH_SHORT).show();

                        categoryProgressBar.setVisibility(View.GONE);
                        category_recycler_view.setAdapter(categoryAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    //gets trending product
    public void getTrendingProduct(){

        trendingProductProgressBar.setVisibility(View.GONE);

    }
}
