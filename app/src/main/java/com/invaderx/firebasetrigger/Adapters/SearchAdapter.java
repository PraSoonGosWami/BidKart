package com.invaderx.firebasetrigger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.invaderx.firebasetrigger.Activity.ProductPageActivity;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private ArrayList<Products> searchAdapterList;
    private Context context;


    public SearchAdapter(ArrayList<Products> searchAdapterList, Context context) {
        this.searchAdapterList = searchAdapterList;
        this.context = context;
    }


    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_search, viewGroup, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAdapter.ViewHolder holder, int i) {
        final Products list = searchAdapterList.get(i);
        Glide.with(context).
                load(list.getProductListImgURL())
                .into(holder.search_img);
        holder.search_title.setText(list.getpName());
        holder.search_category.setText("in " + list.getpCategory());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductPageActivity.class);
            intent.putExtra("pid", list.getpId());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return searchAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView search_img;
        public TextView search_title, search_category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            search_img = itemView.findViewById(R.id.search_img);
            search_title = itemView.findViewById(R.id.search_title);
            search_category = itemView.findViewById(R.id.search_category);
        }
    }
}
