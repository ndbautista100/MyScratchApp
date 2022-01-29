package com.example.scratchappfeature;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class CreateRecipeActivity extends AppCompatActivity{
    ImageButton addToolButton;
    Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        addToolButton = (ImageButton) findViewById(R.id.addToolButton);
        addToolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddToolDialog();
            }
        });

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRecipePageActivity();
            }
        });
    }

    public void openAddToolDialog() {
        Intent intent = new Intent(this, AddToolActivity.class);
        startActivity(intent);
    }

    public void openRecipePageActivity() {
        Intent intent = new Intent(this, RecipePageActivity.class);
        startActivity(intent);
    }
}