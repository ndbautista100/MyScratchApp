package com.example.scratchappfeature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        db.collection("recipes")
                .whereEqualTo("user_ID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("Success", FirebaseAuth.getInstance().getCurrentUser().getUid() + " => " + document.getData());
                            }
                        } else {
                            Log.d("err", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_create:
                openScratchNotesActivity();
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_profile:
                openProfilePageActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openScratchNotesActivity() {
        Intent intent = new Intent(this, ScratchNotesActivity.class);
        startActivity(intent);
    }

    public void openProfilePageActivity() {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }

    public void logout(View view){
    FirebaseAuth.getInstance().signOut();
    startActivity(new Intent(getApplicationContext(), Login.class));
    finish();
    }



}