package com.example.scratchappfeature;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import classes.Posts;

public class ActivityFeedAdapter {//extends RecyclerView.Adapter<ActivityFeedAdapter.ActivityFeedViewHolder> implements Filterable {

//    public static class ActivityFeedViewHolder extends RecyclerView.ViewHolder{
//        public ActivityFeedViewHolder(@NonNull View itemView) {
//            super(itemView);
//        }
//    }

}
