package com.example.saloon.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.saloon.Constants;
import com.example.saloon.Fragments.DatePickerFragment;
import com.example.saloon.Fragments.TimePickerFragment;
import com.example.saloon.Models.Order;
import com.example.saloon.Models.SaloonModel;
import com.example.saloon.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    String TAG = "PlaceOrder";
    AutoCompleteTextView categorySpinner;
    TextInputLayout textInputTime, textInputDate;
    TextInputEditText timeText, dateText;
    Button btnRequest;
    LinearLayout placeOrderLayout, pendingPostLayout;
    TextView requestProcess, clientName, clientTimeSlot, clientDate, clientCategory;
    String[] maleCategory, femaleCategory;
    DatabaseReference databaseReference;
    String name, gender, userId, adminSharedId, setTime, setDate;
    ArrayAdapter < String > arrayAdapter;
    int validation = 0;
    boolean splashBool;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_place_order );

        // Casting
        categorySpinner = findViewById( R.id.categorySpinner );
        textInputTime = findViewById( R.id.textInputTime );
        timeText = findViewById( R.id.timeText );
        textInputDate = findViewById( R.id.textInputDate );
        dateText = findViewById( R.id.dateText );
        btnRequest = findViewById( R.id.btnRequest );
        requestProcess = findViewById( R.id.requestProcess );
        clientName = findViewById( R.id.clientName );
        clientTimeSlot = findViewById( R.id.clientTimeSlot );
        clientDate = findViewById( R.id.clientDate );
        clientCategory = findViewById( R.id.clientCategory );
        placeOrderLayout = findViewById( R.id.placeOrderLayout );
        pendingPostLayout = findViewById( R.id.pendingPostLayout );

        databaseReference = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getSharedPreferences( Constants.myPrefs , Context.MODE_PRIVATE );
        adminSharedId = sharedPreferences.getString( Constants.adminIDShared , "" );
        splashBool = sharedPreferences.getBoolean( Constants.splashBool , false );
        userId = sharedPreferences.getString( Constants.userIDShared , "aa" );

        Log.e( TAG , "adminSharedId 111 : " + adminSharedId );

