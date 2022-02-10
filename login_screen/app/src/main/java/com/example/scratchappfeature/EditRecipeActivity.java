package com.example.scratchappfeature;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import classes.Recipe;

public class EditRecipeActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Recipe recipe;
    private TextView recipeNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        Toolbar toolbarCreateRecipe = (Toolbar) findViewById(R.id.toolbarEditRecipe);
        setSupportActionBar(toolbarCreateRecipe);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // getting recipe sent from RecipePageActivity
        Intent intent = getIntent();
        recipe = (Recipe) intent.getSerializableExtra("edit_recipe");

        recipeNameTextView = (TextView) findViewById(R.id.recipeNameTextView);
        recipeNameTextView.setText(recipe.getDocument_ID());

        // edit recipe
        // at the end - add to database
        // not add - UPDATE the data
        // how? - create ids for each recipe?
        // once a recipe is created in CreateRecipe - set the id using documentReference.getId()


    }
}