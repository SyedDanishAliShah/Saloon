package com.example.saloon.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saloon.Models.Order;
import com.example.saloon.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter < HistoryAdapter.viewHolder > {

    final ArrayList < Order > arrayLists;
    final Context context;
    String adminId;

    public HistoryAdapter( ArrayList < Order > arrayLists , Context context , String adminId ) {
        this.arrayLists = arrayLists;
        this.context = context;
        this.adminId = adminId;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {
        View view = LayoutInflater.from( context ).inflate( R.layout.history_recycler_item , parent , false );
        return new viewHolder( view );
    }

    @Override
    public void onBindViewHolder( @NonNull viewHolder holder , int position ) {
        Order model = arrayLists.get( position );
        holder.clientName.setText("Name : " + model.getName() );
        holder.timeSlot.setText("Time : " + model.getTime() );
        holder.date.setText("Date : " + model.getDate() );
        holder.category.setText("Category : " + model.getCategory() );

    }

    @Override
    public int getItemCount() {
        return arrayLists.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        TextView clientName, timeSlot, date, category;

        public viewHolder( @NonNull View itemView ) {
            super( itemView );
            clientName = itemView.findViewById( R.id.clientName );
            timeSlot = itemView.findViewById( R.id.clientTimeSlot );
            date = itemView.findViewById( R.id.clientDate );
            category = itemView.findViewById( R.id.clientCategory );
        }
    }
}
