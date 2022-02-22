package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import org.w3c.dom.Text;

import classes.Recipe;

public class CustomizeRecipeFeature extends AppCompatActivity {

    private Button fontBtn;
    private Button colorBtn;
    private Button backgroundBtn;
    private Button layoutBtn;
    private Button saveBtn;

    private HorizontalScrollView layoutScrollView;

    private Button layoutOneBtn;
    private Button layoutTwoBtn;
    private Button layoutThreeBtn;
    private Button layoutFourBtn;
    private Button layoutFiveBtn;
    private Button layoutSixBtn;

    FragmentManager fragmentManager = getSupportFragmentManager();
    private Recipe recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //If recipe is getting passed through activities
        Intent intent = getIntent();
        if (intent.hasExtra("customize_recipe")) {
            recipe = (Recipe) intent.getSerializableExtra("customize_recipe");
            populateRecipe(recipe);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_recipe_features);

        fontBtn = findViewById(R.id.fontButton);
        colorBtn = findViewById(R.id.colorButton);
        backgroundBtn = findViewById(R.id.backgroundButton);
        layoutBtn = findViewById(R.id.layoutButton);
        layoutOneBtn = findViewById(R.id.layoutOneButton);
        layoutScrollView = findViewById(R.id.layoutHorizontalScrollBar);
        saveBtn = findViewById(R.id.layoutSaveButton);


        //Finished picking the customized options
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    }

    //Inflate the fragments with the layout we want
    private void inflateFragment(FragmentManager fragmentManager, int layoutId) {
        LayoutFragment layout = LayoutFragment.newInstance(layoutId);
        layout.setRecipe(recipe);

        fragmentManager.beginTransaction()
                .replace(R.id.layoutFragmentView, layout)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();
    }

    //Set recipe values for fragments
    private void populateRecipe(Recipe recipe) {
        this.recipe.setName(recipe.getName());
        this.recipe.setDescription(recipe.getDescription());
        this.recipe.setIngredients(recipe.getIngredients());
        this.recipe.setTools(recipe.getTools());
        this.recipe.setUser_ID(recipe.getUser_ID());
        this.recipe.setDocument_ID(recipe.getDocument_ID());
        this.recipe.setLayoutChoice(recipe.getLayoutChoice());
        this.recipe.setImage_URL(recipe.getImage_URL());

    }
}