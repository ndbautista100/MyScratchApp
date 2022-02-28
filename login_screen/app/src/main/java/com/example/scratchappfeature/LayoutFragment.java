
package com.example.scratchappfeature;


import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import classes.Recipe;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LayoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LayoutFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String docId;
    Recipe recipe;

    public void setDocumentID(String docId) {
        this.docId = docId;
    }
    public void setRecipe (Recipe recipe) { this.recipe = recipe; }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private int layoutId;

    public LayoutFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param layoutId Parameter 1.
     * @return A new instance of fragment LayoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LayoutFragment newInstance(int layoutId) {
        LayoutFragment fragment = new LayoutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, layoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            layoutId = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(layoutId, container, false);
        ConstraintLayout mainBackground = view.findViewById(R.id.layoutConstraint);
        mainBackground.setBackgroundColor(recipe.getBackgroundColor());

        TextView recipeName = view.findViewById(R.id.titleTextView);

        TextView recipeTools = view.findViewById(R.id.toolsTextView);
        ScrollView toolsScrollView = view.findViewById(R.id.toolsScrollView);

        TextView recipeIngredients = view.findViewById(R.id.ingredientsTextView);
        ScrollView ingredientsScrollView = view.findViewById(R.id.ingredientsScrollView);

        TextView recipeInstructions = view.findViewById(R.id.instructionsTextView);
        ScrollView instructionsScrollView = view.findViewById(R.id.instructionsScrollView);

        recipeName.setText(recipe.getName());
        recipeName.setBackgroundColor(recipe.getTextBoxColor());

        recipeTools.setText(recipe.getTools());
        toolsScrollView.setBackgroundColor(recipe.getTextBoxColor());

        recipeIngredients.setText(recipe.getIngredients());
        ingredientsScrollView.setBackgroundColor(recipe.getTextBoxColor());

        recipeInstructions.setText(recipe.getDescription());
        instructionsScrollView.setBackgroundColor(recipe.getTextBoxColor());



        initRecyclerView(view);
        // Inflate the layout for this fragment
        return view;
    }



    private void initRecyclerView(View view){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.photosRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        LayoutRVAdapter adapter = new LayoutRVAdapter(getContext(),recipe.getImage_URL());
        recyclerView.setAdapter(adapter);

    }

}