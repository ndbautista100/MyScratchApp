package com.example.scratchappfeature;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.io.LineReader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import classes.Recipe;
import top.defaults.colorpicker.ColorPickerPopup;

public class CustomizeRecipeFeature extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button fontBtn;
    private Button colorBtn;
    private Button backgroundBtn;
    private Button layoutBtn;
    private Button saveBtn;

    private HorizontalScrollView layoutScrollView;
    private RecyclerView photosRecyclerView;
    private LayoutRVAdapter adapter;

    private Button layoutOneBtn;
    private Button layoutTwoBtn;
    private Button layoutThreeBtn;
    private Button layoutFourBtn;
    private Button layoutFiveBtn;
    private Button layoutSixBtn;

    private int textBoxColor;
    private int backgroundColor;

    FragmentManager fragmentManager = getSupportFragmentManager();
    private Recipe recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //If recipe is getting passed through activities
        Intent intent = getIntent();
        if (intent.hasExtra("customize_recipe")) {
            String docId = (String) intent.getStringExtra("customize_recipe");
            DocumentReference docRef = db.collection("recipes").document(docId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Success", "Found document!");

                            recipe = document.toObject(Recipe.class);
                            Toast.makeText(CustomizeRecipeFeature.this, recipe.getName(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_recipe_features);

        fontBtn = findViewById(R.id.fontButton);
        colorBtn = findViewById(R.id.colorButton);
        backgroundBtn = findViewById(R.id.backgroundButton);
        layoutBtn = findViewById(R.id.layoutButton);
        layoutOneBtn = findViewById(R.id.layoutOneButton);
        layoutTwoBtn = findViewById(R.id.layoutTwoButton);
        layoutThreeBtn = findViewById(R.id.layoutThreeButton);
        layoutFourBtn = findViewById(R.id.layoutFourButton);
        layoutScrollView = findViewById(R.id.layoutHorizontalScrollBar);
        saveBtn = findViewById(R.id.layoutSaveButton);
        photosRecyclerView = findViewById(R.id.photosRecyclerView);

        //Finished picking the customized options
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Save Recipe
                startActivity(new Intent(getApplicationContext(), ScratchNotesActivity.class));
                finish();
            }
        });

        fontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutScrollView.setVisibility(View.INVISIBLE);
            }
        });

        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutScrollView.setVisibility(View.INVISIBLE);
            }
        });

        backgroundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutScrollView.setVisibility(View.INVISIBLE);
            }
        });

        layoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutScrollView.setVisibility(View.VISIBLE);
            }
        });

        layoutOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateFragment(fragmentManager, R.layout.fragment_layout_one);
            }
        });

        layoutTwoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateFragment(fragmentManager, R.layout.fragment_layout_two);
            }
        });

        layoutThreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateFragment(fragmentManager, R.layout.fragment_layout_three);
            }
        });

        layoutFourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateFragment(fragmentManager, R.layout.fragment_layout_four);
            }
        });


        colorBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(CustomizeRecipeFeature.this)
                        .initialColor(Color.RED)
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle("Select")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver(){
                            @Override
                            public void
                            onColorPicked(int color){
                                recipe.setTextBoxColor(color);
                            }
                        });
            }
        });

        backgroundBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(CustomizeRecipeFeature.this)
                        .initialColor(Color.RED)
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle("Select")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver(){
                            @Override
                            public void
                            onColorPicked(int color){
                                recipe.setBackgroundColor(color);
                            }
                        });
            }
        });


    }

    //Inflate the fragments with the layout we want
    private void inflateFragment(FragmentManager fragmentManager, int layoutId) {
        LayoutFragment layout = LayoutFragment.newInstance(layoutId);
        layout.setDocumentID(recipe.getDocument_ID());
        layout.setRecipe(recipe);

        fragmentManager.beginTransaction()
                .replace(R.id.layoutFragmentView, layout)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();
    }

}