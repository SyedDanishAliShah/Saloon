package com.example.saloon.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.saloon.Constants;
import com.example.saloon.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    String TAG = "splash";
    boolean splash = false;
    boolean checkAdmin = false;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user = null;
    String user_idd;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash_screen );
        FirebaseApp.initializeApp(this);

        SharedPreferences sharedPreferences = getSharedPreferences( Constants.myPrefs , Context.MODE_PRIVATE );
        splash = sharedPreferences.getBoolean( Constants.splashBool , false );
        user_idd = sharedPreferences.getString( Constants.userIDShared , "d" );
        checkAdmin = sharedPreferences.getBoolean( Constants.adminCheck , false );
        firebaseAuth = FirebaseAuth.getInstance();

        Log.e( TAG , "checkAdmin : " + checkAdmin );

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( @NonNull FirebaseAuth firebaseAuth ) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if ( user != null ) {
//                user_idd = user.getUid();
                }

            }
        };
//        Toast.makeText( SplashScreenActivity.this , "splash" + splash , Toast.LENGTH_SHORT ).show();

        Log.e( TAG , "usersss : " + user );


        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {

                if ( user == null ) {
                    startActivity( new Intent( SplashScreenActivity.this , LoginUserActivity.class ) );
                } else {

                    Intent intent;
                    if ( splash ) {
                        intent = new Intent( SplashScreenActivity.this , PlaceOrderActivity.class );
                    } else if ( checkAdmin ) {
                        intent = new Intent( SplashScreenActivity.this , ClientOrderActivity.class );
                    } else {
                        intent = new Intent( SplashScreenActivity.this , MapsActivity.class );
                    }
                    intent.putExtra( "userId" , user_idd );
                    startActivity( intent );

                }
                finish();


//                if ( user != null ) {
//                    // User is signed in
//                    if ( splash ) {
//                        startActivity( new Intent( SplashScreenActivity.this , PlaceOrderActivity.class ) );
//                    } else {
//                        startActivity( new Intent( SplashScreenActivity.this , MapsActivity.class ) );
//                    }
//                } else {
//                    // No user is signed in
//                    startActivity( new Intent( SplashScreenActivity.this , LoginUserActivity.class ) );
//                }
//                finish();
            }
        } , 3000 );
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener( mAuthListener );
    }

}