//        Intent intent = getIntent();
//        adminId = intent.getStringExtra( "adminId" );
//        userId = intent.getStringExtra( "userId" );
//        Log.e( TAG , "Admin id 111 : : " + adminId );
        Log.e( TAG , "User id 111 : : " + userId );

        databaseReference.child( "Admin" ).child( adminSharedId ).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                SaloonModel saloonModel = snapshot.getValue( SaloonModel.class );
                maleCategory = saloonModel.getMale_services().split( "_" );
                femaleCategory = saloonModel.getFemale_services().split( "_" );

            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {

            }
        } );


        if ( !splashBool ) {
            databaseReference.child( "Users" ).child( userId ).addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot snapshot ) {
                    gender = snapshot.getValue( SaloonModel.class ).getGender();
                    name = snapshot.getValue( SaloonModel.class ).getName();

                    if ( gender.toLowerCase().equals( "male" ) ) {
                        arrayAdapter = new ArrayAdapter( PlaceOrderActivity.this , R.layout.spinner_dropdown_item , maleCategory );
                    } else {
                        arrayAdapter = new ArrayAdapter( PlaceOrderActivity.this , R.layout.spinner_dropdown_item , femaleCategory );
                    }

                    arrayAdapter.setDropDownViewResource( R.layout.spinner_dropdown_item );

                    // Setting the ArrayAdapter data on the Spinner
                    categorySpinner.setAdapter( arrayAdapter );
                    showAcceptOrder();

                }

                @Override
                public void onCancelled( @NonNull DatabaseError error ) {

                }
            } );
        }

        categorySpinner.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView < ? > adapterView , View view , int i , long l ) {
                validation = 2;
            }
        } );

        timeText.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show( getSupportFragmentManager() , "time picker" );
                validation = 2;
            }
        } );

        dateText.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show( getSupportFragmentManager() , "date picker" );
                validation = 2;
            }
        } );


        if ( splashBool ) {
            showAcceptOrder();
            placeOrderLayout.setVisibility( View.GONE );
        }


        btnRequest.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                placeOrder();
            }
        } );


    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu , menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        if ( item.getItemId() == R.id.logout ) {
//            FirebaseAuth.getInstance().signOut();
            AuthUI.getInstance()
                    .signOut( this )
                    .addOnCompleteListener( new OnCompleteListener < Void >() {
                        public void onComplete( @NonNull Task < Void > task ) {
                            // user is now signed out
                            startActivity( new Intent( PlaceOrderActivity.this , LoginUserActivity.class ) );
                            finish();
                            Toast.makeText( PlaceOrderActivity.this , "Logout success" , Toast.LENGTH_SHORT ).show();
                        }
                    } );
            return true;
        } else {
            return super.onOptionsItemSelected( item );
        }

    }

    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
        menu.findItem( R.id.adminLoginItem ).setVisible( false );
        MenuItem historyItem = menu.findItem( R.id.historyItem );
        historyItem.setVisible( false );
        menu.findItem( R.id.loginMenuIcon ).setIcon( ContextCompat.getDrawable( this , R.drawable.ic_logout ) );
        return super.onPrepareOptionsMenu( menu );
    }

    private void showAcceptOrder() {

        databaseReference.child( "Order" ).child( adminSharedId ).child( userId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {

                if ( snapshot.exists() ) {

                    Order order = snapshot.getValue( Order.class );

                    boolean accept = order.isAccept();

                    if ( accept ) {
//                        SharedPreferences.Editor editor = getSharedPreferences( "mShared" , Context.MODE_PRIVATE ).edit();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean( "splashBool" , true );
                        editor.apply();
                        clientName.setText( order.getName() + " your oder details is" );
                        clientTimeSlot.setText( "TIME:" + order.getTime() );
                        clientDate.setText( "DATE: " + order.getDate() );
                        clientCategory.setText( "CATAGORY:  " + order.getCategory() );

                        requestProcess.setVisibility( View.GONE );
                        pendingPostLayout.setVisibility( View.VISIBLE );
                        placeOrderLayout.setVisibility( View.GONE );
                    } else {
                        requestProcess.setVisibility( View.VISIBLE );
                        placeOrderLayout.setVisibility( View.GONE );
                        pendingPostLayout.setVisibility( View.GONE );
                    }

//                    placeOrderLayout.setVisibility( View.GONE );

                } else {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean( Constants.splashBool , false );
                    editor.apply();

                    placeOrderLayout.setVisibility( View.VISIBLE );
                    requestProcess.setVisibility( View.GONE );
                    pendingPostLayout.setVisibility( View.GONE );
                }

            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {
                Toast.makeText( PlaceOrderActivity.this , error.getMessage() , Toast.LENGTH_SHORT ).show();

            }
        } );
    }

    private void placeOrder() {

//                Log.e( TAG , "adminId : " + adminId );
//                Toast.makeText( PlaceOrderActivity.this , adminId , Toast.LENGTH_SHORT ).show();

        Log.e( TAG , "Admin id : " + adminSharedId );
        Log.e( TAG , "Spinner item : " + categorySpinner.getText().toString() );

//                String key = databaseReference.child( adminId ).child(  ).getKey();

        if ( categorySpinner.getText().equals( "Category" ) || categorySpinner.getText().length() == 0
                || timeText.getText().equals( "Time" ) || timeText.getText().length() == 0
                || dateText.getText().equals( "Date" ) || dateText.getText().length() == 0 ) {
            Toast.makeText( PlaceOrderActivity.this , "Please fill the fields..." , Toast.LENGTH_SHORT ).show();
            pendingPostLayout.setVisibility( View.GONE );
        } else {

//                    if ( validation == 2 ) {

            if ( adminSharedId != null ) {
                Map < String, Object > map = new HashMap <>();
                map.put( "name" , name );
                map.put( "category" , categorySpinner.getText().toString() );
                map.put( "time" , setTime );
                map.put( "date" , setDate );
                map.put( "accept" , false );
                map.put( "user_id" , userId );
//                        map.put( "completed" , false );

                // Generate Random doubles
//                        double randNum = ThreadLocalRandom.current().nextDouble();
//                        map.put( "key" , String.valueOf( randNum ) );

//                        Log.e( TAG , "ranKey   : " + randNum );
//
//                        String stringNum = String.valueOf( randNum );

//                        String pushKey = databaseReference.push().getKey();
//                        map.put( "push_key" , pushKey );

//                        databaseReference.child( "Order" ).child( adminId ).push().setValue( map );
//                        assert pushKey != null;
//                        databaseReference.child( "Order" ).child( adminId ).child( pushKey ).setValue( map );

//                        databaseReference.child( "Order" ).child( adminId ).child( uId ).setValue( map );

                databaseReference.child( "Order" ).child( adminSharedId ).child( userId ).setValue( map );

                placeOrderLayout.setVisibility( View.GONE );

            } else {
                Toast.makeText( PlaceOrderActivity.this , "Something went wrong..." , Toast.LENGTH_SHORT ).show();
            }

//                    } else {
//                        Toast.makeText( PlaceOrderActivity.this , "Please fill the fields" , Toast.LENGTH_SHORT ).show();
//                    }

        }

//                setRating();

//                showAcceptOrder();

    }

//    private void setRating() {
//        Dialog dialog = new Dialog( this );
//        dialog.setContentView( R.layout.rating_dialog );
//        dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT );
//
//        // Casting
//        RatingBar ratingBar = dialog.findViewById( R.id.ratingBar );
//        Button submitRating = dialog.findViewById( R.id.btnRateSubmit );
//
//        ratingBar.setOnRatingBarChangeListener( new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged( RatingBar ratingBar , float rating , boolean b ) {
//                TextView textView = dialog.findViewById( R.id.tvRateDialog );
//                textView.setText( "Rate us : " + rating );
//            }
//        } );
//
//        dialog.show();
//
//        submitRating.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick( View view ) {
//                dialog.dismiss();
//            }
//        } );
//    }

    @Override
    public void onTimeSet( TimePicker timePicker , int hour , int minute ) {

        Time time = new Time( hour , minute , 0 );

        //little h uses 12 hour format and big H uses 24 hour format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "h:mm a" );

        //format takes in a Date, and Time is a subclass of Date
        setTime = simpleDateFormat.format( time );
        timeText.setText( setTime );
    }

    @Override
    public void onDateSet( DatePicker datePicker , int year , int month , int day ) {
        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR , year );
        calendar.set( Calendar.MONTH , month );
        calendar.set( Calendar.DAY_OF_MONTH , day );

        setDate = DateFormat.getDateInstance( DateFormat.FULL ).format( calendar.getTime() );
        dateText.setText( setDate );
    }
}