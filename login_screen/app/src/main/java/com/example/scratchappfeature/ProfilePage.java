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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import classes.Profile;

public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfilePage";
    private String namestr;
    private String biostr;
    private String favoritefoodstr;
    private String id;
    private TextView displayname;
    private TextView displaybio;
    private TextView displayfavoritefood;
    private Button finishButton;
    private Button editbutton;
    private ImageView profileImage;
    private ImageButton followBtn;

    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;
    private CollectionReference fcollection;
    private StorageReference storageRef;
    private String userID;

    private Toolbar toolbarProfilePage;
    private ActionBar ab;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page); // change to correct activity if needed

        setToolbar();

        displayname = findViewById(R.id.name);
        displaybio =  findViewById(R.id.bio);
        displayfavoritefood =  findViewById(R.id.favoriteFood);
        profileImage = (ImageView) findViewById(R.id.profilePicture);
        followBtn =  findViewById(R.id.followButton);
        storageRef = FirebaseStorage.getInstance().getReference();

        fstore = FirebaseFirestore.getInstance();
        fcollection = fstore.collection("profile");
        fauth = FirebaseAuth.getInstance();
        userID = fauth.getCurrentUser().getUid();

        id = fstore.collection("profile").document().getId();
        fstore.collection("profile").document(userID).get();
        fstore.collection("profile").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        namestr = doc.getString("pname");
                        biostr = doc.getString("bio");
                        favoritefoodstr = doc.getString("favoritefood");
                        displayname.setText(namestr);
                        displaybio.setText(biostr);
                        displayfavoritefood.setText(favoritefoodstr);

                        downloadImage();
                    }
                    else{
                        Log.d("docv", "No such info");
                    }
                }
                else{
                    Log.d("docv", "failed to get with", task.getException());
                }
            }
        });

//        finishButton = (Button) findViewById(R.id.finishButton);
//        finishButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                returnToMainActivity();
//            }
//
//        });
//
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

    public void openEditActivity () {
        Intent intent = new Intent(this, EditProfilePage.class);
        startActivity(intent);
    }

    public void returnToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void setToolbar() {
        toolbarProfilePage = findViewById(R.id.toolbarProfilePage);
        setSupportActionBar(toolbarProfilePage);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}