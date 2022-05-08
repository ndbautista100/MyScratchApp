package com.example.scratchappfeature;

import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.net.URI;
import java.util.ArrayList;
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

    private static final String TAG = "ActivityFeed";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference activityStorageRef = storage.getReference("images/activityfeed");
    private DocumentReference profileref;

    private final CollectionReference profilesCollection = db.collection("profile");
    private ArrayList<Posts> userposts;
    private RecyclerView activityRV;
    private ActivityRVAdapter adapter;
    private Profile profile;
    private Posts posts;
    private String userID;
    private Uri ImageUri;

    public void setUserID(String userID){
        this.userID = userID;
    }

    public void setPosts(Posts posts){this.posts=posts;}

    public ActivityFeed() {
        // Required empty public constructor
    }


    public static ActivityFeed newInstance() {
        ActivityFeed fragment = new ActivityFeed();
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
        activityRV = view.findViewById(R.id.activityRecyclerView);

        activityRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        showPosts();
        return view;
    }


    public void showPosts() {
        userposts = new ArrayList<>();

        db.collection("activityfeed")
                .whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null || !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Posts posts = document.toObject(Posts.class);
                                userposts.add(posts);
                            }
                            adapter = new ActivityRVAdapter(userposts, getActivity().getApplicationContext());
                            adapter.setUserID(userID);

                            activityRV.setAdapter(adapter);
                        }
                    }else{
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}