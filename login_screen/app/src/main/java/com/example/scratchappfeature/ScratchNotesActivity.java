package com.example.scratchappfeature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import classes.Recipe;

public class ScratchNotesActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recipesRV;
    private RecipeRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_notes);
        Toolbar toolbarScratchNotes = (Toolbar) findViewById(R.id.toolbarScratchNotes);
        setSupportActionBar(toolbarScratchNotes);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // create a RecyclerView to store the recipe name TextViews
        recipesRV = findViewById(R.id.recipesRecyclerView);
        recipesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // create an ArrayList to store all of the user's recipes
        ArrayList<Recipe> user_recipes = new ArrayList<Recipe>();

        db.collection("recipes")
            .whereEqualTo("user_ID", FirebaseAuth.getInstance().getCurrentUser().getUid())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Log.d("Success", FirebaseAuth.getInstance().getCurrentUser().getUid() + " => " + document.getData());

                            Recipe recipe = document.toObject(Recipe.class);
                            user_recipes.add(recipe);
                        }
                        adapter = new RecipeRVAdapter(user_recipes, getApplicationContext());
                        // after passing this ArrayList to our adapter class we are setting our adapter to our RecyclerView
                        recipesRV.setAdapter(adapter);
                        // this is called when a recipe is clicked
                        adapter.setOnItemClickListener(new RecipeRVAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Recipe clickedRecipe = user_recipes.get(position);
                                openRecipePageActivity(clickedRecipe.getDocument_ID());
                            }
                        });
                    } else {
                        Log.d("err", "Error getting documents: ", task.getException());
                    }
                }
            });
    }

    public void openRecipePageActivity(String recipe_ID) {
        Intent intent = new Intent(getApplicationContext(), RecipePageActivity.class);
        intent.putExtra("open_recipe_from_id", recipe_ID);
        startActivity(intent);
    }

    public void openCreateRecipeActivity() {
        Intent intent = new Intent(this, CreateRecipeActivity.class);
        startActivity(intent);
    }

    public void searchForRecipe(MenuItem item) {
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE); // keyboard
        searchView.setQueryHint("Recipe name...");
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    // Opens the tool bar for the Scratch Notes page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrach_notes, menu);
        return true;
    }

    /*
        Scratch Notes action bar options:
        - Search for a Recipe
        - Create a Recipe
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_recipe:
                searchForRecipe(item);
                return true;
            case R.id.action_create_recipe:
                openCreateRecipeActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}