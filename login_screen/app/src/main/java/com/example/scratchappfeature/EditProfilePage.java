package com.example.scratchappfeature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import classes.Profile;

public class EditProfilePage extends AppCompatActivity {

    private String name, bio, favoritefood;
    private EditText nameInput;
    private EditText bioInput;
    private EditText favoritefoodInput;
    private Button finishbutton;
    private ImageButton uploadbutton;
    private ImageView profileImage;

    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;
    private StorageReference storageRef;
    private String userID;

    private RecyclerView courseRV;
    private ArrayList<Profile> profileArrayList;
    //private ProfileRV profileRV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page); // change to correct activity if needed
        nameInput = findViewById(R.id.nameInput);
        bioInput = (EditText) findViewById(R.id.bioInput);
        favoritefoodInput = (EditText) findViewById(R.id.favoritefoodInput);
        profileImage = (ImageView) findViewById(R.id.profilepicture);
        storageRef = FirebaseStorage.getInstance().getReference();

        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        userID = fauth.getCurrentUser().getUid();

/*        //Tried pulling name from the scratchUsers Didn't work.
        DocumentReference docref = fstore.collection("scratchUsers").document(userID);
        docref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name3 = value.getString("First Name");
            }
        });*/

        //RecyclerView for profile page.
        /*coursesArrayList = new ArrayList<>();
        courseRV.setHasFixedSize(true);
        courseRV.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
        courseRVAdapter = new CourseRVAdapter(coursesArrayList, this);

        // setting adapter to our recycler view.
        courseRV.setAdapter(courseRVAdapter);*/


        finishbutton = (Button) findViewById(R.id.finishbutton);
        finishbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameInput.getText().toString();
                bio = bioInput.getText().toString();
                favoritefood = favoritefoodInput.getText().toString();

                addDatatoDatabase(name, bio, favoritefood);

                //Change this to submitting the the information, picture, and everything.
                returnToProfileActivity();
            }

        });

        uploadbutton = (ImageButton) findViewById(R.id.uploadbutton);
        uploadbutton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        }));
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageuri = data.getData();
                profileImage.setImageURI(imageuri);

                //uploadtoFirebase(imageuri);
            }
        }
    }


    //for adding image to the database NOT WORKING
   /* private void uploadtoFirebase(Uri imageuri){
        StorageReference filereference = storageRef.child("blankpicture.png");
        filereference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(profile_pagae.this,"Image Upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(profile_pagae.this,"Image did not upload", Toast.LENGTH_SHORT).show();
            }
        });
}*/
    private void addDatatoDatabase(String name, String bio, String favoritefood) {
        DocumentReference dbProfile = fstore.collection("profile").document(userID);
        Profile profile = new Profile(name, bio, favoritefood);
        dbProfile.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Toast.makeText(EditProfilePage.this, "Your profile has been saved", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(EditProfilePage.this, "Fail to add profile \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void returnToProfileActivity () {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }

    public void showToast (String text){
        Toast.makeText(EditProfilePage.this, text, Toast.LENGTH_SHORT).show();
    }
}