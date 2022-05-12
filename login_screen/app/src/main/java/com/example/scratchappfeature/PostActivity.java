package com.example.scratchappfeature;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import classes.LoadingDialog;
import classes.Posts;
import classes.Profile;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostActivity extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final CollectionReference profilesCollection = db.collection("profile");

    private static final String TAG = "PostActivity";
    private TextView profilename;
    private ImageView profilepic;
    private EditText status;
    private ImageView addpostimage;
    private ImageView picture;
    private Profile profile;
    private String userID;
    private Posts posts;
    private Button submitbutton;
    private Uri imageLocationUri;

    private DocumentReference profileref;
    private final StorageReference storageReference = storage.getReference("images/activityfeed");

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result != null){
                picture.setImageURI(result);
                imageLocationUri = result;

                Log.d(TAG, imageLocationUri.toString());

                addImage();
            }
        }
    });

    public PostActivity() {
        // Required empty public constructor
    }

    public static PostActivity newInstance() {
        PostActivity fragment = new PostActivity();
        return fragment;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view1 = inflater.inflate(R.layout.fragment_post_activity, container, false);

        profilepic = view1.findViewById(R.id.ActivityFeedProfilePic);
        profilename = view1.findViewById(R.id.ActivityFeedProfileName);

        status =  view1.findViewById(R.id.ActivityFeedPost);

        posts = new Posts();
        posts.setUserID(userID);

        picture = view1.findViewById(R.id.activitypicture);

        addpostimage = view1.findViewById(R.id.Postaddimage);
        addpostimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        profileref = db.collection("profile").document(userID);
        profileref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if(doc.exists()){
                            profile = doc.toObject(Profile.class);
                            profilename.setText(profile.getpname());

                        }
                        else {
                            Log.d(TAG, "No such info");
                        }
                    }
                    else {
                        Log.d(TAG, "failed to get with", task.getException());
                    }
                });
        downloadProfileImage();
        //downloadImage();

        submitbutton = view1.findViewById(R.id.postsubmitbutton);
        submitbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                addDatatoFirebase();
            }
        });
        return view1;
    }

    public void addImage() {
        Log.d(TAG, "Post has an image: " + posts.getImageName());
        if(posts.getImageName() != null) { // if recipe already has an image
            // delete that image from Firebase Storage
            StorageReference recipeImageRef = storageReference.child(posts.getImageName());
            Log.d(TAG, "Deleting image: " + posts.getImageName());
            recipeImageRef.delete()
                    .addOnSuccessListener(unused1 -> Log.i(TAG, "Successfully deleted image: " + posts.getImageName()))
                    .addOnFailureListener(e -> Log.e(TAG, e.toString()));
        }

        try { // uploading the new image
            if(imageLocationUri != null) {
                LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                loadingDialog.startLoadingDialog();

                String imageName = posts.getUserID() + "_" + UUID.randomUUID().toString() + "." + getExtension(imageLocationUri);
                StorageReference imageReference = storageReference.child(imageName);

                UploadTask uploadTask = imageReference.putFile(imageLocationUri);
                uploadTask.continueWithTask(task -> {
                    if(!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        //store image
                        posts.setImageURL(task.getResult().toString());
                        posts.setImageName(imageName);
                        loadingDialog.dismissDialog();
                    } else if(!task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadImage() {
        try {
            // get the recipe document from the database
            DocumentReference downloadRef = db.collection("posts").document(posts.getDocumentID());

            downloadRef.get().addOnSuccessListener(documentSnapshot -> {
                String downloadUrl = documentSnapshot.getString("image_URL");

                // Glide makes it easy to load images into ImageViews
                if(downloadUrl != null) {
                    Glide.with(getActivity())
                            .load(downloadUrl)
                            .into(picture);
                }

            }).addOnFailureListener(e -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private String getExtension(Uri uri) {
        try {
            ContentResolver contentResolver = getActivity().getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

        } catch (Exception e) {
            return null;
        }
    }

    public void downloadProfileImage(){
        try {
            // get the recipe document from the database
            DocumentReference downloadRef = profilesCollection.document(userID);

            downloadRef.get().addOnSuccessListener(documentSnapshot -> {
                String downloadUrl = documentSnapshot.getString("profileImageURL");
                Log.d(TAG, "profileImageURL: " + downloadUrl);

                // Glide makes it easy to load images into ImageViews
                if(downloadUrl != null) {
                    Glide.with(getActivity())
                            .load(downloadUrl)
                            .into(profilepic);
                }

            }).addOnFailureListener(e -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private void addDatatoFirebase(){
        posts = new Posts(status.getText().toString(), posts.getImageURL(), posts.getImageName(), userID);

        db.collection("activityfeed").add(posts).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                documentReference.update("documentID", documentReference.getId());
                Toast.makeText(getActivity(),"Your Posts have been added.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Your Posts has not been added.", Toast.LENGTH_SHORT).show();
            }
        });

        returnToMainActivity();
    }

    public void returnToMainActivity () {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}