package com.example.saloon.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.saloon.Constants;
import com.example.saloon.Models.SaloonModel;
import com.example.saloon.R;
import com.example.saloon.databinding.ActivityMapsBinding;
import com.firebase.client.Firebase;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    SupportMapFragment mapFragment;
    private GoogleMap mMap, saloonMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient client;
    String title;
    private DatabaseReference databaseReference;
    MarkerOptions markerOptions, saloonMarkerOptions;
    Marker marker;
    SaloonModel saloonModel;
    String TAG = "Maps";
    SharedPreferences sharedPreferences;
    ImageButton img_logout;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        binding = ActivityMapsBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( @NonNull FirebaseAuth firebaseAuth ) {

                String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                sharedPreferences = getSharedPreferences( Constants.myPrefs , Context.MODE_PRIVATE );
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString( Constants.userIDShared , uId );
                editor.apply();
            }
        };


        databaseReference = FirebaseDatabase.getInstance().getReference();


//        Intent intent = getIntent();
//        uId = intent.getStringExtra( "userId" );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = ( SupportMapFragment ) getSupportFragmentManager().findFragmentById( R.id.map );

        img_logout = findViewById( R.id.logut_map );
        img_logout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                AuthUI.getInstance()
                        .signOut( MapsActivity.this )
                        .addOnCompleteListener( new OnCompleteListener < Void >() {
                            public void onComplete( @NonNull Task < Void > task ) {
                                // user is now signed out
                                startActivity( new Intent( MapsActivity.this , LoginUserActivity.class ) );
                                finish();
                                Toast.makeText( MapsActivity.this , "Logout success" , Toast.LENGTH_SHORT ).show();
                            }
                        } );
            }
        } );
        client = LocationServices.getFusedLocationProviderClient( this );

