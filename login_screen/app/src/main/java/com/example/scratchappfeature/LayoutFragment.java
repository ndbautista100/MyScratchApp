
package com.example.scratchappfeature;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        TextView recipeName = view.findViewById(R.id.titleTextView);
        TextView recipeTools = view.findViewById(R.id.toolsTextView);
        TextView recipeIngredients = view.findViewById(R.id.ingredientsTextView);
        TextView recipeInstructions = view.findViewById(R.id.instructionsTextView);
        recipeName.setText(recipe.getName());
        recipeTools.setText(recipe.getTools());
        recipeIngredients.setText(recipe.getIngredients());
        recipeInstructions.setText(recipe.getDescription());

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