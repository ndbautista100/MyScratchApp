package com.example.scratchappfeature;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
<<<<<<< HEAD
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
=======
    private Profile profile;
    private EditText nameEditText;
    private EditText bioEditText;
    private EditText favFoodEditText;
    private Button finishButton;
    private Button editAvatarButton;
    private Button editBannerButton;
    private ImageView profileImageView;
    private ImageView bannerImageView;
    private Uri profileImageUri;
    private Uri bannerImageUri;

>>>>>>> 0b860fca77f9d4109b4e154470198feb347fd07f
    private String userID;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private final StorageReference avatarStorageRef = storage.getReference("images/avatars");
    private final StorageReference bannerStorageRef = storage.getReference("images/banners");
    private DocumentReference profileRef;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result != null){
                profileImageView.setImageURI(result);
                profileImageUri = result;

                Log.d(TAG, profileImageUri.toString());

<<<<<<< HEAD
                uploadImage();
=======
                editAvatarButtonImage();
            }
        }
    });

    ActivityResultLauncher<String> bGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result != null){
                bannerImageView.setImageURI(result);
                bannerImageUri = result;

                Log.d(TAG, bannerImageUri.toString());

                editBannerButtonImage();
>>>>>>> 0b860fca77f9d4109b4e154470198feb347fd07f
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page2); // change to correct activity if needed
<<<<<<< HEAD
        nameInput = (EditText) findViewById(R.id.nameInput);
        bioInput = (EditText) findViewById(R.id.bioInput);
        favoriteFoodInput = (EditText) findViewById(R.id.favoritefoodInput);

        fstorage = FirebaseStorage.getInstance();
        storageRef = fstorage.getReference("images/avatars");

        profileImage = (ImageView) findViewById(R.id.profilePicture);
        uploadbutton = (Button) findViewById(R.id.uploadProfilePic);
        uploadbutton.setOnClickListener(view -> mGetContent.launch("image/*"));
=======

        setToolbar();

        userID = auth.getCurrentUser().getUid();

        nameEditText = findViewById(R.id.editNameEditText);
        bioEditText = findViewById(R.id.editBioEditText);
        favFoodEditText = findViewById(R.id.editFavFoodEditText);

        profileImageView = findViewById(R.id.editAvatarImageView);
        editAvatarButton = findViewById(R.id.editAvatarButton);
        editAvatarButton.setOnClickListener(view -> mGetContent.launch("image/*"));
>>>>>>> 0b860fca77f9d4109b4e154470198feb347fd07f

        bannerImageView = findViewById(R.id.editBannerImageView);
        editBannerButton = findViewById(R.id.editBannerButton);
        editBannerButton.setOnClickListener(view -> bGetContent.launch("image/*"));

        profileRef = db.collection("profile").document(userID);
        profileRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if(doc.exists()){
                            profile = doc.toObject(Profile.class);

                            nameEditText.setText(profile.getpname());
                            bioEditText.setText(profile.getbio());
                            favFoodEditText.setText(profile.getfavoritefood());

<<<<<<< HEAD
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
=======
                            downloadProfileImage();
                            downloadBannerImage();
                        }
                        else {
                            Log.d(TAG, "No such info");
                        }
