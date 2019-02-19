package com.invaderx.firebasetrigger.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<Products> productAdapterList;
    private Context context;


    public ProductAdapter(ArrayList<Products> trendingProductAdapterList, Context context) {
        this.productAdapterList = trendingProductAdapterList;
        this.context = context;
    }


    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.trending_model,viewGroup,false);
        return new ProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int i) {
        Products list = productAdapterList.get(i);

       // holder.trending_product_image.setRe
        holder.trending_product_title.setText(list.getpName());


    }


    @Override
    public int getItemCount() {
        return productAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView trending_product_image;
        public TextView  trending_product_title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trending_product_image=itemView.findViewById(R.id.trending_product_image);
            trending_product_title=itemView.findViewById(R.id.trending_product_title);
        }
    }
}
