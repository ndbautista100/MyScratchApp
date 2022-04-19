package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
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
    Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_recipe);
        setToolbar();
        userTV = findViewById(R.id.userNameTV);
        profilePic = findViewById(R.id.creatorProfilePicture);
        saveButton = findViewById(R.id.saveRecipeButton);

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If its not their own recipe, the user can save it
                if (!recipe.getUser_ID().equals(userID)) {
                    Map<String, Object> savedRecipes = new HashMap<>();
                    savedRecipes.put("savedRecipes", FieldValue.arrayUnion(recipe.getDocument_ID()));
                    db.collection("profile").document(userID)
                            .update(savedRecipes);
                } else {
                    Toast.makeText(ViewOtherRecipe.this, "You created this wonderful recipe!", Toast.LENGTH_SHORT).show();
                }

            }
        });
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


    public void setToolbar() {
        toolbarViewRecipe = findViewById(R.id.toolbarViewRecipe);
        setSupportActionBar(toolbarViewRecipe);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
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
}
