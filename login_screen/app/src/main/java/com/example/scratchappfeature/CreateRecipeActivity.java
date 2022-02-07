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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import classes.AddIngredientDialogFragment;
import classes.AddToolDialogFragment;
import classes.Recipe;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class CreateRecipeActivity extends AppCompatActivity implements AddToolDialogFragment.AddToolDialogListener, AddIngredientDialogFragment.AddIngredientDialogListener {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageButton addToolImageButton;
    private ImageButton addIngredientImageButton;
    private TextView toolsTextView;
    private TextView ingredientsTextView;
    private EditText recipeNameEditText;
    private EditText toolNameEditText;
    private Button doneButton;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        Toolbar toolbarCreateRecipe = (Toolbar) findViewById(R.id.toolbarCreateRecipe);
        setSupportActionBar(toolbarCreateRecipe);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        recipeNameEditText = (EditText) findViewById(R.id.recipeNameEditText);

        toolsTextView = (TextView) findViewById(R.id.toolsTextView);
        ingredientsTextView = (TextView) findViewById(R.id.ingredientsTextView);

        addToolImageButton = (ImageButton) findViewById(R.id.addToolImageButton);
        addToolImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddToolDialog();
            }
        });

        addIngredientImageButton = (ImageButton) findViewById(R.id.addIngredientImageButton);
        addIngredientImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddIngredientDialog();
            }
        });

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(recipeNameEditText.getText().toString())) {
                    recipeNameEditText.setError("Please enter your recipe's name.");
                    return;
                }
                String user = FirebaseAuth.getInstance().getCurrentUser().toString();
                recipe = new Recipe(recipeNameEditText.getText().toString());
                recipe.setTools(toolsTextView.getText().toString());
                recipe.setIngredients(ingredientsTextView.getText().toString());
                recipe.setUser_ID(user);

                // open recipe page and add recipe to database
                openRecipePageActivity(recipe);
            }
        });
    }

    public void showAddToolDialog() {
        AddToolDialogFragment dialog = new AddToolDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddToolDialogFragment");
    }

    @Override
    public void applyToolName(String toolName) {
        toolsTextView.setText(toolName);
    }

    public void showAddIngredientDialog() {
        AddIngredientDialogFragment dialog = new AddIngredientDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddIngredientDialogFragment");
    }

    @Override
    public void applyIngredientName(String ingredientName) {
        ingredientsTextView.setText(ingredientName);
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