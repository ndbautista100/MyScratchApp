package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import classes.Profile;
import classes.Recipe;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowUserRecipes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowUserRecipes extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "ShowUserRecipesFragment";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference profilesCollection = db.collection("profile");
    private String userID;
    private Profile profile;
    private RecyclerView userRecipesRV;
    private FirestoreAdapter adapter;
    private final CollectionReference recipesRef = db.collection("recipes");
    private final PagingConfig pagingConfig = new PagingConfig(6, 3, false);

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShowUserRecipes() {
        // Required empty public constructor
    }

    public static ShowUserRecipes newInstance(){
        ShowUserRecipes fragment =  new ShowUserRecipes();
        return fragment;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_user_recipes, container, false);

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        userRecipesRV.setLayoutManager(linearLayoutManager);
        userRecipesRV = view.findViewById(R.id.recipeRecycler123);
        showProfileRecipes();

        return view;
    }

    public void showProfileRecipes() {
        Query query = recipesRef.whereEqualTo("user_ID", userID);

        FirestorePagingOptions<Recipe> firestorePagingOptions = new FirestorePagingOptions.Builder<Recipe>()
                .setLifecycleOwner(this)
                .setQuery(query, pagingConfig, Recipe.class)
                .build();

        adapter = new FirestoreAdapter(firestorePagingOptions, getActivity().getApplicationContext());

        adapter.setOnItemClickListener((documentSnapshot, position) -> openRecipePageActivity(documentSnapshot.getId()));

        adapter.addLoadStateListener(combinedLoadStates -> {
            LoadState refresh = combinedLoadStates.getRefresh();
            LoadState append = combinedLoadStates.getAppend();

            if (refresh instanceof LoadState.Error || append instanceof LoadState.Error) {
                // The previous load (either initial or additional) failed. Call
                // the retry() method in order to retry the load operation.
                Toast.makeText(getActivity(), "Load failed. Retrying...", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Load failed. Retrying...");
                adapter.retry();
            }

            if (refresh instanceof LoadState.Loading) {
                // The initial Load has begun
                Log.d(TAG, "Initial load has begun.");
            }

            if (append instanceof LoadState.Loading) {
                // The adapter has started to load an additional page
                Log.d(TAG, "Loading additional page...");
            }

            if (append instanceof LoadState.NotLoading) {
                LoadState.NotLoading notLoading = (LoadState.NotLoading) append;
                if (notLoading.getEndOfPaginationReached()) {
                    // The adapter has finished loading all of the data set
                    Log.d(TAG, "Finished loading all data.");
                    return null;
                }

                if (refresh instanceof LoadState.NotLoading) {
                    // The previous load (either initial or additional) completed
                    Log.d(TAG, "Previous load completed.");
                    return null;
                }
            }
            return null;
        });
        adapter.startListening();
        userRecipesRV.setHasFixedSize(false);
        userRecipesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        userRecipesRV.setAdapter(adapter);
    }
    public void openRecipePageActivity(String recipe_ID) {
        Intent intent = new Intent(getActivity().getApplicationContext(), RecipePageActivity.class);
        intent.putExtra("open_recipe_from_id", recipe_ID);
        startActivity(intent);
    }
}