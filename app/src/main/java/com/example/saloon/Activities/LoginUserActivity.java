package com.example.saloon.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saloon.Constants;
import com.example.saloon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginUserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextInputEditText loginUserEmailEdit, loginUserPasswordEdit;
    Button btnUserLogin;
    TextView userNewAccount;
    String userId;
    boolean valid = false;
    SharedPreferences sharedPreferences;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login_user );

        loginUserEmailEdit = findViewById( R.id.loginUserEmailEdit );
        loginUserPasswordEdit = findViewById( R.id.loginUserPasswordEdit );
        btnUserLogin = findViewById( R.id.btnUserLogin );
        userNewAccount = findViewById( R.id.userNewAccount );

        sharedPreferences = getSharedPreferences(Constants.myPrefs, Context.MODE_PRIVATE );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged( @NonNull FirebaseAuth firebaseAuth ) {
//                userId = firebaseAuth.getCurrentUser().getUid();
//            }
//        };

        btnUserLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                checkFieldValidation( loginUserEmailEdit );
                checkFieldValidation( loginUserPasswordEdit );

                if ( valid ) {

                    mAuth.signInWithEmailAndPassword( loginUserEmailEdit.getText().toString() , loginUserPasswordEdit.getText().toString() ).addOnCompleteListener( LoginUserActivity.this , new OnCompleteListener < AuthResult >() {
                        @Override
                        public void onComplete( @NonNull Task < AuthResult > task ) {
                            if ( task.isSuccessful() ) {
                                // Sign in success, update UI with the signed-in user's information
//                                user = mAuth.getCurrentUser();

                                userId = mAuth.getCurrentUser().getUid();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString( Constants.userIDShared , userId );
                                editor.apply();

                                Log.e( "loginActivity" , "login user id : " + userId );

//                            Toast.makeText(LoginUserActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Toast.makeText( LoginUserActivity.this , "Login success!" , Toast.LENGTH_SHORT ).show();
                                Intent intent = new Intent( LoginUserActivity.this , MapsActivity.class );
//                                intent.putExtra( "uId" , userId );
                                startActivity( intent );
//                            finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText( LoginUserActivity.this , task.getException().getMessage() ,
                                        Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );

                }
            }
        } );

        userNewAccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startActivity( new Intent( LoginUserActivity.this , SignupUserActivity.class ) );
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

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu , menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
        if ( item.getItemId() == R.id.adminLoginItem ) {
            startActivity( new Intent( LoginUserActivity.this , LoginAdminActivity.class ) );
            return true;
        } else {
            return super.onOptionsItemSelected( item );
        }
    }

    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
        MenuItem historyItem = menu.findItem( R.id.historyItem );
        MenuItem logout = menu.findItem( R.id.logout );
        historyItem.setVisible( false );
        logout.setVisible( false );
        return super.onPrepareOptionsMenu( menu );
    }
}