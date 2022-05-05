package com.example.scratchappfeature;

import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.net.URI;
import java.util.UUID;

import classes.LoadingDialog;
import classes.Profile;
import classes.Posts;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityFeed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityFeed extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private String mParam2;

    private static final String TAG = "ProfilePage";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference activityStorageRef = storage.getReference("images/activityfeed");

    private final CollectionReference profilesCollection = db.collection("profile");
    private TextView profilename;
    private ImageView profilepic;
    private TextView status;
    private ImageView picture;
    private Profile profile;
    private Posts posts;
    private String userID;
    private Uri ImageUri;

    public void setProfile(Profile profile){
        this.profile = profile;
    }

    public void setPosts(Posts posts){this.posts=posts;}

    public ActivityFeed() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ActivityFeed.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityFeed newInstance(String param1) {
        ActivityFeed fragment = new ActivityFeed();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_activity_feed, container, false);
        profilename = view.findViewById(R.id.ActivityFeedProfileName);
        profilepic = view.findViewById(R.id.ActivityFeedProfilePic);
        status = view.findViewById(R.id.ActivityFeedPost);
        picture = view.findViewById(R.id.activitypicture);

        DocumentReference docRef1 = db.collection("profile").document(userID);
        docRef1.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    Log.i("ActivityFeed", "Found document!");

                    profile = document.toObject(Profile.class);

                    profilename.setText(profile.getpname());

                    downloadProfileImage();
                } else {
                    Log.e("ActivityFeed", "No such document.");
                }
            } else {
                Log.e("ActivityFeed", "get failed with" + task.getException());
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    public void downloadProfileImage(){
        try {
            // get the recipe document from the database
            DocumentReference downloadRef = profilesCollection.document(userID);

            downloadRef.get().addOnSuccessListener(documentSnapshot -> {
                String downloadUrl = documentSnapshot.getString("profileImageURL");
                Log.d("ActivityFeed", "profileImageURL: " + downloadUrl);

                // Glide makes it easy to load images into ImageViews
                if(downloadUrl != null) {
                    Glide.with(ActivityFeed.this)
                            .load(downloadUrl)
                            .into(profilepic);
                }

            }).addOnFailureListener(e -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}