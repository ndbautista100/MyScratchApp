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

import classes.Profile;

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

            if(TextUtils.isEmpty(mFullName.getText().toString())) {
                mFullName.setError("Please enter a name");
                return;
            }
            if(TextUtils.isEmpty(email)){
                mEmail.setError("Email is required");
                return;
            }
            if(TextUtils.isEmpty(password)){
                mPassword.setError("Password is required");
                return;
            }
            if(!TextUtils.equals(password,confirmPassword)){
                mConfirmPassword.setError("Passwords must match");
                return;
            }
            if (password.length() < 6){
                mPassword.setError("Password must be at least 6 characters");
                return;
            }

            //Register the User
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "Account successfully created!", Toast.LENGTH_SHORT).show();
                    // prompt user to create a profile
                    Intent intent = new Intent(getApplicationContext(), CreateProfileActivity.class);
                    intent.putExtra("registered_name", mFullName.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}