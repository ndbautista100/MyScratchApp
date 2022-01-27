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

public class Login extends AppCompatActivity {
    EditText userEmail, userPass;
    Button logBtn;
    TextView createBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.editTextTextEmailAddress);
        userPass = findViewById(R.id.editTextTextPassword);
        fAuth = FirebaseAuth.getInstance();
        logBtn = (Button) findViewById(R.id.button2);
        logBtn.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){
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
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else{Toast.makeText(Login.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }
}
