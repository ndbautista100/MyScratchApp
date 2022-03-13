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

import classes.LoadingDialog;
import classes.Profile;
import java.util.Map;

public class EditProfilePage extends AppCompatActivity {
    private static final String TAG = "EditProfilePage";
    private String name;
    private String randomString;
    private String bio;
    private String favoriteFood;
    private String id;
    private EditText nameInput;
    private EditText bioInput;
    private EditText favoriteFoodInput;
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
    private DocumentReference docRef;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result != null){
                profileImage.setImageURI(result);
                profileImageUri = result;

                Log.d(TAG, profileImageUri.toString());

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
        favoriteFoodInput = (EditText) findViewById(R.id.favoritefoodInput);

        fstorage = FirebaseStorage.getInstance();
        storageRef = fstorage.getReference("images/avatars");

        profileImage = (ImageView) findViewById(R.id.profilePicture);
        uploadbutton = (Button) findViewById(R.id.uploadProfilePic);
        uploadbutton.setOnClickListener(view -> mGetContent.launch("image/*"));

        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        userID = fauth.getCurrentUser().getUid();

        id = fstore.collection("profile").document().getId();

        docRef = fstore.collection("profile").document(userID);

        docRef.get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        profile = doc.toObject(Profile.class);
                        name = doc.getString("pname");
                        bio = doc.getString("bio");
                        favoriteFood = doc.getString("favoriteFood");
                        nameInput.setText(name);
                        bioInput.setText(bio);
                        favoriteFoodInput.setText(favoriteFood);

                        downloadImage();
                    }
                    else {
                        Log.d("docv", "No such info");
                    }
                }
                else {
                    Log.d("docv", "failed to get with", task.getException());
                }
            });

        finishbutton = (Button) findViewById(R.id.finishbutton);
        finishbutton.setOnClickListener(v -> {
            profile = new Profile();
            name = nameInput.getText().toString();
            bio = bioInput.getText().toString();
            favoriteFood = favoriteFoodInput.getText().toString();

            addDataToDatabase(name, bio, favoriteFood);

            // Change this to submitting the the information, picture, and everything.
            returnToProfileActivity();
        });
    }


    private void uploadImage(){
        Log.d(TAG, "User has a profile image: " + profile.getProfileImageName());
        if(profile.getProfileImageName() != null) { // if user already has a profile image
            // delete that image from Firebase Storage
            StorageReference recipeImageRef = storageRef.child(profile.getProfileImageName());
            Log.d(TAG, "Deleting image: " + profile.getProfileImageName());
            recipeImageRef.delete()
                .addOnSuccessListener(unused1 -> Log.i(TAG, "Successfully deleted image: " + profile.getProfileImageName()))
                .addOnFailureListener(e -> Log.e(TAG, e.toString()));
        }

        try { // uploading the profile pic
            if (profileImageUri == null) {
                LoadingDialog loadingDialog = new LoadingDialog(EditProfilePage.this);
                loadingDialog.startLoadingDialog();
                
                String imageName = userID + "_" + UUID.randomUUID().toString() + "." + getExtension(profileImageUri);
                StorageReference imageReference = storageRef.child(imageName);

                Log.d(TAG, "Image name: " + imageName);

                UploadTask uploadTask = imageReference.putFile(profileImageUri); // store image
                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl(); // im guessing this is where it messes up
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Task successful!!");
                        // update profile document's profileImageURL field
                        profile.setProfileImageURL(task.getResult().toString());
                        profile.setProfileImageName(imageName);
                        fstore.collection("profile")
                            .document(userID)
                            .update("profileImageURL", profile.getProfileImageURL(),
                                    "profileImageName", profile.getProfileImageName())
                            .addOnCompleteListener(task1 -> {
                                loadingDialog.dismissDialog();
                                Log.d(TAG, "Success!!!");
                                Toast.makeText(EditProfilePage.this, "Profile image uploaded!", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                loadingDialog.dismissDialog();
                                Log.d(TAG, "Failure in storing");
                                Toast.makeText(EditProfilePage.this, "Profile image failed to upload.", Toast.LENGTH_SHORT).show();
                        });
                    } // Something with the code is giving the error that the task was not successful.
                    else if (!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        Log.d(TAG, "Task failed: " + task.getException().toString());
                        Toast.makeText(EditProfilePage.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(EditProfilePage.this, e.getMessage() +"in upload image2", Toast.LENGTH_SHORT).show();
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

    public void downloadImage(){
        try {
            // get the profile document from the database
            DocumentReference downloadRef = fstore.collection("profile").document(userID);

            downloadRef.get().addOnSuccessListener(documentSnapshot -> {
                String downloadUrl = documentSnapshot.getString("profileImageURL");

                // Glide makes it easy to load images into ImageViews
                if(downloadUrl != null) {
                    Glide.with(EditProfilePage.this)
                        .load(downloadUrl)
                        .into(profileImage);
                }

            }).addOnFailureListener(e -> Toast.makeText(EditProfilePage.this, e.getMessage()+"in downloadImage1", Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(EditProfilePage.this, e.getMessage() + "in downloadImage2", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDataToDatabase(String name, String bio, String favoriteFood) {
        docRef.update("pname", name,
                "bio", bio,
                "favoritefood", favoriteFood)
                .addOnSuccessListener(unused -> Log.i(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating document", e));
    }

    public void returnToProfileActivity () {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }

    public void showToast (String text){
        Toast.makeText(EditProfilePage.this, text, Toast.LENGTH_SHORT).show();
    }
}