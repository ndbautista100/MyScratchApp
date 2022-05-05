package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import android.widget.FrameLayout;


import classes.Profile;
import classes.Recipe;

public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfilePage";
    private TextView displayNameTextView;
    private TextView bioTextView;
    private TextView favoriteFoodTextView;
    private ImageView profileImageView;
    private ImageView bannerImageView;
    private ImageButton followImageButton;
    private CardView followCardView;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference profilesCollection = db.collection("profile");
    private String userID;
    private Profile profile;

    private RecyclerView recipeRV;
    private FirestoreAdapter adapter;
    private final CollectionReference recipesRef = db.collection("recipes");
    private final PagingConfig pagingConfig = new PagingConfig(6, 3, false);

    private Toolbar toolbarProfilePage;
    private ActionBar ab;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page); //change to correct activity if needed

        profileImageView = findViewById(R.id.profilePictureImageView);
        bannerImageView = findViewById(R.id.bannerImageView);

        followCardView = findViewById(R.id.followCardView);
        followCardView.setVisibility(View.GONE);
        followImageButton = findViewById(R.id.followImageButton);
        followImageButton.setVisibility(View.GONE);

        displayNameTextView = findViewById(R.id.nameTextView);
        bioTextView = findViewById(R.id.bioTextView);
        favoriteFoodTextView = findViewById(R.id.favoriteFoodTextView);
        
        userID = auth.getCurrentUser().getUid();

        //recipeRV = findViewById(R.id.recipeRecycler);


        //Experiment with tablayout.
        frameLayout = findViewById(R.id.frlayout);

        tabLayout = findViewById(R.id.tlayout);

        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Recipes"));
//        TabLayout.Tab firstTab = tabLayout.newTab();
//        firstTab.setText("Posts");
//        tabLayout.addTab(firstTab);
//
//        TabLayout.Tab secondTab = tabLayout.newTab();
//        secondTab.setText("Recipes");
//        tabLayout.addTab(secondTab);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()){
                    case 0:
                        //fragment = new ActivityFeed();
                        break;
                    case 1:
                        ShowUserRecipesFragment fragment2 = ShowUserRecipesFragment.newInstance();
                        fragment2.setUserID(userID);
                        fragment = fragment2;
                        break;
                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frlayout, fragment)
                    .setReorderingAllowed(true)
                    .addToBackStack("name")
                    .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

      //  MainAdapter adapter = new MainAdapter(getSupportFragmentManager());

