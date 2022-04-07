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
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import classes.LoadingDialog;
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

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReference("images/recipes");
    private Uri imageLocationUri;

    private Toolbar toolbarScratchNotes;
    private ActionBar ab;

    // must be placed outside of onCreate- startActivityForResult is deprecated
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

        Intent intent = getIntent();

        if (intent.hasExtra("create_recipe")) { // getting recipe sent from CreateRecipeActivity
            recipe = (Recipe) intent.getSerializableExtra("create_recipe");
            populateRecipePage();

        } else if (intent.hasExtra("open_recipe_from_id")) { // getting recipe from EditRecipeActivity
            String recipe_ID = intent.getStringExtra("open_recipe_from_id");
            findRecipe(recipe_ID);

        } else {
            Log.w(TAG, "No intents.");
        }
    }

    // with the recipe ID, find the document and create a Recipe object
    public void findRecipe(String recipe_ID) {
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

    public void populateRecipePage() {
        descriptionTextView = findViewById(R.id.recipePageDescriptionTextView);
        toolsTextView = findViewById(R.id.toolsTextViewRecipePage);
        ingredientsTextView = findViewById(R.id.ingredientsTextViewRecipePage);

        // recipe images
        recipeImageView = findViewById(R.id.recipeImageView);
        addImageButton = findViewById(R.id.addImageButton);

        try {
            ab.setTitle(recipe.getName()); // set toolbar title using the recipe name
            descriptionTextView.setText(recipe.getDescription());
            toolsTextView.setText(recipe.getTools());
            ingredientsTextView.setText(recipe.getIngredients());
            addImageButton.setOnClickListener(view -> mGetContent.launch("image/*"));
            downloadImage();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

//    public void handleDynamicLink(Intent intent) {
//        FirebaseDynamicLinks.getInstance()
//            .getDynamicLink(intent)
//            .addOnSuccessListener(this, pendingDynamicLinkData -> {
//                Log.i(TAG, "We have a dynamic link!");
//                // Get deep link from result (may be null if no link is found)
//                Uri deepLink = null;
//                if (pendingDynamicLinkData != null) {
//                    deepLink = pendingDynamicLinkData.getLink();
//                }
//
//                // Handle the deep link. For example, open the linked
//                // content, or apply promotional credit to the user's
//                // account.
//                if (deepLink != null) {
//                    String deepLinkString = deepLink.toString();
//                    Log.i(TAG, "Here's the deep link URL:\n" + deepLinkString);
//
//                    String recipe_ID = deepLinkString.substring(deepLinkString.lastIndexOf('/') + 1);
//                    Log.i(TAG, "Recipe ID: " + recipe_ID);
//
//                    // Now get the recipe from the database with recipe_ID
//                    findRecipe(recipe_ID);
//                }
//            })
//            .addOnFailureListener(this, e -> Log.w(TAG, "getDynamicLink failed: ", e));
//    }

    public void shareRecipe() {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://myscratch.page.link/recipe/" + recipe.getDocument_ID()))
            .setDomainUriPrefix("https://myscratch.page.link")
            // Open links with this app on Android
            .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
            // Open links with com.example.ios on iOS
            .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
            .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.i(TAG, "Created Dynamic Link: " + dynamicLinkUri);

        String recipe_ID = dynamicLinkUri.toString().substring(dynamicLinkUri.toString().lastIndexOf("%2F") + 3);
        Log.d(TAG, "Recipe ID: " + recipe_ID);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString());
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Recipe: " + recipe.getName());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void uploadImage() {
        Log.d(TAG, "Recipe has an image: " + recipe.getImageName());
        if(recipe.getImageName() != null) { // if recipe already has an image
            // delete that image from Firebase Storage
            StorageReference recipeImageRef = storageReference.child(recipe.getImageName());
            Log.d(TAG, "Deleting image: " + recipe.getImageName());
            recipeImageRef.delete()
                .addOnSuccessListener(unused1 -> Log.i(TAG, "Successfully deleted image: " + recipe.getImageName()))
                .addOnFailureListener(e -> Log.e(TAG, e.toString()));
        }

        try { // uploading the new image
            if(imageLocationUri != null) {
                LoadingDialog loadingDialog = new LoadingDialog(RecipePageActivity.this);
                loadingDialog.startLoadingDialog();

                String imageName = recipe.getName() + "_" + UUID.randomUUID().toString() + "." + getExtension(imageLocationUri);
                StorageReference imageReference = storageReference.child(imageName);

                UploadTask uploadTask = imageReference.putFile(imageLocationUri);
                uploadTask.continueWithTask(task -> {
                    if(!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        //store image
                        recipe.setImage_URL(task.getResult().toString());
                        recipe.setImageName(imageName);
                        db.collection("recipes")
                            .document(recipe.getDocument_ID())
                            .update("image_URL", recipe.getImage_URL(),
                                    "imageName", imageName)
                            .addOnCompleteListener(task1 -> {
                                loadingDialog.dismissDialog();
                                Toast.makeText(RecipePageActivity.this, "Recipe image uploaded!", Toast.LENGTH_SHORT).show();
                            })
                        .addOnFailureListener(e -> {
                            loadingDialog.dismissDialog();
                            Toast.makeText(RecipePageActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                        });
                    } else if(!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
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
                shareRecipe();
                return true;
            case R.id.action_edit_recipe:
                openEditRecipeActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}