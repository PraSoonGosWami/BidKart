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
    private ArrayList<String> time = new ArrayList<>();


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
        holder.product_list_current_bid.setText("Current Bid : ₹" + Collections.max(list.getpBid().values()));
        holder.product_list_cat.setText("in " + list.getpCategory());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductPageActivity.class);
            intent.putExtra("pid", list.getpId());
            context.startActivity(intent);
        });

        time.clear();
        expiryTime(list.getExpTime());

        if (!list.getpStatus().equals("sold")) {
            holder.time_layout.setVisibility(View.VISIBLE);
            holder.expDays.setText(time.get(0) + " :\nDay");
            holder.expHrs.setText(time.get(1) + " :\nHrs");
            holder.expMin.setText(time.get(2) + " :\nMin");
            holder.expSec.setText(time.get(3) + "\nSec");
        } else
            holder.time_layout.setVisibility(View.GONE);




    }


    @Override
    public int getItemCount() {
        return productListAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView product_list_image;
        public LinearLayout time_layout;
        public TextView product_list_title, product_list_current_bid, product_list_cat, expDays, expHrs, expMin, expSec;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product_list_image = itemView.findViewById(R.id.product_list_image);
            product_list_title = itemView.findViewById(R.id.product_list_title);
            product_list_current_bid = itemView.findViewById(R.id.product_list_current_bid);
            product_list_cat = itemView.findViewById(R.id.product_list_cat);
            expDays = itemView.findViewById(R.id.expDay);
            expHrs = itemView.findViewById(R.id.expHrs);
            expMin = itemView.findViewById(R.id.expMin);
            expSec = itemView.findViewById(R.id.expSec);
            time_layout = itemView.findViewById(R.id.time_layout);

        }
    }


    public void expiryTime(long milliseconds) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);

        String d = String.valueOf(days);
        String h = String.valueOf(hours);
        String m = String.valueOf(minutes);
        String s = String.valueOf(seconds);

        if (d.length() == 1)
            d = "0" + d;
        if (h.length() == 1)
            h = "0" + h;
        if (m.length() == 1)
            m = "0" + m;
        if (s.length() == 1)
            s = "0" + s;

        time.add(d);
        time.add(h);
        time.add(m);
        time.add(s);

    }


}

