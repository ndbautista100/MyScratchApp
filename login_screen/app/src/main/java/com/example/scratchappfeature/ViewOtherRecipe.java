package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import classes.Recipe;

public class ViewOtherRecipe extends AppCompatActivity {

    private final String TAG = "ViewOtherRecipe";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Toolbar toolbarViewRecipe;
    private ActionBar ab;
    Recipe recipe;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_recipe);
        //setToolbar();

        Intent intent = getIntent();
        if(intent.hasExtra("open_recipe_from_id")){
            String recipe_Id = intent.getStringExtra("open_recipe_from_id");
            DocumentReference docRef = db.collection("recipes").document(recipe_Id);
            docRef.get().addOnCompleteListener(task -> {
               if(task.isSuccessful()){
                   DocumentSnapshot document = task.getResult();
                   if(document.exists()){
                       Log.i(TAG, "Found Document");
                       recipe = document.toObject(Recipe.class);
                       populatePage(recipe);
                   } else {
                       Log.e(TAG, "No such Document");
                   }
               } else {
                   Log.e(TAG, "get failed with" + task.getException());
               }

            });
        }
    }




    private void populatePage(Recipe recipe){

        TextView userTV = findViewById(R.id.userNameTV);

        //I used layout one because not every recipe has a layout chosen
        inflateFragment(fragmentManager, R.layout.fragment_layout_one);
        //inflateFragment(fragmentManager, recipe.getLayoutChoice());

        userTV.setText(recipe.getUser_ID());

    }


    public void setToolbar() {
        toolbarViewRecipe = findViewById(R.id.toolbarProfilePage);
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
}
