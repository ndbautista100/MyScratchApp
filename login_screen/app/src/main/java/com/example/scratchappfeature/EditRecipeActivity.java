package com.example.scratchappfeature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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
    private TextView recipeNameTextView;
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
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Log.d("Success", "Found document!");
                        recipe = document.toObject(Recipe.class);
                    } else {
                        Log.d("Fail", "No such document.");
                    }
                } else {
                    Log.d("Fail", "get failed with" + task.getException());
                }
            }
        });

        // on DONE button: send recipe ID back after updating
        // on BACK button: send recipe ID back without updating anything

        recipeNameTextView = (TextView) findViewById(R.id.recipeNameTextView);
        recipeNameTextView.setText(recipe_ID);

        doneButton = (Button) findViewById(R.id.editDoneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRecipePageActivity(recipe_ID);
            }
        });

        // edit recipe
        // at the end - add to database
        // not add - UPDATE the data
        // how? - create ids for each recipe?
        // once a recipe is created in CreateRecipe - set the id using documentReference.getId()
        // https://firebase.google.com/docs/firestore/query-data/get-data

    }

    private void openRecipePageActivity(String recipe_id) {
        Intent intent = new Intent(getApplicationContext(), RecipePageActivity.class);
        intent.putExtra("edit_recipe_done", recipe_ID);
        startActivity(intent);
    }
}