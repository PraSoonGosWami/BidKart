package com.invaderx.firebasetrigger.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.invaderx.firebasetrigger.Models.Transactions;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private ArrayList<Transactions> transactionAdapterList;
    private Context context;


    public TransactionAdapter(ArrayList<Transactions> searchAdapterList, Context context) {
        this.transactionAdapterList = searchAdapterList;
        this.context = context;
    }


    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_tranascation, viewGroup, false);
        return new TransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TransactionAdapter.ViewHolder holder, int i) {
        final Transactions list = transactionAdapterList.get(i);

        if(list.getBidderuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.t_type.setText("Paid to");
            holder.t_amount.setTextColor(Color.RED);
            holder.t_wallet_type.setText("Debited from");
        }

        if(list.getSelleruid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.t_type.setText("Received from");
            holder.t_amount.setTextColor(Color.parseColor("#54b978"));
            holder.t_wallet_type.setText("Credited to");

        }

        holder.t_person.setText(list.getName());
        holder.t_product.setText(list.getProName());
        holder.t_amount.setText("₹"+list.getAmount());
        holder.t_id.setText("Transaction id: "+list.gettID());
        holder.t_date.setText(list.getDate());



    }

    @Override
    public int getItemCount() {
        return transactionAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView t_type, t_person, t_product, t_id, t_amount, t_date, t_wallet_type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            t_product = itemView.findViewById(R.id.t_product);
            t_person = itemView.findViewById(R.id.t_person);
            t_type = itemView.findViewById(R.id.t_type);
            t_id = itemView.findViewById(R.id.t_id);
            t_amount = itemView.findViewById(R.id.t_amount);
            t_date = itemView.findViewById(R.id.t_date);
            t_wallet_type = itemView.findViewById(R.id.t_wallet_type);

        }
    }
}

