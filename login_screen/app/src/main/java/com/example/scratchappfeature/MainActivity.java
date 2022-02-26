package com.example.scratchappfeature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import classes.Recipe;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "myTag";
    private RecyclerView userRecipesRV;
    private FeedRecipeRVAdapter adapter;
    private boolean isLoading = false;
    ArrayList<Recipe> recipes_feed = new ArrayList<Recipe>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        // create a RecyclerView to store the main feed
        userRecipesRV = findViewById(R.id.userRecipesRecyclerView);
        userRecipesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        db.collection("recipes")
            //.whereEqualTo("user_ID", FirebaseAuth.getInstance().getCurrentUser().getUid())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe userRecipe = document.toObject(Recipe.class);
                            recipes_feed.add(userRecipe);
                        }
                        adapter = new FeedRecipeRVAdapter(recipes_feed, getApplicationContext());
                        userRecipesRV.setAdapter(adapter); // after passing this ArrayList to our adapter class we are setting our adapter to our RecyclerView
                        adapter.setOnItemClickListener(new FeedRecipeRVAdapter.OnItemClickListener() { // this is called when a recipe is clicked
                            @Override
                            public void onItemClick(int position) {
                                Recipe clickedRecipe = recipes_feed.get(position);
                                openRecipePageActivity(clickedRecipe.getDocument_ID());
                            }
                        });

                        userRecipesRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) userRecipesRV.getLayoutManager();
                                if(!isLoading) {
                                    if(linearLayoutManager != null & linearLayoutManager.findLastCompletelyVisibleItemPosition() == recipes_feed.size() - 1) {
                                        isLoading = true;
                                        getMoreData();
                                    }
                                }
                            }
                        });
                    } else {
                        Log.d("err", "Error getting documents: ", task.getException());
                    }
                }
            });
    }

    private void getMoreData() {
        recipes_feed.add(null);
        adapter.notifyItemInserted(recipes_feed.size() - 1);
        recipes_feed.remove(recipes_feed.size() - 1);

        int currentSize = recipes_feed.size();
        int nextSize = currentSize + 10;
        while(currentSize < nextSize) {
            // add more data - try to do this with database
            recipes_feed.add(new Recipe("new recipe #" + currentSize));
            currentSize++;
        }
        adapter.notifyDataSetChanged();
        isLoading = false;
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