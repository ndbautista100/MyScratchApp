package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText mFullName, mEmail, mPassword, mConfirmPassword;
    Button mRegisterButton;
    TextView mAlreadyMember;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.editTextTextRegisterName);
        mEmail = findViewById(R.id.editTextTextRegisterEmail);
        mPassword = findViewById(R.id.editTextTextRegisterPassword);
        mConfirmPassword = findViewById(R.id.editTextTextRegisterConfirmPassword);
        mRegisterButton = findViewById(R.id.confirmButton);
        mAlreadyMember = findViewById(R.id.textViewAlreadyMember);

        fAuth = FirebaseAuth.getInstance();

        // If User is already logged in, send them back to main activity
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent (getApplicationContext(), MainActivity.class));
            finish();
        }

        // If "Already a Member" is clicked, send user back to Login
        mAlreadyMember.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        });

        mRegisterButton.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String confirmPassword = mConfirmPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                mEmail.setError("Email is Required");
                return;
            }
            if(TextUtils.isEmpty(password)){
                mPassword.setError("Password is Required");
                return;
            }
            if(!TextUtils.equals(password,confirmPassword)){
                mConfirmPassword.setError("Passwords Must Match");
                return;
            }
            if (password.length() < 6){
                mPassword.setError("Password Must Be >= 6 Characters");
                return;
            }

            //Register the User
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "Account successfully created!", Toast.LENGTH_SHORT).show();
                    //Change Activity to Main
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}