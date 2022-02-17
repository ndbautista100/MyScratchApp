package com.example.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Register extends AppCompatActivity{
    EditText mFullName, mEmail, mPassword, mConfirmPassword;
    Button mConfirmButton;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate (Bundle savedInstanceStats){
        super.onCreate(savedInstanceStats);
        setContentView(R.layout.activity_register);

        mFullName           = findViewById(R.id.editTextTextName);
        mEmail              = findViewById(R.id.editTextTextEmail);
        mPassword           = findViewById(R.id.editTextTextPassword);
        mConfirmPassword    = findViewById(R.id.editTextTextConfirmPassword);
        mConfirmButton      = findViewById(R.id.ConfirmButton);

        fAuth = FirebaseAuth.getInstance();

        //If User is already logged in, send them back to the main activity
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mConfirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }

                if(TextUtils.equals(password,confirmPassword)){
                    mConfirmPassword.setError("Passwords must match");
                    return;
                }

                if (password.length() < 6){
                    mPassword.setError("Password must be longer than 5 characters");
                    return;
                }

                //Register the User
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            //Change Activity to main
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
