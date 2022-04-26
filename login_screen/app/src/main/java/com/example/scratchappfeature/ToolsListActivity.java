package com.example.scratchappfeature;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        setToolbar();

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
                            if (mProfile.getSavedTools() == null ||
                                    mProfile.getSavedTools().size() == 0) {
                                String emptyList =
                                        "Lets add some tools. " +
                                                "\nLet started by tapping on the +";
                                mToolsList.add(emptyList);
                            } else {
                                for (String ingredient : mProfile.getSavedTools()) {
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


    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarToolsList);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tools_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_tool_to_list:
                //Make Dialog
                showAddToolDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddToolDialog() {
        AddToolDialogFragment dialog = new AddToolDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddIngredientListDialog");
    }

    @Override
    public void applyToolName(String toolName) {

        Map<String, Object> savedTools = new HashMap<>();
        savedTools.put("savedTools", FieldValue.arrayUnion(toolName));
        db.collection("profile").document(userID)
                .update(savedTools);

        getTools();
    }
}
