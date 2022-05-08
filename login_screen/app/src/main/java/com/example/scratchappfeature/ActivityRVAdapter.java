package com.example.scratchappfeature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

import classes.Posts;
import classes.Profile;

public class ActivityRVAdapter extends RecyclerView.Adapter<ActivityRVAdapter.ActivityViewHolder> {
    private static final String TAG = "ActivityRVAdapter";
    private ArrayList<Posts> postsArrayList;
    private ArrayList<Posts> postsArrayListFull;
    private Context context;
    private String userID;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReference("images/");

    public void setUserID(String userID){
        this.userID=userID;
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder{

        private final TextView profilename;
        private final ImageView profilepic;
        private final TextView status;
        private final ImageView picture;
        private Profile profile;
        private Posts posts;
        private Uri ImageUri;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.ActivityFeedPost);
            profilepic = itemView.findViewById(R.id.ActivityFeedProfilePic);
            profilename = itemView.findViewById(R.id.ActivityFeedProfileName);
            picture = itemView.findViewById(R.id.activitypicture);

        }
    }

    public ActivityRVAdapter(ArrayList<Posts> postsArrayList, Context context){
        this.postsArrayList = postsArrayList;
        Log.d(TAG, postsArrayList.toString());
        postsArrayListFull = new ArrayList<>(postsArrayList);
        this.context=context;
    }

    public ActivityRVAdapter(View inflate){

    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activityrv, parent, false);
        return new ActivityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityRVAdapter.ActivityViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Posts posts = postsArrayList.get(position);
        setAvatar(holder, posts.getUserID());
        holder.status.setText((posts.getDescription()));
        setPicture(holder, posts.getDocumentID());
    }

    @Override
    public int getItemCount() {
        return postsArrayList.size();
    }
//    @Override
//    public Filter getFilter(){return exampleFilter;}
//
//    private final Filter exampleFilter = new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence charSequence){
//            ArrayList<Posts> filteredlist = new ArrayList<>();
//        }
//    }

    private void setAvatar(@NonNull ActivityRVAdapter.ActivityViewHolder holder, String user_Id){
        Log.d(TAG, "user_Id: " + user_Id);
        db.collection("profile")
                .document(user_Id)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()) {
                            holder.profilename.setText(document.getString("pname")); // set user's name

                            // we are using Picasso to load profile images from URLs into an ImageView
                            if(document.getString("profileImageURL") != null) {
                                Picasso.with(context)
                                        .load(document.getString("profileImageURL"))
                                        .into(holder.profilepic);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, e.toString()));
    }

    private void setPicture(@NonNull ActivityRVAdapter.ActivityViewHolder holder, String documentID){
        Log.d(TAG, "documentID " + documentID);
        db.collection("activityfeed")
                .document(documentID)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()) {
                            // we are using Picasso to load profile images from URLs into an ImageView
                            if(document.getString("imageURL") != null) {
                                Picasso.with(context)
                                        .load(document.getString("imageURL"))
                                        .into(holder.picture);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, e.toString()));
    }
}
