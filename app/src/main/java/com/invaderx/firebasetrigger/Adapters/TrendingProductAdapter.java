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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.invaderx.firebasetrigger.Activity.MainActivity;
import com.invaderx.firebasetrigger.Activity.ProductPageActivity;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class TrendingProductAdapter extends RecyclerView.Adapter<TrendingProductAdapter.ViewHolder> {
    private ArrayList<Products> trendingProductAdapterList;
    private Context context;


    public TrendingProductAdapter(ArrayList<Products> trendingProductAdapterList, Context context) {
        this.trendingProductAdapterList = trendingProductAdapterList;
        this.context = context;
    }


    @NonNull
    @Override
    public TrendingProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_trending, viewGroup, false);
        return new TrendingProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingProductAdapter.ViewHolder holder, int i) {
        final Products list = trendingProductAdapterList.get(i);

        Glide.with(context).
                load(list.getProductListImgURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
                .override(500, 500)
                .into(holder.trending_product_image);
        holder.trending_product_title.setText(list.getpName());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductPageActivity.class);
            intent.putExtra("pid", list.getpId());
            context.startActivity(intent);
        });


    }


    @Override
    public int getItemCount() {
        return trendingProductAdapterList.size();
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
