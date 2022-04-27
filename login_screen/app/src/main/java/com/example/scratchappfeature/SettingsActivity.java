package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import classes.DeleteAccountDialogFragment;
import classes.EnterPasswordDialogFragment;
import classes.SettingsFragment;
import classes.UpdateEmailDialogFragment;

public class SettingsActivity extends AppCompatActivity implements DeleteAccountDialogFragment.DeleteAccountDialogListener, UpdateEmailDialogFragment.UpdateEmailDialogListener, EnterPasswordDialogFragment.EnterPasswordDialogListener {
    private static final String TAG = "SettingsActivity";
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setToolbar();

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.settings_container, new SettingsFragment())
            .commit();

    }

    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void startLoginActivity() {
        startActivity(new Intent(SettingsActivity.this, Login.class));
    }

    @Override
    public void verifyPassword(String newPassword) {
        String email = Objects.requireNonNull(auth.getCurrentUser()).getEmail();
        if(email != null) {
            auth.signInWithEmailAndPassword(email, newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");

                        // now that they verified their password, enter the new email
                        UpdateEmailDialogFragment emailDialog = new UpdateEmailDialogFragment();
                        emailDialog.show(getSupportFragmentManager(), "UpdateEmailDialogFragment");
                    } else {
                        Log.d(TAG, "email: " + email);
                        Log.e(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(SettingsActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    @Override
    public void applyNewEmail(String newEmail) {
        Log.d(TAG, "New email: " + newEmail);
        try {
            FirebaseUser user = auth.getCurrentUser();

            user.updateEmail(newEmail)
                .addOnSuccessListener(unused -> Toast.makeText(SettingsActivity.this, "Email successfully updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(SettingsActivity.this, "Failed to update email.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());
                });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}