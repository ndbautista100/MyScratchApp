package com.example.scratchappfeature;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.UUID;

import classes.Recipe;

public class RecipePageActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Recipe recipe;
    private ImageView recipeImageView;
    private Button addImageButton;
    private TextView descriptionTextView;
    private TextView toolsTextView;
    private TextView ingredientsTextView;

    private Button nextButton;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri imageLocationUri;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

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
        Toolbar toolbarScratchNotes = (Toolbar) findViewById(R.id.toolbarRecipePage);
        setSupportActionBar(toolbarScratchNotes);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/");

        // getting recipe sent from CreateRecipeActivity
        // create different scenarios for opening from CreateRecipe and EditRecipe
        Intent intent = getIntent();

        if(intent.hasExtra("create_recipe")) {
            recipe = (Recipe) intent.getSerializableExtra("create_recipe");
            populateRecipePage(ab);

        } else if (intent.hasExtra("open_recipe_from_id")) {
            String recipe_ID = intent.getStringExtra("open_recipe_from_id");

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
                            populateRecipePage(ab);

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

    public void populateRecipePage(ActionBar ab) {
        ab.setTitle(recipe.getName()); // set toolbar title using the recipe name

        descriptionTextView = (TextView) findViewById(R.id.recipePageDescriptionTextView);
        descriptionTextView.setText(recipe.getDescription());
        toolsTextView = (TextView) findViewById(R.id.toolsTextViewRecipePage);
        toolsTextView.setText(recipe.getTools());
        ingredientsTextView = (TextView) findViewById(R.id.ingredientsTextViewRecipePage);
        ingredientsTextView.setText(recipe.getIngredients());

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                downloadImage();
                openCustomizeRecipeFeatureActivity(recipe);
            }
        });
        // recipe images
        recipeImageView = (ImageView) findViewById(R.id.recipeImageView);
        addImageButton = (Button) findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(view -> mGetContent.launch("image/*"));


    }

    public void uploadImage() {
        try {
            if(imageLocationUri != null) {
                String imageName = recipe.getName() + "_" + UUID.randomUUID().toString() + "." + getExtension(imageLocationUri);
                StorageReference imageReference = storageReference.child(imageName);

                UploadTask uploadTask = imageReference.putFile(imageLocationUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()) {
                            //store image
                            recipe.setImage_URL(task.getResult().toString());
                            db.collection("recipes")
                                .document(recipe.getDocument_ID())
                                .update("image_URL", recipe.getImage_URL())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(RecipePageActivity.this, "Recipe image uploaded!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RecipePageActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else if(!task.isSuccessful()) {
                            Toast.makeText(RecipePageActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
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

            downloadRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String downloadUrl = documentSnapshot.getString("image_URL");

                    // Glide makes it easy to load images into ImageViews
                    if(downloadUrl != null) {
                        Glide.with(RecipePageActivity.this)
                            .load(downloadUrl)
                            .into(recipeImageView);
                    }
                        recipe.setImage_URL(downloadUrl);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RecipePageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(RecipePageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void openCustomizeRecipeFeatureActivity(Recipe recipe) {
        //Parse Recipe object to map
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> recipeMap = oMapper.convertValue(recipe, Map.class);

        Intent intent = new Intent(getApplicationContext(), CustomizeRecipeFeature.class);
        intent.putExtra("customize_recipe", recipe.getDocument_ID());
        startActivity(intent);

    }
}