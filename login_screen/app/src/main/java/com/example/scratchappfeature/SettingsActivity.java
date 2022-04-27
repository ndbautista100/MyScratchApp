package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import classes.ChangePasswordDialogFragment;
import classes.DeleteAccountDialogFragment;
import classes.EnterPasswordDialogFragment;
import classes.SettingsFragment;
import classes.UpdateEmailDialogFragment;

public class SettingsActivity extends AppCompatActivity implements DeleteAccountDialogFragment.DeleteAccountDialogListener, UpdateEmailDialogFragment.UpdateEmailDialogListener, EnterPasswordDialogFragment.EnterPasswordDialogListener, ChangePasswordDialogFragment.ChangePasswordDialogListener {
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

    @Override
    public void changePassword(String currentPassword, String newPassword, String confirmPassword) {
        String email = Objects.requireNonNull(auth.getCurrentUser()).getEmail();
        if(email != null) {
            auth.signInWithEmailAndPassword(email, currentPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");

                        // now that they verified their password, change their password
                        if (TextUtils.equals(newPassword, confirmPassword)) {
                            if (newPassword.length() < 6 || confirmPassword.length() < 6) {
                                Toast.makeText(SettingsActivity.this, "Password must be at least 6 characters, try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseUser user = auth.getCurrentUser();

                                user.updatePassword(newPassword)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(SettingsActivity.this, "Password updated!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(SettingsActivity.this, "Password failed to update.", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Update password failed: ", task1.getException());
                                        }
                                    });
                            }
                        } else {
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SettingsActivity.this, "Passwords do not match, try again.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.e(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(SettingsActivity.this, "Incorrect password, try again.", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }
}