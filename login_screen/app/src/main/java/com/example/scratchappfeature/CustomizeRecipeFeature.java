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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.io.LineReader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private Button layoutOneBtn;
    private Button layoutTwoBtn;
    private Button layoutThreeBtn;
    private Button layoutFourBtn;
    private Button layoutFiveBtn;
    private Button layoutSixBtn;

    private String fontSelection;

    private ItemViewModel viewModel;

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

        //Finished picking the customized options
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Update recipe before moving on
                updateRecipe(recipe);
                //Move to Scratch notes
                startActivity(new Intent(getApplicationContext(), ScratchNotesActivity.class));
                finish();
            }
        });

        //Show font fragment
        fontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutScrollView.setVisibility(View.INVISIBLE);
                FontFragment fontFragment = FontFragment.newInstance(fontSelection);
                fragmentManager.beginTransaction()
                        .replace(R.id.layoutFragmentView, fontFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("name")
                        .commit();
            }
        });

        //Font selection is being passed from fragment to the activity
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(this, item -> {
            fontSelection = item;
            recipe.setFontFamily(fontSelection);
            Toast.makeText(this, fontSelection, Toast.LENGTH_SHORT).show();
        });

        //Show layout buttons to be selected from
        layoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutScrollView.setVisibility(View.VISIBLE);
                inflateFragment(fragmentManager, R.layout.fragment_layout_one);

            }
        });

        layoutButtonOnClickListener(layoutOneBtn , R.layout.fragment_layout_one);
        layoutButtonOnClickListener(layoutTwoBtn , R.layout.fragment_layout_two);
        layoutButtonOnClickListener(layoutThreeBtn , R.layout.fragment_layout_three);
        layoutButtonOnClickListener(layoutFourBtn , R.layout.fragment_layout_four);

        //Color wheel for color
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutScrollView.setVisibility(View.INVISIBLE);
                clearFragments();
                new ColorPickerPopup.Builder(CustomizeRecipeFeature.this)
                        .initialColor(Color.WHITE)
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle("Select")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void
                            onColorPicked(int color) {
                                recipe.setTextBoxColor(color);
                            }
                        });
            }
        });

        backgroundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutScrollView.setVisibility(View.INVISIBLE);
                clearFragments();
                new ColorPickerPopup.Builder(CustomizeRecipeFeature.this)
                        .initialColor(Color.WHITE)
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle("Select")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void
                            onColorPicked(int color) {
                                recipe.setBackgroundColor(color);
                            }
                        });
            }
        });


    }

    //Clear all the fragments in container to leave it blank
    private void clearFragments() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
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

    //Saving the recipe details to the database
    public void updateRecipe(Recipe recipe) {
        DocumentReference docRef = db.collection("recipes").document(recipe.getDocument_ID());
        Log.d("Found document", docRef.getId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Success", "Found Document");
                        docRef.update("backgroundColor", recipe.getBackgroundColor(),
                                "fontFamily", recipe.getFontFamily(),
                                "layoutChoice", recipe.getLayoutChoice(),
                                "textBoxColor", recipe.getTextBoxColor()
                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Success", "DocumentSnapshot successfully updated");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Fail", "Error updating document", e);
                            }
                        });

                    } else {
                        Log.d("Fail", "No such document");
                    }
                } else {
                    Log.d("Fail", "failed with " + task.getException());
                }
            }
        });
    }


    private void layoutButtonOnClickListener(Button button, int layoutId){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe.setLayoutChoice(layoutId);
                inflateFragment(fragmentManager, layoutId);
            }
        });

    }
}