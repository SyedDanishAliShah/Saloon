package com.example.saloon.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class LoginAdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextInputEditText loginAdminEmailEdit, loginAdminPasswordEdit;
    Button btnAdminLogin;
    TextView adminNewAccount;
    String adminId;
    boolean valid = false;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login_admin );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        loginAdminEmailEdit = findViewById( R.id.loginAdminEmailEdit );
        loginAdminPasswordEdit = findViewById( R.id.loginAdminPasswordEdit );
        btnAdminLogin = findViewById( R.id.btnAdminLogin );
        adminNewAccount = findViewById( R.id.adminNewAccount );

        btnAdminLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                checkFieldValidation( loginAdminEmailEdit );
                checkFieldValidation( loginAdminPasswordEdit );

                if ( valid ) {
                    loginAdmin();
                }
            }
        } );

        adminNewAccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startActivity( new Intent( LoginAdminActivity.this , SignupAdminActivity.class ) );
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

    private void loginAdmin() {
        mAuth.signInWithEmailAndPassword( loginAdminEmailEdit.getText().toString() , loginAdminPasswordEdit.getText().toString() )
                .addOnCompleteListener( this , new OnCompleteListener < AuthResult >() {
                    @Override
                    public void onComplete( @NonNull Task < AuthResult > task ) {
                        if ( task.isSuccessful() ) {

                            adminId = task.getResult().getUser().getUid();

                            SharedPreferences.Editor editor = getSharedPreferences( Constants.myPrefs , Context.MODE_PRIVATE ).edit();
                            editor.putBoolean( Constants.adminCheck , true );
                            editor.putString( Constants.adminIDShared , adminId );
                            editor.apply();

                            // Sign in success, update UI with the signed-in user's information
//                            user = mAuth.getCurrentUser();
//                            Toast.makeText(LoginAdminActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Toast.makeText( LoginAdminActivity.this , "Login success!" , Toast.LENGTH_SHORT ).show();
                            Intent intent = new Intent( LoginAdminActivity.this , ClientOrderActivity.class );
//                            intent.putExtra( "adminID", adminId );
                            startActivity( intent );
//                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText( LoginAdminActivity.this , task.getException().getMessage() ,
                                    Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity( new Intent( LoginAdminActivity.this , LoginUserActivity.class ) );
        finish();
    }
}