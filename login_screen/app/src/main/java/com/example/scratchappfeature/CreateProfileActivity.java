package com.example.scratchappfeature;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import classes.LoadingDialog;
import classes.Profile;

public class CreateProfileActivity extends AppCompatActivity {
    private static final String TAG = "CreateProfileActivity";
    private Profile profile;
    private EditText nameEditText;
    private EditText bioEditText;
    private EditText favFoodEditText;
    private Button finishButton;
    private Button uploadAvatarButton;
    private Button uploadBannerButton;
    private ImageView profileImageView;
    private ImageView bannerImageView;

    private Uri profileImageUri;
    private Uri bannerImageUri;

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private final StorageReference avatarStorageRef = storage.getReference("images/avatars");
    private final StorageReference bannerStorageRef = storage.getReference("images/banners");

    private String userID = fAuth.getCurrentUser().getUid();

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result != null){
                profileImageView.setImageURI(result);
                profileImageUri = result;

                Log.d(TAG, profileImageUri.toString());

                uploadAvatarButtonImage();
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

                uploadBannerButtonImage();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        profile = new Profile();

        nameEditText = findViewById(R.id.createNameEditText);
        bioEditText = findViewById(R.id.createBioEditText);
        favFoodEditText = findViewById(R.id.createFavFoodEditText);
        finishButton = findViewById(R.id.finishCreateButton);
        uploadAvatarButton = findViewById(R.id.createAvatarButton);
        uploadBannerButton = findViewById(R.id.createBannerButton);
        profileImageView = findViewById(R.id.createAvatarImageView);
        bannerImageView = findViewById(R.id.createBannerImageView);

        uploadAvatarButton.setOnClickListener(view -> mGetContent.launch("image/*"));
        uploadBannerButton.setOnClickListener(view -> bGetContent.launch("image/*"));

        Intent intent = getIntent();
        String displayName = intent.getStringExtra("user_display_name");
        nameEditText.setText(displayName);

        finishButton.setOnClickListener(view -> {
            profile.setname(nameEditText.getText().toString());
            profile.setbio(bioEditText.getText().toString());
            profile.setfavoritefood(favFoodEditText.getText().toString());
            profile.setUserID(fAuth.getCurrentUser().getUid());

            db.collection("profile").document(fAuth.getCurrentUser().getUid())
                .set(profile)
                .addOnSuccessListener(unused -> Log.i(TAG, "Successfully created profile!"))
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());
                });


            Toast.makeText(CreateProfileActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
    }

    private void uploadAvatarButtonImage(){
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
                LoadingDialog loadingDialog = new LoadingDialog(CreateProfileActivity.this);
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
                        loadingDialog.dismissDialog();
                        Log.d(TAG, "Uploading avatar was successful!");
                        Toast.makeText(CreateProfileActivity.this, "Banner image uploaded!", Toast.LENGTH_SHORT).show();

                        // set profile object's profileImageURL and profileImageName fields
                        profile.setProfileImageURL(task.getResult().toString());
                        profile.setProfileImageName(imageName);

                    } else if (!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        Log.d(TAG, "Task failed: " + task.getException().toString());
                        Toast.makeText(CreateProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(CreateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadBannerButtonImage(){
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
                LoadingDialog loadingDialog = new LoadingDialog(CreateProfileActivity.this);
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
                        loadingDialog.dismissDialog();
                        Log.d(TAG, "Uploading banner was successful!");
                        Toast.makeText(CreateProfileActivity.this, "Banner image uploaded!", Toast.LENGTH_SHORT).show();

                        // set profile object's bannerImageURL and bannerImageName fields
                        profile.setBannerImageURL(task.getResult().toString());
                        profile.setBannerImageName(imageName);

                    } else if (!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        Log.d(TAG, "Task failed: " + task.getException().toString());
                        Toast.makeText(CreateProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(CreateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
}