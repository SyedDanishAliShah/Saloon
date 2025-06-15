package com.example.saloon.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saloon.Models.Order;
import com.example.saloon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientOrderDialogAdapter extends RecyclerView.Adapter < ClientOrderDialogAdapter.viewHolder > {

    String TAG = "dialog";
    private final Context context;
    private final ArrayList < Order > arrayLists;
    private DatabaseReference databaseReference;
    String adminId, userId;
    List arralylistKey;
    List < Order > keyList;

    public ClientOrderDialogAdapter( Context context , ArrayList < Order > arrayLists , String adminId , List arralylistKey ) {
        this.context = context;
        this.arrayLists = arrayLists;
        this.adminId = adminId;
        this.arralylistKey = arralylistKey;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {
        View view = LayoutInflater.from( context ).inflate( R.layout.client_order_dialog_recycler_item , parent , false );
        return new viewHolder( view );
    }

    @Override
    public void onBindViewHolder( @NonNull viewHolder holder , int position ) {
        Order model = arrayLists.get( position );
        holder.clientName.setText( model.getName() );
        holder.timeSlot.setText( model.getTime() );
        holder.date.setText( model.getDate() );
        holder.category.setText( model.getCategory() );

//        userId = arrayLists.get( holder.getAdapterPosition() ).getUser_id();

//        keys = model.getUser_key();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child( "Order" ).child( adminId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                for ( DataSnapshot dataSnapshot : snapshot.getChildren() ) {
                    if ( dataSnapshot.exists() ) {
                        Order order = dataSnapshot.getValue( Order.class );

                        Log.e( TAG , "dialog snap data : " + dataSnapshot );

                        keyList = new ArrayList <>();
                        keyList.add( order );

                        Log.e( TAG , "list data : " + keyList );

                    }
                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {

            }
        } );

        holder.btnAccepted.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                HashMap < String, Object > hashMap = new HashMap();
                hashMap.put( "accept" , true );

//                databaseReference.child( "Order" ).child( adminId ).child( arralylistKey.get(holder.getAdapterPosition() ).toString() ).updateChildren( hashMap );

//                keys = arrayLists.get( holder.getAdapterPosition() ).getPush_key();
                userId = arrayLists.get( holder.getAdapterPosition() ).getUser_id();

//                databaseReference.child( "Order" ).child( adminId ).child( keys ).updateChildren( hashMap );
                databaseReference.child( "Order" ).child( adminId ).child( userId ).updateChildren( hashMap );
            }
        } );

        holder.btnDecline.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
//                databaseReference.child( "Order" ).child( adminId ).child( keys ).removeValue();
                userId = arrayLists.get( holder.getAdapterPosition() ).getUser_id();
                databaseReference.child( "Order" ).child( adminId ).child( userId ).removeValue();
                Toast.makeText( context , "Order declined" , Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    @Override
    public int getItemCount() {
        return arrayLists.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        TextView clientName, timeSlot, date, category;
        Button btnDecline, btnAccepted;

        public viewHolder( @NonNull View itemView ) {
            super( itemView );
            clientName = itemView.findViewById( R.id.clientNameDialog );
            timeSlot = itemView.findViewById( R.id.clientTimeSlotDialog );
            date = itemView.findViewById( R.id.clientDateDialog );
            category = itemView.findViewById( R.id.clientCategoryDialog );
            btnDecline = itemView.findViewById( R.id.btnDeclineDialog );
            btnAccepted = itemView.findViewById( R.id.btnAcceptedDialog );
        }
    }

}
