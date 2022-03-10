package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import classes.Recipe;

public class EditRecipeActivity extends AppCompatActivity {
    private static final String TAG = "EditRecipeActivity";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Recipe recipe;
    private String recipe_ID;
    private TextView editTextView;
    EditText editRecipeNameEditText;
    EditText editDescriptionEditText;
    EditText editToolsEditText;
    EditText editIngredientsEditText;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        setToolbar();

        // getting recipe ID sent from RecipePageActivity
        Intent intent = getIntent();
        recipe_ID = intent.getStringExtra("edit_recipe");

        // with the recipe ID, find the document and create a Recipe object
        DocumentReference docRef = db.collection("recipes").document(recipe_ID);
        Log.i(TAG, "Found document: " + docRef.getId());
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    recipe = document.toObject(Recipe.class);

                    editTextView = (TextView) findViewById(R.id.editRecipeNameTextView);

                    editRecipeNameEditText = (EditText) findViewById(R.id.editRecipeNameEditText);
                    editRecipeNameEditText.setText(recipe.getName());

                    editDescriptionEditText = (EditText) findViewById(R.id.editRecipeDescriptionEditText);
                    editDescriptionEditText.setText(recipe.getDescription());

                    editToolsEditText = (EditText) findViewById(R.id.editToolsEditText);
                    editToolsEditText.setText(recipe.getTools());

                    editIngredientsEditText = (EditText) findViewById(R.id.editIngredientsEditText);
                    editIngredientsEditText.setText(recipe.getIngredients());

                    doneButton = (Button) findViewById(R.id.editDoneButton);
                    doneButton.setOnClickListener(view -> {
                        if(TextUtils.isEmpty(editRecipeNameEditText.getText().toString())) {
                            editRecipeNameEditText.setError("Please enter your recipe's name.");
                            return;
                        }
                        // update the recipe
                        docRef.update(
                                "description", editDescriptionEditText.getText().toString(),
                                "ingredients", editIngredientsEditText.getText().toString(),
                                "name", editRecipeNameEditText.getText().toString(),
                                "tools", editToolsEditText.getText().toString(),
                                "search", editRecipeNameEditText.getText().toString().toLowerCase() // currently needed for searching the database to ignore case
                        ).addOnSuccessListener(unused -> {
                            Log.i(TAG, "DocumentSnapshot successfully updated!");
                            openRecipePageActivity();
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Error updating document", e));
                    });

                } else {
                    Log.e(TAG, "No such document.");
                }
            } else {
                Log.e(TAG, "get failed with" + task.getException());
            }
        });
    }

    private void openRecipePageActivity() {
        Intent intent = new Intent(getApplicationContext(), RecipePageActivity.class);
        intent.putExtra("open_recipe_from_id", recipe_ID);
        startActivity(intent);
    }

    public void setToolbar() {
        Toolbar toolbarEditRecipe = findViewById(R.id.toolbarEditRecipe);
        setSupportActionBar(toolbarEditRecipe);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                openRecipePageActivity(); // on BACK button: send recipe ID back without updating anything
                                          // RecipePageActivity was removed as the parent of EditRecipeActivity in AndroidManifest.xml to allow this
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}