package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import classes.AddIngredientDialogFragment;
import classes.AddToolDialogFragment;
import classes.Recipe;

public class CreateRecipeActivity extends AppCompatActivity implements AddToolDialogFragment.AddToolDialogListener, AddIngredientDialogFragment.AddIngredientDialogListener {
    private static final String TAG = "CreateRecipeActivity";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageButton addToolImageButton;
    private ImageButton addIngredientImageButton;
    private TextView toolsTextView;
    private TextView ingredientsTextView;
    private EditText recipeNameEditText;
    private EditText recipeDescriptionEditText;
    private Button doneButton;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        setToolbar();

        recipeNameEditText = (EditText) findViewById(R.id.recipeNameEditText);
        recipeDescriptionEditText = (EditText) findViewById(R.id.recipeDescriptionEditText);

        toolsTextView = (TextView) findViewById(R.id.toolsTextView);
        ingredientsTextView = (TextView) findViewById(R.id.ingredientsTextView);

        addToolImageButton = (ImageButton) findViewById(R.id.addToolImageButton);
        addToolImageButton.setOnClickListener(v -> showAddToolDialog());

        addIngredientImageButton = (ImageButton) findViewById(R.id.addIngredientImageButton);
        addIngredientImageButton.setOnClickListener(view -> showAddIngredientDialog());

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(view -> {
            if(TextUtils.isEmpty(recipeNameEditText.getText().toString())) {
                recipeNameEditText.setError("Please enter your recipe's name.");
                return;
            }
            String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
            recipe = new Recipe(recipeNameEditText.getText().toString());
            recipe.setDescription(recipeDescriptionEditText.getText().toString());
            recipe.setTools(toolsTextView.getText().toString());
            recipe.setIngredients(ingredientsTextView.getText().toString());
            recipe.setUser_ID(user);

            // open recipe page and add recipe to database
            openRecipePageActivity(recipe);
        });
    }

    public void showAddToolDialog() {
        AddToolDialogFragment dialog = new AddToolDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddToolDialogFragment");
    }

    @Override
    public void applyToolName(String toolName) {
        toolsTextView.append(" "+ toolName);
    }

    public void showAddIngredientDialog() {
        AddIngredientDialogFragment dialog = new AddIngredientDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddIngredientDialogFragment");
    }

    @Override
    public void applyIngredientName(String ingredientName) {
        ingredientsTextView.append(" "+ingredientName);
    }

    public void openRecipePageActivity(Recipe recipe) {
        // parse Recipe object to Map
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> recipeMap = oMapper.convertValue(recipe, Map.class);


        // add recipe to database
        db.collection("recipes")
            .add(recipeMap)
            .addOnSuccessListener(documentReference -> {
                // update the newly added document to set its document ID - on the Java object and Firebase document reference
                recipe.setDocument_ID(documentReference.getId());
                documentReference.update("document_ID", documentReference.getId());

                Log.i(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                Intent intent = new Intent(getApplicationContext(), RecipePageActivity.class);
                intent.putExtra("create_recipe", recipe);
                startActivity(intent);
            })
            .addOnFailureListener(e -> Log.e(TAG, "Error adding document: ", e));


    }

    public void setToolbar() {
        Toolbar toolbarCreateRecipe = (Toolbar) findViewById(R.id.toolbarCreateRecipe);
        setSupportActionBar(toolbarCreateRecipe);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}