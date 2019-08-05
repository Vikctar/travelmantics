package com.vikctar.vikcandroid.travelmantics;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DealRecyclerAdapter extends RecyclerView.Adapter<DealRecyclerAdapter.DealViewHolder> {
    ArrayList<TravelDeal> deals;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    public DealRecyclerAdapter() {
        FirebaseUtil.openFirebaseReference("traveldeals");

        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;

        deals = FirebaseUtil.deals;

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deal", travelDeal.getTitle());
                travelDeal.setId(dataSnapshot.getKey());
                deals.add(travelDeal);
                notifyItemInserted(deals.size() - 1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addChildEventListener(childEventListener);

    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);

        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal deal = deals.get(position);
        holder.tvTitle.setText(deal.getTitle());
        holder.tvDescription.setText(deal.getDescription());
        holder.tvPrice.setText(deal.getPrice());

    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    class DealViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPrice;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.text_title);
            tvDescription = itemView.findViewById(R.id.text_description);
            tvPrice = itemView.findViewById(R.id.text_price);
        }
    }
}
