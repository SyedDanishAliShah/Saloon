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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientOrderAdapter extends RecyclerView.Adapter < ClientOrderAdapter.viewHolder > {

    String TAG = "clientOrderAdapter";
    private final ArrayList < Order > arrayLists;
    private final Context context;
    String adminId, keys, userId;
    private DatabaseReference databaseReference;

    public ClientOrderAdapter( ArrayList < Order > arrayLists , Context context , String adminId ) {
        this.arrayLists = arrayLists;
        this.context = context;
        this.adminId = adminId;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {
        View view = LayoutInflater.from( context ).inflate( R.layout.client_order_recycler_item , parent , false );
        return new viewHolder( view );
    }

    @Override
    public void onBindViewHolder( @NonNull viewHolder holder , int position ) {
        Order model = arrayLists.get( position );
        holder.clientName.setText( model.getName() );
        holder.timeSlot.setText( model.getTime() );
        holder.date.setText( model.getDate() );
        holder.category.setText( model.getCategory() );
//        holder.key.setText( model.getUser_key() );

//        Log.e( "posittionnnn" , position + "" );

//        keys = model.getKey();
//        keys = model.getName();

//        keys = arrayLists.get( position ).getKey();

//        keys = holder.key.getText().toString();

        databaseReference = FirebaseDatabase.getInstance().getReference();

//        holder.itemView.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick( View view ) {
//                Toast.makeText( context , keys , Toast.LENGTH_SHORT ).show();
//                Log.e( TAG , "list data : " + arrayLists );
//                Log.e( TAG , "keys : " + keys );
//
////                databaseReference.child( "Order" ).child( adminId ).child( "key" ).addValueEventListener( new ValueEventListener() {
////                    @Override
////                    public void onDataChange( @NonNull DataSnapshot snapshot ) {
////                        for ( DataSnapshot dataSnapshot : snapshot.getChildren() ) {
////                            Order order = dataSnapshot.getValue( Order.class );
////                            Log.e( TAG , "snapshot keys : " + order );
////                            Toast.makeText( context , order.toString() , Toast.LENGTH_SHORT ).show();
////                        }
////                    }
////
////                    @Override
////                    public void onCancelled( @NonNull DatabaseError error ) {
////
////                    }
////                } );
//            }
//        } );

        holder.btnCompleted.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
//                keys = arrayLists.get( holder.getAdapterPosition() ).getPush_key();
                userId = arrayLists.get( holder.getAdapterPosition() ).getUser_id();

//                Log.e( TAG , "position : " + holder.getAdapterPosition() );
//                Log.e( TAG , "keys : " + keys );

//                Toast.makeText( context , "position : " + holder.getAdapterPosition() , Toast.LENGTH_SHORT ).show();
//                Toast.makeText( context , "keys : " + keys , Toast.LENGTH_LONG ).show();

//                HashMap < String, Object > hashMap = new HashMap <>();
//                hashMap.put( "completed" , true );
//                databaseReference.child( "Order" ).child( adminId ).child( keys ).updateChildren( hashMap );

//                Firebase.setAndroidContext( context );
//                Firebase ref = new Firebase("https://saloon-8dbd2-default-rtdb.firebaseio.com/");

                HashMap < String, Object > historyInsertion = new HashMap <>();
                historyInsertion.put( "name" , arrayLists.get( holder.getAdapterPosition() ).getName() );
                historyInsertion.put( "category" , arrayLists.get( holder.getAdapterPosition() ).getCategory() );
                historyInsertion.put( "time" , arrayLists.get( holder.getAdapterPosition() ).getTime() );
                historyInsertion.put( "date" , arrayLists.get( holder.getAdapterPosition() ).getDate() );

                String pushKeys = databaseReference.push().getKey();
                Log.e( TAG , "push keyssss  : " + pushKeys );
                Log.e( TAG , "admin idsss  : " + adminId );
                Log.e( TAG , "user idsss  : " + userId );

                databaseReference.child( "History" ).child( adminId ).child( pushKeys ).setValue( historyInsertion );   // Insertion of orders in history table
                databaseReference.child( "Order" ).child( adminId ).child( userId ).removeValue();    // Deletion of orders from order table

                Toast.makeText( context , databaseReference.child( "Order" ).child( adminId ).child( userId ).toString() , Toast.LENGTH_LONG ).show();

//                HashMap<String, Object> orderDeletion = new HashMap <>();
//                orderDeletion.remove( "", keys );

//                Map fanOut = new HashMap();
//                fanOut.put( "History/" + adminId + "/" + keys + "/", historyInsertion );
//                fanOut.put( "Order" + adminId + "/" , orderDeletion);
//                ref.updateChildren( fanOut );
            }
        } );
    }

    @Override
    public int getItemCount() {
        return arrayLists.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        TextView clientName, timeSlot, date, category, key;
        Button btnCompleted;

        public viewHolder( @NonNull View itemView ) {
            super( itemView );
            clientName = itemView.findViewById( R.id.clientName );
            timeSlot = itemView.findViewById( R.id.clientTimeSlot );
            date = itemView.findViewById( R.id.clientDate );
            category = itemView.findViewById( R.id.clientCategory );
            key = itemView.findViewById( R.id.clientKey );
            btnCompleted = itemView.findViewById( R.id.btnCompleted );
        }
    }

}
