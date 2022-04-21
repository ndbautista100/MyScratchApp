package com.example.scratchappfeature;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
    private Spinner spinner;
    private String[] spinnerOptions =
            {"User", "Recipe", "Ingredients", "Tools"};
    private int selectedOption = 0;
    private String userID;

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

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                spinnerOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        searchBox.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    search(searchBox.getText().toString());
                    return true;
                }
                return false;
            }
        });

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(searchBox.getText().toString());
            }
        });


        Intent intent = getIntent();
        if(intent.hasExtra("open_explore_from_ingredients")){
            String name = intent.getStringExtra("open_explore_from_ingredients");
            selectedOption = 2;
            spinner.setSelection(2);
            searchBox.setText(name);
            search(name);
        }

        if(intent.hasExtra("open_explore_from_tools")){
            String name = intent.getStringExtra("open_explore_from_tools");
            selectedOption = 3;
            spinner.setSelection(3);
            searchBox.setText(name);
            search(name);
        }
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
        ArrayList<Profile> profilesList = new ArrayList<>();
        db.collection("profile")
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot profile : task.getResult()) {
                        if (profile.get("pname").toString().toLowerCase().contains(query.toLowerCase())) {
                            profilesList.add(profile.toObject(Profile.class));
                        }
                    }
                    ProfileRVAdapter adapter = new ProfileRVAdapter(profilesList, getApplicationContext());
                    profiles_RV.setAdapter(adapter);
                    adapter.setOnItemClickListener(position -> {
                        Profile clickedProfile = profilesList.get(position);
                    });
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

    public void openViewOtherRecipe(String recipe_ID){
        Intent intent = new Intent(getApplicationContext(), ViewOtherRecipe.class);
        intent.putExtra("open_recipe_from_id", recipe_ID);
        startActivity(intent);
    }
}
