package com.example.scratchappfeature;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import classes.DataModal;

public class ScratchNotesActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView recipesTextView;
    private String recipesList = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_notes);
        Toolbar toolbarScratchNotes = (Toolbar) findViewById(R.id.toolbarScratchNotes);
        setSupportActionBar(toolbarScratchNotes);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);



        // use the database to display the user's recipes
        /*db.collection("recipes")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Success", document.getId() + " => " + document.getData());
                            // recipesList += document.getData().get("name") + "\n\n";
                        }
                    } else {
                        Log.d("Failure", "Error getting documents: ", task.getException());
                    }
                }
            });*/

        // recipesTextView.setText(recipesList);
        ListView coursesLV = findViewById(R.id.idLVCourses);
        ArrayList<DataModal> user_recipes = new ArrayList<DataModal>();
        db.collection("recipes")
                .whereEqualTo("user_ID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Success", FirebaseAuth.getInstance().getCurrentUser().getUid() + " => " + document.getData());
                                DataModal dataModal = document.toObject(DataModal.class);
                                user_recipes.add(dataModal);

                            }
                            RecipesLVAdapter adapter = new RecipesLVAdapter(getApplicationContext(), user_recipes);

                            // after passing this array list to our adapter
                            // class we are setting our adapter to our list view.
                            coursesLV.setAdapter(adapter);
                        } else {
                            Log.d("err", "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    public void openCreateRecipeActivity() {
        Intent intent = new Intent(this, CreateRecipeActivity.class);
        startActivity(intent);
    }

    /*
            Opens the tool bar for the Scratch Notes page
         */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrach_notes, menu);
        return true;
    }

    /*
        Recipe Page action bar options:
        - Search for a Recipe
        - Create a Recipe
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_recipe:
                return true;
            case R.id.action_create_recipe:
                openCreateRecipeActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}