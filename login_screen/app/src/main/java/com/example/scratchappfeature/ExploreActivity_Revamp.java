package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import classes.Profile;
import classes.Recipe;

public class ExploreActivity_Revamp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "search";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText searchBox;
    private ImageButton searchBTN;
    private RecyclerView profiles_RV;
    private ProfileRVAdapter adapter;
    private Spinner spinner;
    private String[] spinnerOptions =
            {"User", "Recipe", "Ingredients", "Tools"};
    private int selectedOption = 0;
    private String userID;


    private Toolbar toolbarExplorePage;
    private ActionBar ab;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_revamp);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        searchBox = findViewById(R.id.searchBar);
        searchBTN = findViewById(R.id.searchButton);
        profiles_RV = findViewById(R.id.profileRV);
        profiles_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        spinner = findViewById(R.id.searchSpinner);
        spinner.setOnItemSelectedListener(this);
        setToolbar();
        ab.setTitle("Explore");

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                spinnerOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(searchBox.getText().toString());
            }
        });
    }

    public boolean search(String query){
        switch (selectedOption){
            case 0:
                searchUser(query);
                return true;
            case 1:
                searchRecipe(query);
                return true;
            case 2:
                searchIngredient(query);
                return true;
            case 3:
                searchTool(query);
                return true;
            default:
                return true;

        }
    }

    public void searchUser(String query){
        ArrayList<Profile> results = new ArrayList<Profile>();
        db.collection("profile").whereEqualTo("pname", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.i(TAG, "Success! " + FirebaseAuth.getInstance().getCurrentUser().getUid() + " => " + document.getData());

                            Profile profile = document.toObject(Profile.class);
                            results.add(profile);
                        }
                        adapter = new ProfileRVAdapter(results, getApplicationContext());
                        // after passing this ArrayList to our adapter class we are setting our adapter to our RecyclerView
                        profiles_RV.setAdapter(adapter);
                        // this is called when a recipe is clicked
                        adapter.setOnItemClickListener(position -> {
                            Profile clickedProfile = results.get(position);

                        });
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }

    public void searchRecipe(String query){
        ArrayList<Recipe> recipeArrayList = new ArrayList<>();
        db.collection("recipes")
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot recipe : task.getResult()) {
                        if (recipe.get("name").toString().toLowerCase().contains(query.toLowerCase())) {
                            recipeArrayList.add(recipe.toObject(Recipe.class));
                        }
                    }
                    SearchRecipeRVAdapter adapter = new SearchRecipeRVAdapter(getApplicationContext(), recipeArrayList);
                    profiles_RV.setAdapter(adapter);
                    adapter.setOnItemClickListener(position -> {
                       Recipe clickedRecipe = recipeArrayList.get(position);
                       openViewOtherRecipe(clickedRecipe.getDocument_ID());
                    });
                });
    }

    public void searchIngredient(String query){
        ArrayList<Recipe> recipeArrayList = new ArrayList<>();
        db.collection("recipes")
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot recipe : task.getResult()) {
                        if (recipe.get("ingredients").toString().toLowerCase().contains(query.toLowerCase())) {
                            recipeArrayList.add(recipe.toObject(Recipe.class));
                        }
                    }
                    SearchRecipeRVAdapter adapter = new SearchRecipeRVAdapter(getApplicationContext(), recipeArrayList);
                    profiles_RV.setAdapter(adapter);
                    adapter.setOnItemClickListener(position -> {
                        Recipe clickedRecipe = recipeArrayList.get(position);
                        openViewOtherRecipe(clickedRecipe.getDocument_ID());
                    });
                });
    }

    public void searchTool(String query){
        ArrayList<Recipe> recipeArrayList = new ArrayList<>();
        db.collection("recipes")
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot recipe : task.getResult()) {
                        if (recipe.get("tools").toString().toLowerCase().contains(query.toLowerCase())) {
                            recipeArrayList.add(recipe.toObject(Recipe.class));
                        }
                    }
                    SearchRecipeRVAdapter adapter = new SearchRecipeRVAdapter(getApplicationContext(), recipeArrayList);
                    profiles_RV.setAdapter(adapter);
                    adapter.setOnItemClickListener(position -> {
                        Recipe clickedRecipe = recipeArrayList.get(position);
                        openViewOtherRecipe(clickedRecipe.getDocument_ID());
                    });
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedOption = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setToolbar() {
        toolbarExplorePage = findViewById(R.id.toolbarExplorePage);
        setSupportActionBar(toolbarExplorePage);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void openViewOtherRecipe(String recipe_ID){
        Intent intent = new Intent(getApplicationContext(), ViewOtherRecipe.class);
        intent.putExtra("open_recipe_from_id", recipe_ID);
        startActivity(intent);
    }
}
