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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_home, container, false);
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




        TrendingList.add(new Products("p1","Apple iPhone 7","52000",47000));
        TrendingList.add(new Products("p1","Apple iPhone 7","52000",47000));
        TrendingList.add(new Products("p1","Apple iPhone 7","52000",47000));
        TrendingList.add(new Products("p1","Apple iPhone 7","52000",47000));

        trending_recycler_view.setAdapter(productAdapter);


        categoryList.add(new Category("http","c1","Electronics"));
        categoryList.add(new Category("http","c1","Electronics"));
        categoryList.add(new Category("http","c1","Electronics"));
        categoryList.add(new Category("http","c1","Electronics"));
        categoryList.add(new Category("http","c1","Electronics"));

        category_recycler_view.setAdapter(categoryAdapter);



        return view;
    }
}
