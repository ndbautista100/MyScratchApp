package com.example.scratchappfeature;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import classes.Recipe;

public class RecipePageActivity extends AppCompatActivity {
    private static final String TAG = "RecipePageActivity";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Recipe recipe;
    private ImageView recipeImageView;
    private Button addImageButton;
    private TextView descriptionTextView;
    private TextView toolsTextView;
    private TextView ingredientsTextView;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri imageLocationUri;

    private Toolbar toolbarScratchNotes;
    private ActionBar ab;

    // must be placed outside of onCreate
    // startActivityForResult is deprecated
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result != null) {
                recipeImageView.setImageURI(result);
                imageLocationUri = result;

                uploadImage();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_page);

        setToolbar();

        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/");

        // getting recipe sent from CreateRecipeActivity
        // create different scenarios for opening from CreateRecipe and EditRecipe
        Intent intent = getIntent();

        if(intent.hasExtra("create_recipe")) {
            recipe = (Recipe) intent.getSerializableExtra("create_recipe");
            populateRecipePage();

        } else if (intent.hasExtra("open_recipe_from_id")) {
            String recipe_ID = intent.getStringExtra("open_recipe_from_id");

            // with the recipe ID, find the document and create a Recipe object
            DocumentReference docRef = db.collection("recipes").document(recipe_ID);
            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Log.i(TAG, "Found document!");

                        recipe = document.toObject(Recipe.class);
                        populateRecipePage();

                    } else {
                        Log.e(TAG, "No such document.");
                    }
                } else {
                    Log.e(TAG, "get failed with" + task.getException());
                }
            });
        }
    }

    public void populateRecipePage() {
        ab.setTitle(recipe.getName()); // set toolbar title using the recipe name

        descriptionTextView = (TextView) findViewById(R.id.recipePageDescriptionTextView);
        descriptionTextView.setText(recipe.getDescription());
        toolsTextView = (TextView) findViewById(R.id.toolsTextViewRecipePage);
        toolsTextView.setText(recipe.getTools());
        ingredientsTextView = (TextView) findViewById(R.id.ingredientsTextViewRecipePage);
        ingredientsTextView.setText(recipe.getIngredients());

        // recipe images
        recipeImageView = (ImageView) findViewById(R.id.recipeImageView);
        addImageButton = (Button) findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(view -> mGetContent.launch("image/*"));

        downloadImage();
    }

    public void uploadImage() {
        try {
            if(imageLocationUri != null) {
                String imageName = recipe.getName() + "_" + UUID.randomUUID().toString() + "." + getExtension(imageLocationUri);
                StorageReference imageReference = storageReference.child(imageName);

                UploadTask uploadTask = imageReference.putFile(imageLocationUri);
                uploadTask.continueWithTask(task -> {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        //store image
                        recipe.setImage_URL(task.getResult().toString());
                        db.collection("recipes")
                            .document(recipe.getDocument_ID())
                            .update("image_URL", recipe.getImage_URL())
                            .addOnCompleteListener(task1 -> Toast.makeText(RecipePageActivity.this, "Recipe image uploaded!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(RecipePageActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show());

                    } else if(!task.isSuccessful()) {
                        Toast.makeText(RecipePageActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(RecipePageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

        } catch (Exception e) {
            return null;
        }
    }

    public void downloadImage() {
        try {
            // get the recipe document from the database
            DocumentReference downloadRef = db.collection("recipes").document(recipe.getDocument_ID());

            downloadRef.get().addOnSuccessListener(documentSnapshot -> {
                String downloadUrl = documentSnapshot.getString("image_URL");

                // Glide makes it easy to load images into ImageViews
                if(downloadUrl != null) {
                    Glide.with(RecipePageActivity.this)
                        .load(downloadUrl)
                        .into(recipeImageView);
                }

            }).addOnFailureListener(e -> Toast.makeText(RecipePageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(RecipePageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void openEditRecipeActivity() {
        Intent intent = new Intent(getApplicationContext(), EditRecipeActivity.class);
        intent.putExtra("edit_recipe", recipe.getDocument_ID());
        startActivity(intent);
    }

    public void setToolbar() {
        toolbarScratchNotes = findViewById(R.id.toolbarRecipePage);
        setSupportActionBar(toolbarScratchNotes);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
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