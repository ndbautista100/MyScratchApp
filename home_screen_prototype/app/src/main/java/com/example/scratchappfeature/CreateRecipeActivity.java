package com.example.scratchappfeature;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import classes.AddToolDialogFragment;

public class CreateRecipeActivity extends AppCompatActivity implements AddToolDialogFragment.AddToolDialogListener {
    ImageButton addToolButton;
    Button doneButton;
    TextView toolsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        Toolbar toolbarCreateRecipe = (Toolbar) findViewById(R.id.toolbarCreateRecipe);
        setSupportActionBar(toolbarCreateRecipe);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        toolsList = (TextView) findViewById(R.id.toolsList);

        addToolButton = (ImageButton) findViewById(R.id.addToolButton);
        addToolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddToolDialog();
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

    public void showAddToolDialog() {
        DialogFragment dialog = new AddToolDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddToolDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // add the tool to the tools list
        toolsList.setText("tool sample");

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // do nothing - they selected the "Cancel" option
    }

    public void openRecipePageActivity() {
        Intent intent = new Intent(this, RecipePageActivity.class);
        startActivity(intent);
    }
}