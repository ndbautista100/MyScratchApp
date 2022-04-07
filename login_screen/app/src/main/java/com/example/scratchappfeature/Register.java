package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {
    private EditText mFullName, mEmail, mPassword, mConfirmPassword;
    private Button mRegisterButton;
    private TextView mAlreadyMember;
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        // If User is already logged in, send them back to main activity
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent (getApplicationContext(), MainActivity.class));
            finish();
        }

        // If AlreadyMember is clicked, send user back to Login
        mAlreadyMember.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        });

        mRegisterButton.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String confirmPassword = mConfirmPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email)) {
                mEmail.setError("Email is required.");
                return;
            }
            if(TextUtils.isEmpty(password)) {
                mPassword.setError("Password is required.");
                return;
            }
            if(!TextUtils.equals(password,confirmPassword)) {
                mConfirmPassword.setError("Passwords must match.");
                return;
            }
            if (password.length() < 6) {
                mPassword.setError("Password must be at least 6 characters.");
                return;
            }

            // Register the User
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "User created!", Toast.LENGTH_SHORT).show();

                    // Take user to create profile
                    Intent intent = new Intent(getApplicationContext(), CreateProfileActivity.class);
                    intent.putExtra("user_display_name", mFullName.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}