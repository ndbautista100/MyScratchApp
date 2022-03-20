package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView createAccTextView, forgotPasswordTextView;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        createAccTextView = (TextView) findViewById(R.id.newHereTextView);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if(TextUtils.isEmpty(email)) {
                emailEditText.setError("Email is required.");
                return;
            }
            if(TextUtils.isEmpty(password)) {
                emailEditText.setError("Password is required.");
                return;
            }
            if(password.length() < 6) {
                passwordEditText.setError("Password must be more than 6 characters.");
                return;
            }
            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(Login.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(Login.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        createAccTextView.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Register.class)));

        forgotPasswordTextView.setOnClickListener(v -> {
            EditText resetMail =  new EditText(v.getContext());
            AlertDialog.Builder passwordReset = new AlertDialog.Builder(v.getContext());
            passwordReset.setTitle("Reset password?");
            passwordReset.setMessage("Enter your email to receive a reset link.");
            passwordReset.setView(resetMail);

            passwordReset.setPositiveButton("Yes", (dialogInterface, i) -> {
                String mail = resetMail.getText().toString();
                fAuth.sendPasswordResetEmail(mail)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(Login.this, "Reset link has been sent to your email.", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(Login.this, "Error! Reset link could not be sent." + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            });

            passwordReset.setNegativeButton("No", (dialogInterface, i) -> {
                // do nothing, so dialog closes
            });

            passwordReset.create().show();
        });
    }
}
