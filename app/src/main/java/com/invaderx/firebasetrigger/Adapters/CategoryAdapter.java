package com.invaderx.firebasetrigger.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
                .inflate(R.layout.category_model,viewGroup,false);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryAdapter.ViewHolder holder, int i) {
        final Category list = categoryAdapterList.get(i);
        Glide.with(context).
                load(list.getImgURL())
                .into(holder.category_image);
        holder.category_title.setText(list.getCategoryName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Category Id: "+list.getCategoryId(), Toast.LENGTH_SHORT).show();
            }
        });


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
}
