package com.example.scratchappfeature;

import androidx.appcompat.app.AppCompatActivity;
import classes.Posts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class AllActivityFeed extends AppCompatActivity {
    private final String TAG = "AllActivityFeed";
    private RecyclerView allactivityRV;
    private Toolbar toolbarAllActivity;
    private ActionBar ab;
    private ArrayList<Posts> allposts;
    private ActivityRVAdapter adapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_feed);
        allactivityRV = findViewById(R.id.allActivityRecyclerView);
        setToolbar();
        ab.setTitle("Activity Feed");

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setReverseLayout(true);
        allactivityRV.setLayoutManager(lm);

        showPosts();
    }

    public void showPosts() {
        allposts = new ArrayList<>();

        db.collection("activityfeed")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null || !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Posts posts = document.toObject(Posts.class);
                                allposts.add(posts);

                            }
                            adapter = new ActivityRVAdapter(allposts, getApplicationContext());

                            allactivityRV.setAdapter(adapter);
                            allactivityRV.setNestedScrollingEnabled(false);
                        }
                    }else{
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void setToolbar() {
        toolbarAllActivity = (Toolbar) findViewById(R.id.toolbarAAF);
        setSupportActionBar(toolbarAllActivity);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
