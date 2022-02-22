
package com.example.scratchappfeature;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import classes.Recipe;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LayoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LayoutFragment extends Fragment {

    Recipe recipe;

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

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
        TextView recipeName = view.findViewById(R.id.titleTextView);
        TextView recipeTools = view.findViewById(R.id.toolsTextView);
        TextView recipeIngredients = view.findViewById(R.id.ingredientsTextView);
        TextView recipeInstructions = view.findViewById(R.id.instructionsTextView);

        recipeName.setText(recipe.getName());
        recipeTools.setText(recipe.getTools());
        recipeIngredients.setText(recipe.getIngredients());
        recipeInstructions.setText(recipe.getDescription());
        // Inflate the layout for this fragment
        return view;
    }
}