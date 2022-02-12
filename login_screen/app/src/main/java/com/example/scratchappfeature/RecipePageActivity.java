package com.example.scratchappfeature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import classes.Recipe;

public class RecipePageActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Recipe recipe;
    private String recipe_ID;
    private ImageView recipeImagesImageView;
    private Button showImagesButton;
    private TextView descriptionTextView;
    private TextView toolsTextView;
    private TextView ingredientsTextView;
    private int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_page);
        Toolbar toolbarScratchNotes = (Toolbar) findViewById(R.id.toolbarRecipePage);
        setSupportActionBar(toolbarScratchNotes);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // getting recipe sent from CreateRecipeActivity
        // create different scenarios for opening from CreateRecipe and EditRecipe
        Intent intent = getIntent();

        if(intent.hasExtra("create_recipe")) {
            recipe = (Recipe) intent.getSerializableExtra("create_recipe");
            ab.setTitle(recipe.getName()); // set toolbar title using the recipe name

            descriptionTextView = (TextView) findViewById(R.id.recipePageDescriptionTextView);
            descriptionTextView.setText(recipe.getDescription());
            toolsTextView = (TextView) findViewById(R.id.toolsTextViewRecipePage);
            toolsTextView.setText(recipe.getTools());
            ingredientsTextView = (TextView) findViewById(R.id.ingredientsTextViewRecipePage);
            ingredientsTextView.setText(recipe.getIngredients());

        } else if (intent.hasExtra("edit_recipe_done")) {
            recipe_ID = intent.getStringExtra("edit_recipe_done");

            // with the recipe ID, find the document and create a Recipe object
            DocumentReference docRef = db.collection("recipes").document(recipe_ID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()) {
                            Log.d("Success", "Found document!");

                            recipe = document.toObject(Recipe.class);

                            ab.setTitle(recipe.getName()); // set toolbar title using the updated recipe name

                            descriptionTextView = (TextView) findViewById(R.id.recipePageDescriptionTextView);
                            descriptionTextView.setText(recipe.getDescription());
                            toolsTextView = (TextView) findViewById(R.id.toolsTextViewRecipePage);
                            toolsTextView.setText(recipe.getTools());
                            ingredientsTextView = (TextView) findViewById(R.id.ingredientsTextViewRecipePage);
                            ingredientsTextView.setText(recipe.getIngredients());
                        } else {
                            Log.d("Fail", "No such document.");
                        }
                    } else {
                        Log.d("Fail", "get failed with" + task.getException());
                    }
                }
            });
        }
    }

    public void openEditRecipeActivity() {
        Intent intent = new Intent(getApplicationContext(), EditRecipeActivity.class);
        intent.putExtra("edit_recipe", recipe.getDocument_ID());
        startActivity(intent);
    }

    /*
        Opens the tool bar for the Recipe Page
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_page, menu);
        return true;
    }

    /*
        Recipe Page action bar options:
        - Share Recipe
        - Edit Recipe
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share_recipe:
                return true;
            case R.id.action_edit_recipe:
                openEditRecipeActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}