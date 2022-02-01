package com.example.scratchappfeature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import classes.Recipe;

public class RecipePageActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Recipe recipe;
    ImageView recipeImages;
    Button showImagesButton;
    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_page);
        Toolbar toolbarScratchNotes = (Toolbar) findViewById(R.id.toolbarRecipePage);
        setSupportActionBar(toolbarScratchNotes);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // getting recipe
        Intent intent = getIntent();
        recipe = (Recipe) intent.getSerializableExtra("recipe");

        ab.setTitle(recipe.getName()); // set toolbar title

        Map<String, Object> recipeMap = new HashMap<>();
        recipeMap.put("name", recipe.getName());

        // database snippet


        showImagesButton = (Button) findViewById(R.id.showImages);
        recipeImages = (ImageView) findViewById(R.id.recipeImages);

        showImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

    }

    void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share_recipe:
                return true;
            case R.id.action_edit_recipe:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}