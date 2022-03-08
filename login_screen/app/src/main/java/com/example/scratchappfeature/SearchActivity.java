package com.example.scratchappfeature;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_notes);

        setToolbar();
    }

    public void setToolbar() {
        Toolbar toolbarSearch = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbarSearch);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}