//        showProfileRecipes();

        DocumentReference docRef = db.collection("profile").document(userID);
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    Log.i(TAG, "Found document!");

                    profile = document.toObject(Profile.class);

                    setToolbar();
                    ab.setTitle(profile.getpname());

                    displayNameTextView.setText(profile.getpname());
                    bioTextView.setText(profile.getbio());
                    favoriteFoodTextView.setText(profile.getfavoritefood());

                    if(!profile.getUserID().equals(auth.getCurrentUser().getUid())) { // if the user is not yourself, show the follow button
                        Log.d(TAG, "statement = " + profile.getUserID().equals(auth.getCurrentUser().getUid()));
                        followCardView.setVisibility(View.VISIBLE);
                        followImageButton.setVisibility(View.VISIBLE);

                        followImageButton.setOnClickListener(view -> {
                            // follow user method
                        });
                    }

                    downloadProfileImage();
                    downloadBannerImage();

                } else {
                    Log.e(TAG, "No such document.");
                }
            } else {
                Log.e(TAG, "get failed with" + task.getException());
            }
        });
    }

    public void downloadProfileImage(){
        try {
            // get the recipe document from the database
            DocumentReference downloadRef = profilesCollection.document(userID);

            downloadRef.get().addOnSuccessListener(documentSnapshot -> {
                String downloadUrl = documentSnapshot.getString("profileImageURL");
                Log.d(TAG, "profileImageURL: " + downloadUrl);

                // Glide makes it easy to load images into ImageViews
                if(downloadUrl != null) {
                    Glide.with(ProfilePage.this)
                        .load(downloadUrl)
                        .into(profileImageView);
                }

            }).addOnFailureListener(e -> Toast.makeText(ProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(ProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadBannerImage(){
        try {
            // get the profile document from the database
            DocumentReference downloadRef = profilesCollection.document(userID);

            downloadRef.get().addOnSuccessListener(documentSnapshot -> {
                String downloadUrl = documentSnapshot.getString("bannerImageURL");
                Log.d(TAG, "bannerImageURL: " + downloadUrl);

                // Glide makes it easy to load images into ImageViews
                if(downloadUrl != null) {
                    Glide.with(ProfilePage.this)
                        .load(downloadUrl)
                        .into(bannerImageView);
                }

            }).addOnFailureListener(e -> Toast.makeText(ProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(ProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//    public void showProfileRecipes() {
//        Query query = recipesRef.whereEqualTo("user_ID", userID);
//
//        FirestorePagingOptions<Recipe> firestorePagingOptions = new FirestorePagingOptions.Builder<Recipe>()
//                .setLifecycleOwner(this)
//                .setQuery(query, pagingConfig, Recipe.class)
//                .build();
//
//        adapter = new FirestoreAdapter(firestorePagingOptions, getApplicationContext());
//
//        adapter.setOnItemClickListener((documentSnapshot, position) -> openRecipePageActivity(documentSnapshot.getId()));
//
//        adapter.addLoadStateListener(combinedLoadStates -> {
//            LoadState refresh = combinedLoadStates.getRefresh();
//            LoadState append = combinedLoadStates.getAppend();
//
//            if (refresh instanceof LoadState.Error || append instanceof LoadState.Error) {
//                // The previous load (either initial or additional) failed. Call
//                // the retry() method in order to retry the load operation.
//                Toast.makeText(ProfilePage.this, "Load failed. Retrying...", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "Load failed. Retrying...");
//                adapter.retry();
//            }
//
//            if (refresh instanceof LoadState.Loading) {
//                // The initial Load has begun
//                Log.d(TAG, "Initial load has begun.");
//            }
//
//            if (append instanceof LoadState.Loading) {
//                // The adapter has started to load an additional page
//                Log.d(TAG, "Loading additional page...");
//            }
//
//            if (append instanceof LoadState.NotLoading) {
//                LoadState.NotLoading notLoading = (LoadState.NotLoading) append;
//                if (notLoading.getEndOfPaginationReached()) {
//                    // The adapter has finished loading all of the data set
//                    Log.d(TAG, "Finished loading all data.");
//                    return null;
//                }
//
//                if (refresh instanceof LoadState.NotLoading) {
//                    // The previous load (either initial or additional) completed
//                    Log.d(TAG, "Previous load completed.");
//                    return null;
//                }
//            }
//            return null;
//        });
//
//        recipeRV.setHasFixedSize(false);
//        recipeRV.setAdapter(adapter);
//    }

    public void shareProfile() {
        // implement in the future
    }

    public void openEditProfileActivity () {
        Intent intent = new Intent(this, EditProfilePage.class);
        startActivity(intent);
    }

    public void openRecipePageActivity(String recipe_ID) {
        Intent intent = new Intent(getApplicationContext(), RecipePageActivity.class);
        intent.putExtra("open_recipe_from_id", recipe_ID);
        startActivity(intent);
    }

    public void setToolbar() {
        toolbarProfilePage = findViewById(R.id.toolbarProfilePage);
        setSupportActionBar(toolbarProfilePage);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    // Opens the tool bar for the Home page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_page, menu);

        MenuItem editButton = menu.findItem(R.id.action_edit_profile);

        // vvv Attempt to invoke virtual method 'java.lang.String classes.Profile.getUserID()' on a null object reference
        if(auth.getCurrentUser().getUid().equals(profile.getUserID())) { // if the profile is yours, show the edit button
            editButton.setVisible(true);
        }
        return true;
    }

    /*
        Home Page action bar options:
        - Search for a Recipe
        - Open Scratch Notes
        - Open Profile
    */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_profile:
                openEditProfileActivity();
                return true;
            case R.id.action_share_profile:
                shareProfile();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frlayout,fragment);
        transaction.commit();
    }

    private void inflateRecipe(){
        ShowUserRecipesFragment fragment = ShowUserRecipesFragment.newInstance();
        fragment.setUserID(userID);
    }

//    private class MainAdapter extends FragmentPagerAdapter {
//        public MainAdapter(FragmentManager supportFragmentManager){
//            super(supportFragmentManager);
//        }
//    }


}