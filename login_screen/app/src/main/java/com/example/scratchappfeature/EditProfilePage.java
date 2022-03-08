package com.example.scratchappfeature;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.UUID;
import classes.Profile;
import java.util.Map;

public class EditProfilePage extends AppCompatActivity {

    private String name;
    String randostring;
    private String bio;
    private String favoritefood;
    private String id;
    private EditText nameInput;
    private EditText bioInput;
    private EditText favoritefoodInput;
    private Button finishbutton;
    private Button uploadbutton;
    private ImageView profileImage;
    private ImageView bannerImage;
    private Uri profileImageUri;
    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;
    private FirebaseStorage fstorage;
    private StorageReference storageRef;
    private String userID;
    private Profile profile;
    

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result != null){
                profileImage.setImageURI(result);
                profileImageUri = result;

                uploadImage();
            }
        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page2); // change to correct activity if needed
        nameInput = (EditText) findViewById(R.id.nameInput);
        bioInput = (EditText) findViewById(R.id.bioInput);
        favoritefoodInput = (EditText) findViewById(R.id.favoritefoodInput);

        fstorage = FirebaseStorage.getInstance();
        storageRef = fstorage.getReference("images/");

        profileImage = (ImageView) findViewById(R.id.profilepicture);
        uploadbutton = (Button) findViewById(R.id.uploadprofilepic);
        uploadbutton.setOnClickListener(view -> mGetContent.launch("image/*"));

        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        userID = fauth.getCurrentUser().getUid();

        id = fstore.collection("profile").document().getId();
        fstore.collection("profile").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        profile = doc.toObject(Profile.class);
                        name = doc.getString("pname");
                        bio = doc.getString("bio");
                        favoritefood = doc.getString("favoritefood");
                        nameInput.setText(name);
                        bioInput.setText(bio);
                        favoritefoodInput.setText(favoritefood);


                        downloadimage();
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

        finishbutton = (Button) findViewById(R.id.finishbutton);
        finishbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile = new Profile();
                name = nameInput.getText().toString();
                bio = bioInput.getText().toString();
                favoritefood = favoritefoodInput.getText().toString();


                addDatatoDatabase(name, bio, favoritefood);

                //Change this to submitting the the information, picture, and everything.
                returnToProfileActivity();
            }
        });
    }


    private void uploadImage(){
        try {
            if (profileImageUri != null) {
                String imagename = userID + "_" + UUID.randomUUID().toString() + "." + getExtension(profileImageUri);
                StorageReference imageReference = storageRef.child(imagename);

                UploadTask uploadTask = imageReference.putFile(profileImageUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imageReference.getDownloadUrl(); // im guessing this is where it messes up
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            profile.setImageURL(task.getResult().toString());
                            fstore.collection("profile")
                                    .document(userID)
                                    .update("imageURL", profile.getImageURL())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(EditProfilePage.this, "Profile uploaded", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditProfilePage.this, "Profile image failed to upload!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } // Something with the code is giving the error that the task was not successufl.
                        else if (!task.isSuccessful()) {
                            Toast.makeText(EditProfilePage.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (Exception e) {
            Toast.makeText(EditProfilePage.this, e.getMessage() +"in upload image2", Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadimage(){
        try {
            // get the recipe document from the database
            DocumentReference downloadRef = fstore.collection("profile").document(userID);

            downloadRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    //this is giving null value, but why?
                    String downloadUrl = documentSnapshot.getString("imageURL");

                    // Glide makes it easy to load images into ImageViews
                    if(downloadUrl != null) {
                        Glide.with(EditProfilePage.this).load(downloadUrl).into(profileImage);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfilePage.this, e.getMessage()+"indownloadimage1", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(EditProfilePage.this, e.getMessage()+"in download image2", Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

        } catch (Exception e) {
            return null;
        }
    }


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