//        manageRunTimePermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        manageRunTimePermission();
    }

    private void manageRunTimePermission() {
        // Processing the request permissions at runtime.
        Dexter.withContext( getApplicationContext() )
                .withPermission( Manifest.permission.ACCESS_FINE_LOCATION )
                .withListener( new PermissionListener() {
                    @Override
                    public void onPermissionGranted( PermissionGrantedResponse permissionGrantedResponse ) {
                        // If permission granted then do this
                        getLocation();
                    }

                    @Override
                    public void onPermissionDenied( PermissionDeniedResponse permissionDeniedResponse ) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown( PermissionRequest permissionRequest , PermissionToken permissionToken ) {
                        permissionToken.continuePermissionRequest();        //If permission deny and if app open again then it sync the permission again
                    }
                } )
                .withErrorListener( new PermissionRequestErrorListener() {
                    @Override
                    public void onError( DexterError dexterError ) {
                        Toast.makeText( MapsActivity.this , dexterError.toString() , Toast.LENGTH_SHORT ).show();
                    }
                } ).check();
    }

    private BitmapDescriptor BitmapFromVector( MapsActivity context , int vectorResId ) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable( MapsActivity.this , vectorResId );

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds( 0 , 0 , vectorDrawable.getIntrinsicWidth() , vectorDrawable.getIntrinsicHeight() );

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap( vectorDrawable.getIntrinsicWidth() , vectorDrawable.getIntrinsicHeight() , Bitmap.Config.ARGB_8888 );

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas( bitmap );

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw( canvas );

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap( bitmap );
    }

    private void getLocation() {
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

        // Getting last location
        Task < Location > task = client.getLastLocation();

        task.addOnSuccessListener( new OnSuccessListener < Location >() {
            @Override
            public void onSuccess( Location location ) {
                mapFragment.getMapAsync( new OnMapReadyCallback() {
                    @Override
                    public void onMapReady( @NonNull GoogleMap googleMap ) {

                        mMap = googleMap;

                        LatLng latLng = new LatLng( location.getLatitude() , location.getLongitude() );

                        Log.e( TAG , "user lat long : " + latLng );

                        markerOptions = new MarkerOptions().position( latLng ).title( "You are here" )
                                // below line is use to add custom marker on our map.
                                .icon( BitmapFromVector( MapsActivity.this , R.drawable.ic_person_stand ) )
                                .flat( true );

                        mMap.addMarker( markerOptions );
                        mMap.moveCamera( CameraUpdateFactory.newLatLng( latLng ) );

                        // Moving the camera to current location
                        mMap.moveCamera( CameraUpdateFactory.newLatLng( latLng ) );
                        // zoom in or zoom out the camera
                        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng , 16f ) );

                        // Setting type of google map
//                        mMap.setMapType( GoogleMap.MAP_TYPE_NORMAL );
//                        mMap.setMapType( GoogleMap.MAP_TYPE_HYBRID );

                        // Clicking anywhere in the map to add a marker at that place
//                        mMap.setOnMapClickListener( new GoogleMap.OnMapClickListener() {
//                            @Override
//                            public void onMapClick( @NonNull LatLng latLng ) {
////                                mMap.addMarker( new MarkerOptions().position( latLng ).title( "You have clicked here..." ) );
//
//                                // Getting the complete location from map
//                                Geocoder geocoder = new Geocoder( MapsActivity.this );
//
//                                try {
//                                    ArrayList < Address > addressArrayList = ( ArrayList < Address > ) geocoder.getFromLocation( latLng.latitude , latLng.longitude , 1 );
//                                    Log.e( "map" , "Address : " + addressArrayList );
//                                } catch ( IOException e ) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        } );

//                        int adminLength = databaseReference.child( "Admin" ).getKey().length();
//                        Log.e( TAG , "adminLength : " + adminLength );

                        List < SaloonModel > list = new ArrayList < SaloonModel >();

                        // Showing nearby saloon
                        // addValueEventListener automatically call if data update
                        databaseReference.child( "Admin" ).addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange( @NonNull DataSnapshot snapshot ) {

                                saloonMap = googleMap;

                                int ii = 0;

                                for ( DataSnapshot dataSnapshot : snapshot.getChildren() ) {

                                    saloonModel = new SaloonModel();

                                    ii++;

                                    saloonModel = dataSnapshot.getValue( SaloonModel.class );

                                    list.add( saloonModel );

//                                    LatLng latLng = new LatLng( Double.parseDouble( dataSnapshot.getValue( SaloonModel.class ).getLatitude() ) , Double.parseDouble( dataSnapshot.getValue( SaloonModel.class ).getLatitude() ) );
                                    LatLng latLng = new LatLng( Double.parseDouble( list.get( ii - 1 ).getLatitude() ) , Double.parseDouble( list.get( ii - 1 ).getLongitude() ) );
                                    saloonMarkerOptions = new MarkerOptions().position( latLng ).title( saloonModel.getId() );
                                    marker = saloonMap.addMarker( saloonMarkerOptions );
                                    title = marker.getTitle();

                                    saloonMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick( @NonNull Marker marker ) {

//                                            Log.e( TAG , "admin id 1 : " + marker.getTitle() );

                                            for ( int i = 0; i < list.size(); i++ ) {

                                                if ( list.get( i ).getId().equals( marker.getTitle() ) ) {
                                                    sharedPreferences = getSharedPreferences( Constants.myPrefs , Context.MODE_PRIVATE );
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString( Constants.adminIDShared , marker.getTitle() );
                                                    editor.apply();
//                                                    editor.commit();

                                                    Log.e( TAG , "AdminShareId: " + marker.getTitle() );

                                                    Intent intent = new Intent( MapsActivity.this , PlaceOrderActivity.class );
//                                                    intent.putExtra( "adminId" , marker.getTitle() );
//                                                    intent.putExtra( "userId", uId );
                                                    startActivity( intent );

//                                                    Toast.makeText( MapsActivity.this , marker.getTitle() , Toast.LENGTH_LONG ).show();

//                                                    Log.e( TAG , "admin id 2 : " + marker.getTitle()  );

                                                }

                                            }

                                            return false;
                                        }
                                    } );

                                }
                            }

                            @Override
                            public void onCancelled( @NonNull DatabaseError error ) {

                            }
                        } );
                    }
                } );
            }
        } );
    }

}