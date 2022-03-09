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
    EditText userEmail, userPass;
    Button logBtn;
    TextView createAcc;
    FirebaseAuth fAuth;
    TextView forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.editTextTextEmailAddress);
        userPass = findViewById(R.id.editTextTextPassword);
        fAuth = FirebaseAuth.getInstance();
        forgot = findViewById(R.id.textView2);
        logBtn = (Button) findViewById(R.id.button2);
        createAcc = (TextView) findViewById(R.id.create_acc);

        logBtn.setOnClickListener(v -> {
            String email = userEmail.getText().toString();
            String password = userPass.getText().toString();

            if(TextUtils.isEmpty(email)){
                userEmail.setError("Email is Required.");
                return;
            }
            if(TextUtils.isEmpty(password)){
                userEmail.setError("Password is Required.");
                return;
            }
            if(password.length() < 6){
                userPass.setError("Password must be more than 6 characters");
                return;
            }

            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        forgot.setOnClickListener(v -> {
            EditText resetMail =  new EditText(v.getContext());
            AlertDialog.Builder passwordReset = new AlertDialog.Builder(v.getContext());
            passwordReset.setTitle("Reset Password?");
            passwordReset.setMessage("Enter Your Email to receive a reset link.");
            passwordReset.setView(resetMail);

            passwordReset.setPositiveButton("Yes", (dialogInterface, i) -> {
                String mail = resetMail.getText().toString();
                fAuth.sendPasswordResetEmail(mail)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(Login.this, "Reset link has been sent to your email", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(Login.this, "Error! Reset link could not be sent." + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            });

            passwordReset.setNegativeButton("No", (dialogInterface, i) -> { }); // nothing so dialog closes

            passwordReset.create().show();
        });

        createAcc.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Register.class)));
    }
}
