package com.invaderx.firebasetrigger.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Adapters.TransactionAdapter;
import com.invaderx.firebasetrigger.Models.Transactions;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class TranscationFragment extends Fragment {

    private RecyclerView tran_rView;
    private ArrayList<Transactions> tranList = new ArrayList<>();
    private TransactionAdapter tranListAdapter;
    private FirebaseDatabase firebaseDatabase;
    private RelativeLayout t_layout;
    private DatabaseReference databaseReference;
    private TextView tran_error;
    private String uid;
    private String theme;

    public TranscationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);


        //getting bundle  theme
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            theme = bundle.getString("theme");
        }


        //---------------Recycler View------------------------------------------
        tran_rView = view.findViewById(R.id.tran_rView);
        t_layout = view.findViewById(R.id.t_layout);
        tranListAdapter = new TransactionAdapter(tranList, getContext());
        tran_rView.setLayoutManager(new LinearLayoutManager(getContext()));
        tran_rView.setAdapter(tranListAdapter);
        tran_error = view.findViewById(R.id.tran_error);
        tran_error.setVisibility(View.GONE);
        //-----------------------------------------------------------------------

        //firebase Database references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        if (theme.equals("dark")) {
            t_layout.setBackgroundColor(Color.parseColor("#2A2D36"));
            tran_error.setTextColor(Color.WHITE);
        }
        if (theme.equals("light")) {
            t_layout.setBackgroundColor(Color.WHITE);
            tran_error.setTextColor(Color.DKGRAY);
        }

        getAllTransactions(uid);

        tran_rView.setAdapter(tranListAdapter);

        return view;
    }

    //fetches product list for given category
    public void getAllTransactions(String uid) {

        databaseReference.child("Transactions").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Transactions transactions;
                        tranList.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                transactions = data.getValue(Transactions.class);
                                tranList.add(new Transactions(transactions.getSelleruid(), transactions.getBidderuid(), transactions.getName(),
                                        transactions.getAmount(), transactions.getProName(), transactions.gettID(), transactions.getDate()));

                            }
                        } else
                            showSnackbar("No Transactions found");

                        tranListAdapter.notifyDataSetChanged();
                        if (tranList.size() == 0) {

                            tran_error.setVisibility(View.VISIBLE);
                        } else
                            tran_error.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showSnackbar(databaseError.getMessage());

                    }
                });

    }

    //snackbar
    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
