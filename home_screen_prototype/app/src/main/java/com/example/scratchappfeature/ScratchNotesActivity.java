package com.example.scratchappfeature;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class ScratchNotesActivity extends AppCompatActivity {
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_notes);

        imgButton = (ImageButton) findViewById(R.id.createRecipeButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateRecipeActivity();
            }
        });
    }

    public void openCreateRecipeActivity() {
        Intent intent = new Intent(this, CreateRecipeActivity.class);
        startActivity(intent);
    }
}