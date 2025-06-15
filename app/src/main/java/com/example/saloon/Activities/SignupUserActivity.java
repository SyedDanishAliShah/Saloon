package com.example.saloon.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.example.saloon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import xyz.teamgravity.imageradiobutton.GravityImageRadioButton;
import xyz.teamgravity.imageradiobutton.GravityRadioGroup;


public class SignupUserActivity extends AppCompatActivity {

    TextInputEditText userNameEdit, userEmailEdit, userPasswordEdit;
    GravityRadioGroup radioGroup;
    GravityImageRadioButton male, female;
    Button btnUserSignup;
    boolean valid = false;
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth.AuthStateListener mAuthListener;
    TextView userLogin;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_signup );

        // Casting
        userNameEdit = findViewById( R.id.userNameEdit );
        userEmailEdit = findViewById( R.id.userEmailEdit );
        userPasswordEdit = findViewById( R.id.userPasswordEdit );
        radioGroup = findViewById( R.id.radioGroup );
        male = findViewById( R.id.male );
        female = findViewById( R.id.female );
        btnUserSignup = findViewById( R.id.btnUserSignup );
        userLogin = findViewById( R.id.userLogin );

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        btnUserSignup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                checkFieldValidation( userNameEdit );
                checkFieldValidation( userEmailEdit );
                checkFieldValidation( userPasswordEdit );

                if ( valid ) {
                    signupUser();
                }
            }
        } );

        userLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startActivity( new Intent( SignupUserActivity.this , LoginUserActivity.class ) );
            }
        } );
    }

    private boolean checkFieldValidation( TextInputEditText text ) {

        if ( text.getText().toString().isEmpty() ) {
            text.setError( "Error" );
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }

    private void signupUser() {

        // User registration process
        firebaseAuth.createUserWithEmailAndPassword( userEmailEdit.getText().toString() , userPasswordEdit.getText().toString() )
                .addOnCompleteListener( SignupUserActivity.this , new OnCompleteListener < AuthResult >() {
                    @Override
                    public void onComplete( @NonNull Task < AuthResult > task ) {
                        if ( task.isSuccessful() ) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText( SignupUserActivity.this , "Sign up success!" , Toast.LENGTH_SHORT ).show();

                            String id = task.getResult().getUser().getUid();

                            // Storing data user into realtime database using HashMap Technique
                            Map < String, Object > userData = new HashMap <>();
                            userData.put( "id" , id );
                            userData.put( "name" , userNameEdit.getText().toString() );
                            userData.put( "email" , userEmailEdit.getText().toString() );
                            userData.put( "password" , userPasswordEdit.getText().toString() );

                            if ( male.isChecked() ) {
                                userData.put( "gender" , male.text() );
                            } else {
                                userData.put( "gender" , female.text() );
                            }

                            databaseReference.child( "Users" ).child( id ).setValue( userData );

                            startActivity( new Intent( SignupUserActivity.this , LoginUserActivity.class ) );
                            Toast.makeText( SignupUserActivity.this , "Account created you can login now..." , Toast.LENGTH_SHORT ).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText( SignupUserActivity.this , task.getException().getMessage() ,
                                    Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );

    }
}