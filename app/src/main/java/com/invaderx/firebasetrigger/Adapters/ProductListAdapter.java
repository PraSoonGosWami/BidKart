package com.invaderx.firebasetrigger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Activity.ProductPageActivity;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    private ArrayList<Products> productListAdapterList;
    private Context context;


    public ProductListAdapter(ArrayList<Products> productListAdapterList, Context context) {
        this.productListAdapterList = productListAdapterList;
        this.context = context;
    }


    @NonNull
    @Override
    public ProductListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_product_listl, viewGroup, false);
        return new ProductListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListAdapter.ViewHolder holder, int i) {
        final Products list = productListAdapterList.get(i);
        holder.sold_watermark.setVisibility(View.GONE);
        Glide.with(context).
                load(list.getProductListImgURL())
                .into(holder.product_list_image);
        holder.product_list_title.setText(list.getpName());
        holder.product_list_current_bid.setText("Current Bid : ₹" + Collections.max(list.getpBid().values()));
        holder.product_list_cat.setText("in " + list.getpCategory());


        if (list.getpStatus().equals("done") || list.getpStatus().equals("sold"))
            holder.sold_watermark.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(v -> {
            if (!list.getpStatus().equals("done")) {
                Intent intent = new Intent(context, ProductPageActivity.class);
                intent.putExtra("pid", list.getpId());
                context.startActivity(intent);
            }
        });



        if (list.getpStatus().equals("sold") || list.getpStatus().equals("pending")) {
            holder.expDays.setVisibility(View.GONE);
        } else {
            holder.expDays.setVisibility(View.VISIBLE);
            holder.expDays.setText(list.getExpDate());

        }





    }


    @Override
    public int getItemCount() {
        return productListAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView product_list_image, sold_watermark;
        public TextView product_list_title, product_list_current_bid, product_list_cat, expDays;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product_list_image = itemView.findViewById(R.id.product_list_image);
            product_list_title = itemView.findViewById(R.id.product_list_title);
            product_list_current_bid = itemView.findViewById(R.id.product_list_current_bid);
            product_list_cat = itemView.findViewById(R.id.product_list_cat);
            expDays = itemView.findViewById(R.id.expDay);
            sold_watermark = itemView.findViewById(R.id.sold_watermark);


        }
    }





}

