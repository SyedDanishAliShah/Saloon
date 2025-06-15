package com.example.saloon.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saloon.Adapters.ClientOrderAdapter;
import com.example.saloon.Adapters.ClientOrderDialogAdapter;
import com.example.saloon.Constants;
import com.example.saloon.Models.Order;
import com.example.saloon.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClientOrderActivity extends AppCompatActivity {

    String TAG = "ClientOrder";
    RecyclerView recyclerView, dialogRecyclerView;
    TextView tvNoData, tvNoDataDialog;
    private ArrayList < Order > arrayList, arrayList1;
    public List arrayListKey;
    public static Dialog dialog;
    ClientOrderAdapter clientOrderAdapter;
    ClientOrderDialogAdapter clientOrderDialogAdapter;
    DatabaseReference databaseReference;
    String adminId;
    MediaPlayer mediaPlayer;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_client_order );

//        Intent intent = getIntent();
//        adminId = intent.getStringExtra( "adminID" );
//        Log.e( TAG , "admin id : " + adminId );

        sharedPreferences = getSharedPreferences( Constants.myPrefs , Context.MODE_PRIVATE );
        adminId = sharedPreferences.getString( Constants.adminIDShared , "" );

        databaseReference = FirebaseDatabase.getInstance().getReference();

        arrayList = new ArrayList <>();
        arrayListKey = new ArrayList <>();

        setDialog();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu , menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
        if ( item.getItemId() == R.id.historyItem ) {
            Intent intent = new Intent( ClientOrderActivity.this , HistoryActivity.class );
            intent.putExtra( "adminId" , adminId );
            startActivity( intent );
            return true;
        } else if ( item.getItemId() == R.id.logout ) {
//            FirebaseAuth.getInstance().signOut();
            AuthUI.getInstance()
                    .signOut( this )
                    .addOnCompleteListener( new OnCompleteListener < Void >() {
                        public void onComplete( @NonNull Task < Void > task ) {
                            // user is now signed out
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean( Constants.adminCheck, false );
                            editor.apply();

                            startActivity( new Intent( ClientOrderActivity.this , LoginAdminActivity.class ) );
                            finish();
                            Toast.makeText( ClientOrderActivity.this , "Logout success" , Toast.LENGTH_SHORT ).show();
                        }
                    } );
            return true;
        } else {
            return super.onOptionsItemSelected( item );
        }
    }

    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
        MenuItem adminLoginItem = menu.findItem( R.id.adminLoginItem );
        adminLoginItem.setVisible( false );
        return super.onPrepareOptionsMenu( menu );
    }

    private void setDialog() {
        // Create dialog
        dialog = new Dialog( this );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        dialog.setCancelable( false );
        dialog.setContentView( R.layout.client_order_dialog );
        dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT );

        // Cast dialog views
        dialogRecyclerView = dialog.findViewById( R.id.dialogRecycler );
        tvNoDataDialog = dialog.findViewById( R.id.tvNoDataDialog );
        Button btnOk = dialog.findViewById( R.id.dialogBtnOk );

        // Setting a beep sound when dialog open
        mediaPlayer = MediaPlayer.create( ClientOrderActivity.this , R.raw.beep_sound );

        databaseReference.child( "Order" ).child( adminId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                if ( snapshot.exists() ) {

                    arrayList.clear();
//                SaloonModel saloonModel = snapshot.getValue( SaloonModel.class );
                    Log.e( TAG , "data 11 : " + snapshot );
                    for ( DataSnapshot dataSnapshot : snapshot.getChildren() ) {
                        if ( dataSnapshot.exists() ) {
                            Order order = dataSnapshot.getValue( Order.class );

                            boolean accept = order.isAccept();

                            if ( !accept ) {
                                arrayList.add( order );
                            }

//                        arrayListKey.add( snapshot.getKey() );

                            Log.e( TAG , "list data : " + arrayList );

                            // Create adapter object and set adapter for dialog in recyclerview
                            clientOrderDialogAdapter = new ClientOrderDialogAdapter( ClientOrderActivity.this , arrayList , adminId , arrayListKey );
                            dialogRecyclerView.setAdapter( clientOrderDialogAdapter );

                            // Set layout to recycler view
                            dialogRecyclerView.setLayoutManager( new LinearLayoutManager( getApplicationContext() , LinearLayoutManager.VERTICAL , false ) );
                            clientOrderDialogAdapter.notifyDataSetChanged();
                            dialogRecyclerView.setVisibility( View.VISIBLE );
                            tvNoDataDialog.setVisibility( View.GONE );
                            dialog.show();

                            // Start beep sound
                            mediaPlayer.start();
                        }
//                    else if ( dataSnapshot.getKey().isEmpty() ) {
//                        tvNoDataDialog.setVisibility( View.VISIBLE );
//                        dialogRecyclerView.setVisibility( View.GONE );
//                    }

                    }

                } else {
                    dialogRecyclerView.setVisibility( View.GONE );
                    tvNoDataDialog.setVisibility( View.VISIBLE );
//                    Toast.makeText( ClientOrderActivity.this , "There are no orders in dialog" , Toast.LENGTH_SHORT ).show();
                    Log.e( TAG , "There are no orders in dialog..." );
                }

            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {
                Toast.makeText( ClientOrderActivity.this , error.getMessage() , Toast.LENGTH_SHORT ).show();
            }
        } );

        btnOk.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                dialog.dismiss();
            }
        } );

        // Setting a beep sound when dialog open
//        final MediaPlayer mediaPlayer = MediaPlayer.create( ClientOrderActivity.this , R.raw.beep_sound );
//        mediaPlayer.start();

        // Show dialog
        dialog.show();

        // Start beep sound
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOrders();
    }

    private void setOrders() {
        arrayList1 = new ArrayList <>();
        recyclerView = findViewById( R.id.clientOrderRecycler );
        tvNoData = findViewById( R.id.tvNoData );
        databaseReference.child( "Order" ).child( adminId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                arrayList1.clear();
//                SaloonModel saloonModel = snapshot.getValue( SaloonModel.class );
                if ( snapshot.exists() ) {

                    for ( DataSnapshot dataSnapshot : snapshot.getChildren() ) {

                        if ( dataSnapshot.exists() ) {

                            Order order = dataSnapshot.getValue( Order.class );

                            boolean accept = order.isAccept();

//                        boolean completed = order.isCompleted();

//                        if ( accept && !completed ) {
//                            arrayList1.add( order );
//                        }

                            if ( accept ) {
                                arrayList1.add( order );
                            }

                            clientOrderAdapter = new ClientOrderAdapter( arrayList1 , ClientOrderActivity.this , adminId );
                            recyclerView.setAdapter( clientOrderAdapter );
                            recyclerView.setLayoutManager( new LinearLayoutManager( ClientOrderActivity.this ) );
                            recyclerView.setVisibility( View.VISIBLE );
                            tvNoData.setVisibility( View.GONE );

                            clientOrderAdapter.notifyDataSetChanged();

                        }

                    }

                } else {
                    recyclerView.setVisibility( View.GONE );
                    tvNoData.setVisibility( View.VISIBLE );
//                    Toast.makeText( ClientOrderActivity.this , "There are no orders" , Toast.LENGTH_SHORT ).show();
                    Log.e( TAG , "There are no orders..." );
                }

            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {
                Toast.makeText( ClientOrderActivity.this , error.getMessage() , Toast.LENGTH_SHORT ).show();
            }
        } );
    }
}