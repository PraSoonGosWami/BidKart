package com.invaderx.firebasetrigger.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.invaderx.firebasetrigger.Fragments.ProductListFragment;
import com.invaderx.firebasetrigger.Models.Category;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<Category> categoryAdapterList;
    private Context context;



    public CategoryAdapter(ArrayList<Category> categoryAdapterList, Context context) {
        this.categoryAdapterList = categoryAdapterList;
        this.context = context;
    }


    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_category, viewGroup, false);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryAdapter.ViewHolder holder, int i) {
        final Category list = categoryAdapterList.get(i);
        Glide.with(context).
                load(list.getImgURL())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .dontTransform()
                .override(600, 600)
                .centerCrop()
                .error(R.drawable.ic_verify)
                .into(holder.category_image);
        holder.category_title.setText(list.getCategoryName());

        holder.itemView.setOnClickListener(v -> swapFragments(new ProductListFragment(), list.getCategoryId()));


    }

    @Override
    public int getItemCount() {
        return categoryAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView category_image;
        public TextView category_title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category_image=itemView.findViewById(R.id.category_image);
            category_title=itemView.findViewById(R.id.category_title);
        }
    }


    //replaces the container with fragments and sends category id
    public void swapFragments(Fragment fragment, String id) {
        Bundle bundle = new Bundle();
        bundle.putString("category", id);
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction;
        fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.container_frame, fragment);
        fragmentTransaction.addToBackStack("home").commit();
    }
}
