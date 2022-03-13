package com.example.scratchappfeature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import classes.Profile;

public class Register extends AppCompatActivity {
    EditText mFullName, mEmail, mPassword, mConfirmPassword;
    Button mRegisterButton;
    TextView mAlreadyMember;
    FirebaseAuth fAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName           = findViewById(R.id.editTextTextRegisterName);
        mEmail              = findViewById(R.id.editTextTextRegisterEmail);
        mPassword           = findViewById(R.id.editTextTextRegisterPassword);
        mConfirmPassword    = findViewById(R.id.editTextTextRegisterConfirmPassword);
        mRegisterButton     = findViewById(R.id.confirmButton);
        mAlreadyMember      = findViewById(R.id.textViewAlreadyMember);

        fAuth = FirebaseAuth.getInstance();
       

        //If User is already logged in, send them back to main activity
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent (getApplicationContext(), MainActivity.class));
            finish();
        }

        //If AlreadyMember is clicked, send user back to Login
        mAlreadyMember.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    private static final String TAG = "new Profile";

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            //Change Activity to Main
                            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            Profile user = new Profile(mFullName.getText().toString(), "default", "default", fAuth.getCurrentUser().getUid());
                            ObjectMapper oMapper = new ObjectMapper();
                            Map<String, Object> profileMap = oMapper.convertValue(user, Map.class);


                            // add profile to database
                            db.collection("profile").document(fAuth.getCurrentUser().getUid()).set(profileMap)
                                    .addOnSuccessListener(documentReference -> {
                                        // update the newly added document to set its document ID - on the Java object and Firebase document reference
                                        user.setDocument_ID(fAuth.getCurrentUser().getUid());


                                    });
                        } else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }


}