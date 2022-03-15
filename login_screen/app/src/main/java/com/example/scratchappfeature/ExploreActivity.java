package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import classes.Profile;
import classes.Recipe;

public class ExploreActivity extends AppCompatActivity {
    FloatingActionButton addVideoBtn;
    private ProfileRVAdapter adapter;
    private static final String TAG = "ProfileRVAdapter";
    private RecyclerView profiles_RV;
    private ArrayList<Profile> profiles;
    private Toolbar toolbarExplorePage;
    private ActionBar ab;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        profiles_RV = findViewById(R.id.profilesRV);
        profiles_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        setToolbar();
        //profiles_RV.setAdapter();

        //setTitle("Videos");
        showProfiles();
        // instantiate ui view
        addVideoBtn = findViewById(R.id.addVideoBtn);

        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(ExploreActivity.this, AddVideoActivity.class)));
            }
        });
    }

    public void showProfiles() {

        profiles =  new ArrayList<>();
        db.collection("profile")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.i(TAG, "Success! " + FirebaseAuth.getInstance().getCurrentUser().getUid() + " => " + document.getData());

                            Profile profile = document.toObject(Profile.class);
                            profiles.add(profile);
                        }
                        adapter = new ProfileRVAdapter(profiles, getApplicationContext());
                        // after passing this ArrayList to our adapter class we are setting our adapter to our RecyclerView
                        profiles_RV.setAdapter(adapter);
                        // this is called when a recipe is clicked
                        adapter.setOnItemClickListener(position -> {
                            Profile clickedProfile = profiles.get(position);

                        });
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void setToolbar() {
        toolbarExplorePage = findViewById(R.id.toolbarExplorePage);
        setSupportActionBar(toolbarExplorePage);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
