package com.example.saloon.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saloon.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SignupAdminActivity extends AppCompatActivity {

    TextInputEditText adminNameEdit, adminEmailEdit, adminPasswordEdit;
    CheckBox maleHairstyle, maleBeardStyle, maleFacial, maleTatoo,
            maleMoustacheStyle, femaleHairstyle, femaleFacial, femaleTatoo, femaleEyebrows;
    TextView adminAddress, adminLogin;
    Button btnAdminSignup;
    boolean valid = false;
    private FirebaseAuth auth;
    DatabaseReference databaseReference;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location location;
    String id, latitude, longitude, strAdminAddress;
    Map < String, Object > adminData;
    String maleServices = "", femaleServices = "";
    String TAG = "AdminSignup";

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin_signup );

        adminNameEdit = findViewById( R.id.adminNameEdit );
        adminEmailEdit = findViewById( R.id.adminEmailEdit );
        adminPasswordEdit = findViewById( R.id.adminPasswordEdit );
        maleHairstyle = findViewById( R.id.maleHairstyle );
        maleBeardStyle = findViewById( R.id.maleBeardStyle );
        maleFacial = findViewById( R.id.maleFacial );
        maleTatoo = findViewById( R.id.maleTatoo );
        maleMoustacheStyle = findViewById( R.id.maleMoustacheStyle );
        femaleHairstyle = findViewById( R.id.femaleHairstyle );
        femaleFacial = findViewById( R.id.femaleFacial );
        femaleTatoo = findViewById( R.id.femaleTatoo );
        femaleEyebrows = findViewById( R.id.femaleEyebrows );
        adminAddress = findViewById( R.id.adminAddress );
        adminLogin = findViewById( R.id.adminLogin );
        btnAdminSignup = findViewById( R.id.btnAdminSignup );

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );


        // Check permission
        if ( ActivityCompat.checkSelfPermission( SignupAdminActivity.this , Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            // If permission is granted do this
            getUserLocation();
        } else {
            // If permission is denied then do this
            ActivityCompat.requestPermissions( SignupAdminActivity.this , new String[] {Manifest.permission.ACCESS_FINE_LOCATION} , 50 );
        }

        btnAdminSignup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                checkFieldValidation( adminNameEdit );
                checkFieldValidation( adminEmailEdit );
                checkFieldValidation( adminPasswordEdit );

                if ( valid ) {
                    signupAdmin();
                }

//                signupAdmin();
            }
        } );

        adminLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startActivity( new Intent( SignupAdminActivity.this , LoginAdminActivity.class ) );
            }
        } );
    }

    private void getUserLocation() {
        if ( ActivityCompat.checkSelfPermission( this , Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this , Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener( new OnCompleteListener < Location >() {
            @Override
            public void onComplete( @NonNull Task < Location > task ) {
                // Initialize location
                location = task.getResult();

                if ( location != null ) {

                    try {
                        // Initialize GeoCoder
                        Geocoder geocoder = new Geocoder( SignupAdminActivity.this , Locale.getDefault() );

                        // Initialize address list
                        List < Address > addressList = geocoder.getFromLocation( location.getLatitude() , location.getLongitude() , 1 );

                        // Setting latitude and longitude on TextView
//                        latitude.setText( String.valueOf( addressList.get( 0 ).getLatitude() ) );
//                        longitude.setText( String.valueOf( addressList.get( 0 ).getLongitude() ) );
                        latitude = String.valueOf( addressList.get( 0 ).getLatitude() );
                        longitude = String.valueOf( addressList.get( 0 ).getLongitude() );
                        strAdminAddress = addressList.get( 0 ).getAddressLine( 0 );
                        adminAddress.setText( "Lat : " + latitude + " \nLong : " + longitude + " \n " + strAdminAddress );
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }

//                    latitude.setText( String.valueOf( location.getLatitude() ) );
//                    longitude.setText( String.valueOf( location.getLongitude() ) );

                }
            }
        } );
    }

    private boolean checkFieldValidation( TextInputEditText text ) {

        if ( text.getText().toString().length() == 0 ) {
            text.setError( "Field can't be empty!" );
            valid = false;
        } else if ( text.getText().toString().isEmpty() ) {
            text.setError( "Error" );
            valid = false;
        } else {
            valid = true;
        }

        return valid;
    }

    private void signupAdmin() {
        // Admin registration process
        auth.createUserWithEmailAndPassword( adminEmailEdit.getText().toString() , adminPasswordEdit.getText().toString() )
                .addOnCompleteListener( this , new OnCompleteListener < AuthResult >() {
                    @Override
                    public void onComplete( @NonNull Task < AuthResult > task ) {
                        if ( task.isSuccessful() ) {
                            Toast.makeText( SignupAdminActivity.this , "Sign up success!" , Toast.LENGTH_SHORT ).show();

                            id = task.getResult().getUser().getUid();

                            // Storing admin side male services values into realtime database
                            if ( maleHairstyle.isChecked() ) {
                                maleServices += maleHairstyle.getText().toString();
                            }

                            if ( maleBeardStyle.isChecked() ) {
                                maleServices += "_" + maleBeardStyle.getText().toString();
                            }

                            if ( maleFacial.isChecked() ) {
                                maleServices += "_" + maleFacial.getText().toString();
                            }

                            if ( maleTatoo.isChecked() ) {
                                maleServices += "_" + maleTatoo.getText().toString();
                            }

                            if ( maleMoustacheStyle.isChecked() ) {
                                maleServices += "_" + maleMoustacheStyle.getText().toString();
                            }

                            Log.e( TAG , "male service : " + maleServices );
//                            databaseReference.child( "Admin" ).child( id ).child( "male_services" ).setValue( maleServices );

                            // Storing admin side male services values into realtime database
                            if ( femaleHairstyle.isChecked() ) {
                                femaleServices += femaleHairstyle.getText().toString();
                            }

                            if ( femaleFacial.isChecked() ) {
                                femaleServices += "_" + femaleFacial.getText().toString();
                            }

                            if ( femaleTatoo.isChecked() ) {
                                femaleServices += "_" + femaleTatoo.getText().toString();
                            }

                            if ( femaleEyebrows.isChecked() ) {
                                femaleServices += "_" + femaleEyebrows.getText().toString();
                            }

                            Log.e( TAG , "female service : " + femaleServices );
//                            databaseReference.child( "Admin" ).child( id ).child( "female_services" ).setValue( femaleServices );

                            // Storing admin data into realtime database using HashMap Technique
                            adminData = new HashMap <>();
                            adminData.put( "id" , id );
                            adminData.put( "name" , adminNameEdit.getText().toString() );
                            adminData.put( "email" , adminEmailEdit.getText().toString() );
                            adminData.put( "password" , adminPasswordEdit.getText().toString() );
                            adminData.put( "address" , strAdminAddress );
                            adminData.put( "latitude" , latitude );
                            adminData.put( "longitude" , longitude );
                            adminData.put( "male_services" , maleServices );
                            adminData.put( "female_services" , femaleServices );
                            databaseReference.child( "Admin" ).child( id ).setValue( adminData );

                            Intent intent = new Intent( SignupAdminActivity.this , LoginAdminActivity.class );
                            intent.putExtra( "adminID" , id );
                            startActivity( intent );

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText( SignupAdminActivity.this , task.getException().getMessage() ,
                                    Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );
    }

}