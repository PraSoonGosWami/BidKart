package com.invaderx.firebasetrigger.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

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

        Glide.with(context).
                load(list.getProductListImgURL())
                .into(holder.product_list_image);
        holder.product_list_title.setText(list.getpName());
        holder.product_list_base_price.setText("Base Price : ₹" + list.getBasePrice());
        holder.product_list_seller_name.setText("Seller : " + list.getSellerName());
        holder.product_list_current_bid.setText("Current Bid : ₹" + list.getpBid());
        holder.product_list_cat.setText("in " + list.getpCategory());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Product ID:" + list.getpId(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public int getItemCount() {
        return productListAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView product_list_image;
        public TextView product_list_title, product_list_base_price, product_list_seller_name, product_list_current_bid, product_list_cat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product_list_image = itemView.findViewById(R.id.product_list_image);
            product_list_title = itemView.findViewById(R.id.product_list_title);
            product_list_base_price = itemView.findViewById(R.id.product_list_base_price);
            product_list_seller_name = itemView.findViewById(R.id.product_list_seller_name);
            product_list_current_bid = itemView.findViewById(R.id.product_list_current_bid);
            product_list_cat = itemView.findViewById(R.id.product_list_cat);

        }
    }


}

