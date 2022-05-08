package com.example.scratchappfeature;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import classes.AddIngredientListDialog;
import classes.AddToolDialogFragment;
import classes.Profile;

public class ToolsListActivity extends AppCompatActivity implements AddToolDialogFragment.AddToolDialogListener {

    private static final String TAG = "ToolsListActivity";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;
    private Profile mProfile;
    private FirebaseAuth fauth;
    private ArrayList<String> mToolsList;
    private RecyclerView toolsRV;
    private ToolsRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools_list);
        //setToolbar();

        fauth = FirebaseAuth.getInstance();
        userID = fauth.getCurrentUser().getUid();
        toolsRV = findViewById(R.id.toolsListRecyclerView);
        toolsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        getTools();
    }

    private void getTools(){
        mToolsList = new ArrayList<>();
        db.collection("profile")
                .whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            mProfile = document.toObject(Profile.class);
                            //Once we have the user's profile, we can now get their saved recipes
                            //and add them to the recipe arraylist
                            if (mProfile.getSavedIngredients() == null ||
                                    mProfile.getSavedIngredients().size() == 0) {
                                String emptyList =
                                        "Lets add some ingredients" +
                                                "/Get started by tapping on the +";
                                mToolsList.add(emptyList);
                            } else {
                                for (String ingredient : mProfile.getSavedIngredients()) {
                                    mToolsList.add(ingredient);
                                }
                            }

                            adapter = new ToolsRVAdapter(getApplicationContext(), mToolsList);
                            adapter.setProfileID(userID);
                            // after passing this ArrayList to our adapter class we are setting our adapter to our RecyclerView
                            toolsRV.setAdapter(adapter);

                        }
                    }});
    }


    @Override
    public void applyToolName(String toolName) {

    }
}
