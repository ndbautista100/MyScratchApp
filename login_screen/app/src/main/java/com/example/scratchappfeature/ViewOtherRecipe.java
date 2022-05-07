package com.example.scratchappfeature;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import classes.Recipe;

public class ViewOtherRecipe extends AppCompatActivity {

    private final String TAG = "ViewOtherRecipe";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Toolbar toolbarViewRecipe;
    private ActionBar ab;
    private FirebaseAuth fauth;
    private String userID;
    TextView userTV;
    ImageView profilePic;
    Recipe recipe;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_recipe);

        setToolbar();

        userTV = findViewById(R.id.userNameTV);
        profilePic = findViewById(R.id.creatorProfilePicture);

        fauth = FirebaseAuth.getInstance();
        userID = fauth.getCurrentUser().getUid();

        Intent intent = getIntent();
        if (intent.hasExtra("open_recipe_from_id")) {
            String recipe_Id = intent.getStringExtra("open_recipe_from_id");
            DocumentReference docRef = db.collection("recipes").document(recipe_Id);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "Found Document");
                        recipe = document.toObject(Recipe.class);
                        populatePage(recipe);
                        setBanner();
                    } else {
                        Log.e(TAG, "No such Document");
                    }
                } else {
                    Log.e(TAG, "get failed with" + task.getException());
                }

            });
        }
    }

    private boolean populatePage(Recipe recipe) {
        switch (recipe.getLayoutChoice()) {
            case 2:
                inflateFragment(fragmentManager, R.layout.fragment_layout_two);
                return true;
            case 3:
                inflateFragment(fragmentManager, R.layout.fragment_layout_three);
                return true;
            case 4:
                inflateFragment(fragmentManager, R.layout.fragment_layout_four);
                return true;
            default:
                inflateFragment(fragmentManager, R.layout.fragment_layout_one);
                return true;
        }
    }

    private void inflateFragment(FragmentManager fragmentManager, int layoutId) {
        LayoutFragment layout = LayoutFragment.newInstance(layoutId);
        layout.setDocumentID(recipe.getDocument_ID());
        layout.setRecipe(recipe);

        fragmentManager.beginTransaction()
                .replace(R.id.recipeFragmentView, layout)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();
    }

    //To fill out the banner at the bottom to show user, rating,
    //avatar picture, save, share, comments
    private void setBanner() {
        TextView userName = findViewById(R.id.userNameTV);
        ImageView profilePic = findViewById(R.id.creatorProfilePicture);
        ConstraintLayout background = findViewById(R.id.viewOtherRecipeBackground);

        db.collection("profile")
                .document(recipe.getUser_ID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userName.setText(document.getString("pname"));
                            Picasso.with(this)
                                    .load(document.getString("profileImageURL"))
                                    .into(profilePic);
                            background.setBackgroundColor(recipe.getBackgroundColor());
                            Log.i(TAG, "Found Document");
                        } else {
                            Log.i(TAG, "No Such Document");
                        }
                    } else {
                        Log.e(TAG, "Get failed with " + task.getException());

                    }
                });
    }

    public void shareUserRecipe() {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://myscratch.page.link/user-recipe/" + recipe.getDocument_ID()))
                .setDomainUriPrefix("https://myscratch.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.i(TAG, "Created Dynamic Link: " + dynamicLinkUri);

        String recipe_ID = dynamicLinkUri.toString().substring(dynamicLinkUri.toString().lastIndexOf("%2F") + 3);
        Log.d(TAG, "User recipe ID: " + recipe_ID);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString());
        sendIntent.putExtra(Intent.EXTRA_TITLE, "User recipe: " + recipe.getName());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void saveRecipe() {
        // If it's not their own recipe, the user can save it
        if (!recipe.getUser_ID().equals(userID)) {
            Map<String, Object> savedRecipes = new HashMap<>();
            savedRecipes.put("savedRecipes", FieldValue.arrayUnion(recipe.getDocument_ID()));

            db.collection("profile").document(userID)
                .update(savedRecipes)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ViewOtherRecipe.this, "Saved recipe!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ViewOtherRecipe.this, "Failed to save recipe.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to save recipe: " + task.getException());
                    }
                });

        } else {
            Toast.makeText(ViewOtherRecipe.this, "You created this wonderful recipe!", Toast.LENGTH_SHORT).show();
        }
    }

    public void setToolbar() {
        toolbarViewRecipe = findViewById(R.id.toolbarViewRecipe);
        setSupportActionBar(toolbarViewRecipe);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /*
        Opens the tool bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_other_recipe, menu);
        return true;
    }

    /*
        Other Recipe Page action bar options:
        - Share Recipe
        - Save Recipe
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.share_other_recipe:
                shareUserRecipe();
                return true;
            case R.id.save_other_recipe:
                saveRecipe();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
