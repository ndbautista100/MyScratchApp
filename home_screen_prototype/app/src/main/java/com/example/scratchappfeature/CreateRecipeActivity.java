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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import classes.AddToolDialogFragment;
import classes.Recipe;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class CreateRecipeActivity extends AppCompatActivity implements AddToolDialogFragment.AddToolDialogListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageButton addToolImageButton;
    Button doneButton;
    TextView toolsTextView;
    EditText recipeNameEditText;
    Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        Toolbar toolbarCreateRecipe = (Toolbar) findViewById(R.id.toolbarCreateRecipe);
        setSupportActionBar(toolbarCreateRecipe);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        toolsTextView = (TextView) findViewById(R.id.toolsTextView);

        addToolImageButton = (ImageButton) findViewById(R.id.addToolImageButton);
        addToolImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddToolDialog();
            }
        });

        recipeNameEditText = (EditText) findViewById(R.id.recipeNameEditText);

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipe = new Recipe(recipeNameEditText.getText().toString());

                if(TextUtils.isEmpty(recipeNameEditText.getText().toString())) {
                    recipeNameEditText.setError("Please enter your recipe's name.");
                    return;
                }

                openRecipePageActivity(recipe);
            }
        });
    }

    public void showAddToolDialog() {
        DialogFragment dialog = new AddToolDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddToolDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // TODO: add the tool to the tools list
        // toolsTextView.setText("tool sample");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // do nothing - they selected the "Cancel" option
    }

    public void openRecipePageActivity(Recipe recipe) {
        // parse Recipe object to Map
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> recipeMap = oMapper.convertValue(recipe, Map.class);


        // add recipe to database
        db.collection("recipes")
            .add(recipeMap)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("Success", "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Failure", "Error adding document", e);
                }
            });

        Intent intent = new Intent(getApplicationContext(), RecipePageActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
}