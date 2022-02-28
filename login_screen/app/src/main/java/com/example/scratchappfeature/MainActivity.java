package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import classes.Recipe;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView userRecipesRV;
    private FirestoreAdapter adapter;
    private final CollectionReference recipesRef = db.collection("recipes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();

        userRecipesRV = findViewById(R.id.userRecipesRecyclerView); // create a RecyclerView to store the main feed

        showRecipes();
    }

    public void showRecipes() {
        Query query = recipesRef.orderBy("name", Query.Direction.ASCENDING); // order by rating once ratings are implemented

        PagingConfig config = new PagingConfig(6, 3, false);

        FirestorePagingOptions<Recipe> options = new FirestorePagingOptions.Builder<Recipe>()
            .setLifecycleOwner(this)
            .setQuery(query, config, Recipe.class)
            .build();

        adapter = new FirestoreAdapter(options, getApplicationContext());
        adapter.setOnItemClickListener((documentSnapshot, position) -> openRecipePageActivity(documentSnapshot.getId()));

        userRecipesRV.setHasFixedSize(true);
        userRecipesRV.setLayoutManager(new LinearLayoutManager(this));
        userRecipesRV.setAdapter(adapter);
    }

    public void openRecipePageActivity(String recipe_ID) {
        Intent intent = new Intent(getApplicationContext(), RecipePageActivity.class);
        intent.putExtra("open_recipe_from_id", recipe_ID);
        startActivity(intent);
    }

    public void openScratchNotesActivity() {
        Intent intent = new Intent(this, ScratchNotesActivity.class);
        startActivity(intent);
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_create:
                openScratchNotesActivity();
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_profile:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}