package com.example.scratchappfeature;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import classes.AddIngredientListDialog;
import classes.Profile;

public class IngredientListActivity extends AppCompatActivity implements AddIngredientListDialog.AddIngredientListDialogListener {

    private static final String TAG = "IngredientsListActivity";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;
    private Profile mProfile;
    private FirebaseAuth fauth;
    private ArrayList<String> ingredientList;
    private RecyclerView ingredientsRV;
    private IngredientsRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients_list);
        setToolbar();

        fauth = FirebaseAuth.getInstance();
        userID = fauth.getCurrentUser().getUid();
        ingredientsRV = findViewById(R.id.ingredientsListRecyclerView);
        ingredientsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        getIngredients();

    }

    private void getIngredients() {
        ingredientList = new ArrayList<>();
        db.collection("profile")
                .whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            mProfile = document.toObject(Profile.class);
                            //Once we have the user's profile, we can now get their saved recipes
                            //and add them to the recipe arraylist
                            if (mProfile.getSavedIngredients() == null ||
                                    mProfile.getSavedIngredients().size() == 0) {
                                String emptyList =
                                        "Lets add some ingredients" +
                                                "/Get started by tapping on the +";
                                ingredientList.add(emptyList);
                            } else {
                                for (String ingredient : mProfile.getSavedIngredients()) {
                                    ingredientList.add(ingredient);
                                }
                            }

                            adapter = new IngredientsRVAdapter(getApplicationContext(), ingredientList);
                            adapter.setProfileID(userID);
                            // after passing this ArrayList to our adapter class we are setting our adapter to our RecyclerView
                            ingredientsRV.setAdapter(adapter);

                        }
                    }
                });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarIngredientsList);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ingredient_list, menu);
        return true;
    }

        @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_ingredient_to_list:
                //Make Dialog
                showAddIngredientDialog();
                Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void showAddIngredientDialog() {
        AddIngredientListDialog dialog = new AddIngredientListDialog();
        dialog.show(getSupportFragmentManager(), "AddIngredientListDialog");
    }

    public void saveIngredient(String ingredientNameAndCategory) {
        Map<String, Object> savedIngredients = new HashMap<>();
        savedIngredients.put("savedIngredients", FieldValue.arrayUnion(ingredientNameAndCategory));
        db.collection("profile").document(userID)
                .update(savedIngredients);

        getIngredients();
    }


}
