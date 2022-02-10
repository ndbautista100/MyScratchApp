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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import classes.Recipe;

public class RecipePageActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Recipe recipe;
    private ImageView recipeImagesImageView;
    private Button showImagesButton;
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
        Intent intent = getIntent();
        recipe = (Recipe) intent.getSerializableExtra("create_recipe");
        ab.setTitle(recipe.getName()); // set toolbar title using the recipe name

        toolsTextView = (TextView) findViewById(R.id.toolsTextViewRecipePage);
        toolsTextView.setText(recipe.getTools());
        ingredientsTextView = (TextView) findViewById(R.id.ingredientsTextViewRecipePage);
        ingredientsTextView.setText(recipe.getIngredients());

        // database snippet - everything below here doesn't work yet
        Map<String, Object> recipeMap = new HashMap<>();
        recipeMap.put("name", recipe.getName());

        showImagesButton = (Button) findViewById(R.id.showImagesButton);
        recipeImagesImageView = (ImageView) findViewById(R.id.recipeImagesImageView);

        showImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

    }

    public void openEditRecipeActivity() {
        // use database?

        Intent intent = new Intent(getApplicationContext(), EditRecipeActivity.class);
        intent.putExtra("edit_recipe", recipe);
        startActivity(intent);
    }

    void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
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