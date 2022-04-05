package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import classes.Recipe;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView userRecipesRV;
    private FirestoreAdapter adapter;
    private final CollectionReference recipesRef = db.collection("recipes");
    private final PagingConfig pagingConfig = new PagingConfig(6, 3, false);

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

        FirestorePagingOptions<Recipe> firestorePagingOptions = new FirestorePagingOptions.Builder<Recipe>()
            .setLifecycleOwner(this)
            .setQuery(query, pagingConfig, Recipe.class)
            .build();

        adapter = new FirestoreAdapter(firestorePagingOptions, getApplicationContext());

        adapter.setOnItemClickListener((documentSnapshot, position) -> openOtherUserRecipe(documentSnapshot.getId()));

        adapter.addLoadStateListener(combinedLoadStates -> {
            LoadState refresh = combinedLoadStates.getRefresh();
            LoadState append = combinedLoadStates.getAppend();

            if (refresh instanceof LoadState.Error || append instanceof LoadState.Error) {
                // The previous load (either initial or additional) failed. Call
                // the retry() method in order to retry the load operation.
                Toast.makeText(MainActivity.this, "Load failed. Retrying...", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Load failed. Retrying...");
                adapter.retry();
            }

            if (refresh instanceof LoadState.Loading) {
                // The initial Load has begun
                Log.d(TAG, "Initial load has begun.");
            }

            if (append instanceof LoadState.Loading) {
                // The adapter has started to load an additional page
                Log.d(TAG, "Loading additional page...");
            }

            if (append instanceof LoadState.NotLoading) {
                LoadState.NotLoading notLoading = (LoadState.NotLoading) append;
                if (notLoading.getEndOfPaginationReached()) {
                    // The adapter has finished loading all of the data set
                    Log.d(TAG, "Finished loading all data.");
                    return null;
                }

                if (refresh instanceof LoadState.NotLoading) {
                    // The previous load (either initial or additional) completed
                    Log.d(TAG, "Previous load completed.");
                    return null;
                }
            }
            return null;
        });

        userRecipesRV.setHasFixedSize(true);
        userRecipesRV.setLayoutManager(new LinearLayoutManager(this));
        userRecipesRV.setAdapter(adapter);
    }

    public void openRecipePageActivity(String recipe_ID) {
        Intent intent = new Intent(getApplicationContext(), RecipePageActivity.class);
        intent.putExtra("open_recipe_from_id", recipe_ID);
        startActivity(intent);
    }

    public void openOtherUserRecipe(String recipe_ID){
        Intent intent = new Intent(getApplicationContext(), ViewOtherRecipe.class);
        intent.putExtra("open_recipe_from_id", recipe_ID);
        startActivity(intent);
    }
    public void openProfilePageActivity() {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }

    public void openSearchableActivity() {
        Intent intent = new Intent(this, SearchableActivity.class);
        startActivity(intent);
    }

    public void openScratchNotesActivity() {
        Intent intent = new Intent(this, ScratchNotesActivity.class);
        startActivity(intent);
    }

    public void openIngredientsList(){
        Intent intent = new Intent(this, IngredientListActivity.class);
        startActivity(intent);
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
    }

    // Opens the tool bar for the Home page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
        Home Page action bar options:
        - Search for a Recipe
        - Open Scratch Notes
        - Open Profile
    */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_search:
                openSearchableActivity();
                return true;
            case R.id.action_create:
                openScratchNotesActivity();
                return true;
            case R.id.action_explore:
                Intent intent = new Intent(getApplicationContext(), ExploreActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_profile:
                openProfilePageActivity();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_ingredients_list:
                openIngredientsList();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}