>>>>>>> 0b860fca77f9d4109b4e154470198feb347fd07f
                    }
                    else {
                        Log.d(TAG, "failed to get with", task.getException());
                    }
                });

        finishButton = findViewById(R.id.finishEditButton);
        finishButton.setOnClickListener(v -> {

            profileRef.update("pname", nameEditText.getText().toString(),
                    "bio", bioEditText.getText().toString(),
                    "favoritefood", favFoodEditText.getText().toString())
                    .addOnSuccessListener(unused -> Log.i(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating document", e));

            returnToProfileActivity();
        });
    }


<<<<<<< HEAD
    private void uploadImage(){
=======
    private void editAvatarButtonImage(){
>>>>>>> 0b860fca77f9d4109b4e154470198feb347fd07f
        Log.d(TAG, "User has a profile image: " + profile.getProfileImageName());
        if(profile.getProfileImageName() != null) { // if user already has a profile image
            // delete that image from Firebase Storage
            StorageReference recipeImageRef = avatarStorageRef.child(profile.getProfileImageName());
            Log.d(TAG, "Deleting image: " + profile.getProfileImageName());
            recipeImageRef.delete()
                    .addOnSuccessListener(unused1 -> Log.i(TAG, "Successfully deleted image: " + profile.getProfileImageName()))
                    .addOnFailureListener(e -> Log.e(TAG, e.toString()));
        }

        try { // uploading the profile pic
            if (profileImageUri != null) {
                LoadingDialog loadingDialog = new LoadingDialog(EditProfilePage.this);
                loadingDialog.startLoadingDialog();

                String imageName = userID + "_" + UUID.randomUUID().toString() + "." + getExtension(profileImageUri);
                StorageReference imageReference = avatarStorageRef.child(imageName);

                Log.d(TAG, "Avatar image name: " + imageName);

                UploadTask uploadTask = imageReference.putFile(profileImageUri); // store image
                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // update profile document's profileImageURL and profileImageName fields
                        profile.setProfileImageURL(task.getResult().toString());
                        profile.setProfileImageName(imageName);
                        db.collection("profile")
                                .document(userID)
                                .update("profileImageURL", profile.getProfileImageURL(),
                                        "profileImageName", profile.getProfileImageName())
                                .addOnCompleteListener(task1 -> {
                                    loadingDialog.dismissDialog();
                                    Log.i(TAG, "Uploading avatar was successful!");
                                    Toast.makeText(EditProfilePage.this, "Profile image uploaded!", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                            loadingDialog.dismissDialog();
                            Log.e(TAG, "Failure in storing avatar: " + e.getMessage());
                            Toast.makeText(EditProfilePage.this, "Profile image failed to upload.", Toast.LENGTH_SHORT).show();
                        });
                    } else if (!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        Log.e(TAG, "Task failed: " + task.getException().toString());
                        Toast.makeText(EditProfilePage.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(EditProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri uri) {
        try {
<<<<<<< HEAD
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

=======
            // get the profile document from the database
            DocumentReference downloadRef = db.collection("profile").document(userID);

            downloadRef.get().addOnSuccessListener(documentSnapshot -> {
                String downloadUrl = documentSnapshot.getString("profileImageURL");

                // Glide makes it easy to load images into ImageViews
                if(downloadUrl != null) {
                    Glide.with(EditProfilePage.this)
                            .load(downloadUrl)
                            .into(profileImageView);
                }

            }).addOnFailureListener(e -> Toast.makeText(EditProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(EditProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void editBannerButtonImage(){
        Log.d(TAG, "User has a banner image: " + profile.getBannerImageName());
        if(profile.getBannerImageName() != null) { // if user already has a banner image
            // delete that image from Firebase Storage
            StorageReference recipeImageRef = bannerStorageRef.child(profile.getBannerImageName());
            Log.d(TAG, "Deleting image: " + profile.getBannerImageName());
            recipeImageRef.delete()
                    .addOnSuccessListener(unused1 -> Log.i(TAG, "Successfully deleted image: " + profile.getBannerImageName()))
                    .addOnFailureListener(e -> Log.e(TAG, e.toString()));
        }

        try { // uploading the profile pic
            if (bannerImageUri != null) {
                LoadingDialog loadingDialog = new LoadingDialog(EditProfilePage.this);
                loadingDialog.startLoadingDialog();

                String imageName = userID + "_" + UUID.randomUUID().toString() + "." + getExtension(bannerImageUri);
                StorageReference imageReference = bannerStorageRef.child(imageName);

                Log.d(TAG, "Banner image name: " + imageName);

                UploadTask uploadTask = imageReference.putFile(bannerImageUri); // store image
                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // update profile document's bannerImageURL and bannerImageName fields
                        profile.setBannerImageURL(task.getResult().toString());
                        profile.setBannerImageName(imageName);
                        db.collection("profile")
                                .document(userID)
                                .update("bannerImageURL", profile.getBannerImageURL(),
                                        "bannerImageName", profile.getBannerImageName())
                                .addOnCompleteListener(task1 -> {
                                    loadingDialog.dismissDialog();
                                    Log.i(TAG, "Uploading banner was successful!");
                                    Toast.makeText(EditProfilePage.this, "Banner image uploaded!", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                            loadingDialog.dismissDialog();
                            Log.e(TAG, "Failure in storing banner: " + e.getMessage());
                            Toast.makeText(EditProfilePage.this, "Banner image failed to upload.", Toast.LENGTH_SHORT).show();
                        });
                    } else if (!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        Log.e(TAG, "Uploading banner failed: " + task.getException().toString());
                        Toast.makeText(EditProfilePage.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
>>>>>>> 0b860fca77f9d4109b4e154470198feb347fd07f
        } catch (Exception e) {
            return null;
        }
    }

    public void downloadImage(){
        try {
            // get the profile document from the database
            DocumentReference downloadRef = db.collection("profile").document(userID);

            downloadRef.get().addOnSuccessListener(documentSnapshot -> {
                String downloadUrl = documentSnapshot.getString("profileImageURL");

                // Glide makes it easy to load images into ImageViews
                if(downloadUrl != null) {
                    Glide.with(EditProfilePage.this)
<<<<<<< HEAD
                        .load(downloadUrl)
                        .into(profileImage);
=======
                            .load(downloadUrl)
                            .into(bannerImageView);
>>>>>>> 0b860fca77f9d4109b4e154470198feb347fd07f
                }

            }).addOnFailureListener(e -> Toast.makeText(EditProfilePage.this, e.getMessage()+"in downloadImage1", Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(EditProfilePage.this, e.getMessage() + "in downloadImage2", Toast.LENGTH_SHORT).show();
        }
    }

    public void returnToProfileActivity () {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }

    public void setToolbar() {
        Toolbar toolbarEditProfile = findViewById(R.id.toolbarEditProfile);
        setSupportActionBar(toolbarEditProfile);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                returnToProfileActivity();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}