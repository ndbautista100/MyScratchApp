package com.example.scratchappfeature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import classes.Recipe;

public class EditRecipeActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        Toolbar toolbarCreateRecipe = (Toolbar) findViewById(R.id.toolbarEditRecipe);
        setSupportActionBar(toolbarCreateRecipe);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // getting recipe ID sent from RecipePageActivity
        Intent intent = getIntent();
        recipe_ID = intent.getStringExtra("edit_recipe");

        // with the recipe ID, find the document and create a Recipe object
        DocumentReference docRef = db.collection("recipes").document(recipe_ID);
        Log.d("Found document", docRef.getId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Log.d("Success", "Found document!");
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
                        doneButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(TextUtils.isEmpty(editRecipeNameEditText.getText().toString())) {
                                    editRecipeNameEditText.setError("Please enter your recipe's name.");
                                    return;
                                }
                                // update the recipe
                                docRef.update(
                                        "description", editDescriptionEditText.getText().toString(),
                                        "ingredients", editIngredientsEditText.getText().toString(),
                                        "name", editRecipeNameEditText.getText().toString(),
                                        "tools", editToolsEditText.getText().toString()
                                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Success", "DocumentSnapshot successfully updated!");
                                        openRecipePageActivity();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Fail", "Error updating document", e);
                                    }
                                });
                            }
                        });

                    } else {
                        Log.d("Fail", "No such document.");
                    }
                } else {
                    Log.d("Fail", "get failed with" + task.getException());
                }
            }
        });
    }

    private void openRecipePageActivity() {
        Intent intent = new Intent(getApplicationContext(), RecipePageActivity.class);
        intent.putExtra("edit_recipe_done", recipe_ID);
        startActivity(intent);
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