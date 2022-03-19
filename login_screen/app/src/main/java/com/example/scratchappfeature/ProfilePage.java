package com.example.scratchappfeature;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import classes.Profile;
import classes.Recipe;

public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfilePage";
    private String namestr;
    private String biostr;
    private String favoritefoodstr;
    private String id;
    private TextView displayname;
    private TextView displaybio;
    private TextView displayfavoritefood;
    private Button editbutton;
    private ImageView profileImage;
    private ImageView bannerImage;
    private ImageButton followBtn;

    private FirebaseAuth fauth;
    private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private CollectionReference fcollection;
    private StorageReference storageRef;
    private String userID;
    private Profile profile;

    private RecyclerView recipeRV;
    private FirestoreAdapter adapter;
    private final CollectionReference recipesRef= fstore.collection("recipes");
    private final PagingConfig pagingConfig = new PagingConfig(6, 3, false);

    private Toolbar toolbarProfilePage;
    private ActionBar ab;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page); // change to correct activity if needed

        setToolbar();

        displayname = findViewById(R.id.name);
        displaybio =  findViewById(R.id.bio);
        displayfavoritefood =  findViewById(R.id.favoriteFood);
        profileImage = (ImageView) findViewById(R.id.profilePicture);
        bannerImage = (ImageView) findViewById(R.id.banner);
        followBtn =  findViewById(R.id.followButton);
        storageRef = FirebaseStorage.getInstance().getReference();

        fcollection = fstore.collection("profile");
        fauth = FirebaseAuth.getInstance();
        userID = fauth.getCurrentUser().getUid();

        recipeRV = (RecyclerView) findViewById(R.id.recipeRecycler);

        showRecipes();

        DocumentReference docRef = db.collection("profile").document(userID);
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    Log.i(TAG, "Found document!");

                    profile = document.toObject(Profile.class);
                    namestr = profile.getpname();
                    biostr = profile.getbio();
                    favoritefoodstr = profile.getfavoritefood();
                    displayname.setText(namestr);
                    displaybio.setText(biostr);
                    displayfavoritefood.setText(favoritefoodstr);

                    downloadImage();
                    downloadBannerImage();

                } else {
                    Log.e(TAG, "No such document.");
                }
            } else {
                Log.e(TAG, "get failed with" + task.getException());
            }
        });

        editbutton = (Button) findViewById(R.id.editButton);
        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditActivity();
            }

        });

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }

    public void downloadImage(){
        try {
            // get the recipe document from the database
            DocumentReference downloadRef = fstore.collection("profile").document(userID);

            downloadRef.get().addOnSuccessListener(documentSnapshot -> {
                String downloadUrl = documentSnapshot.getString("profileImageURL");
                Log.d(TAG, "downloadURL: " + downloadUrl);

                // Glide makes it easy to load images into ImageViews
                if(downloadUrl != null) {
                    Glide.with(ProfilePage.this)
                        .load(downloadUrl)
                        .into(profileImage);
                }

            }).addOnFailureListener(e -> Toast.makeText(ProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(ProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadBannerImage(){
        try {
            // get the profile document from the database
            DocumentReference downloadRef = fstore.collection("profile").document(userID);

            downloadRef.get().addOnSuccessListener(documentSnapshot -> {
                String downloadUrl = documentSnapshot.getString("bannerImageURL");

                // Glide makes it easy to load images into ImageViews
                if(downloadUrl != null) {
                    Glide.with(ProfilePage.this)
                            .load(downloadUrl)
                            .into(bannerImage);
                }

            }).addOnFailureListener(e -> Toast.makeText(ProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(ProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void openEditActivity () {
        Intent intent = new Intent(this, EditProfilePage.class);
        startActivity(intent);
    }

    public void setToolbar() {
        toolbarProfilePage = findViewById(R.id.toolbarProfilePage);
        setSupportActionBar(toolbarProfilePage);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void showRecipes() {
        Query query = recipesRef.whereEqualTo("user_ID", userID);

        FirestorePagingOptions<Recipe> firestorePagingOptions = new FirestorePagingOptions.Builder<Recipe>()
                .setLifecycleOwner(this)
                .setQuery(query, pagingConfig, Recipe.class)
                .build();

        adapter = new FirestoreAdapter(firestorePagingOptions, getApplicationContext());

        adapter.setOnItemClickListener((documentSnapshot, position) -> openRecipePageActivity(documentSnapshot.getId()));

        adapter.addLoadStateListener(combinedLoadStates -> {
            LoadState refresh = combinedLoadStates.getRefresh();
            LoadState append = combinedLoadStates.getAppend();

            if (refresh instanceof LoadState.Error || append instanceof LoadState.Error) {
                // The previous load (either initial or additional) failed. Call
                // the retry() method in order to retry the load operation.
                Toast.makeText(ProfilePage.this, "Load failed. Retrying...", Toast.LENGTH_SHORT).show();
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

        recipeRV.setHasFixedSize(false);
        recipeRV.setAdapter(adapter);
    }

    public void openRecipePageActivity(String recipe_ID) {
        Intent intent = new Intent(getApplicationContext(), RecipePageActivity.class);
        intent.putExtra("open_recipe_from_id", recipe_ID);
        startActivity(intent);
    }
}