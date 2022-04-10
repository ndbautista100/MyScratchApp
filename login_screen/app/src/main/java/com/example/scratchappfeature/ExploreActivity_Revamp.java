package com.example.scratchappfeature;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import classes.Profile;

public class ExploreActivity_Revamp extends AppCompatActivity {
    private static final String TAG = "search";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText searchBox;
    private ImageButton searchBTN;
    private RecyclerView profiles_RV;
    private ProfileRVAdapter adapter;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_revamp);
        searchBox = findViewById(R.id.searchBar);
        searchBTN = findViewById(R.id.searchButton);
        profiles_RV = findViewById(R.id.profileRV);
        profiles_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(searchBox.getText().toString());
            }
        });
    }

    public void search(String query){
        ArrayList<Profile> results = new ArrayList<Profile>();
        db.collection("profile").whereEqualTo("pname", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.i(TAG, "Success! " + FirebaseAuth.getInstance().getCurrentUser().getUid() + " => " + document.getData());

                            Profile profile = document.toObject(Profile.class);
                            results.add(profile);
                        }
                        adapter = new ProfileRVAdapter(results, getApplicationContext());
                        // after passing this ArrayList to our adapter class we are setting our adapter to our RecyclerView
                        profiles_RV.setAdapter(adapter);
                        // this is called when a recipe is clicked
                        adapter.setOnItemClickListener(position -> {
                            Profile clickedProfile = results.get(position);

                        });
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }
}
