package com.example.saloon.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.saloon.Adapters.ClientOrderAdapter;
import com.example.saloon.Adapters.HistoryAdapter;
import com.example.saloon.Models.Order;
import com.example.saloon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList< Order > arrayList;
    HistoryAdapter historyAdapter;
    DatabaseReference databaseReference;
    String adminId;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_history );

        recyclerView = findViewById( R.id.historyRecyclerView );

        adminId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        arrayList = new ArrayList <>();

        historyAdapter = new HistoryAdapter( arrayList , this , adminId );
        recyclerView.setAdapter( historyAdapter );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        databaseReference.child( "History" ).child( adminId ).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                for ( DataSnapshot dataSnapshot : snapshot.getChildren() ) {
                    if(dataSnapshot.exists()) {
                        Order order = dataSnapshot.getValue(Order.class);
                        arrayList.add( order );
                    }
                }
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {

            }
        } );

    }
}