package com.example.scratchappfeature;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import classes.Recipe;

public class SearchRecipeRVAdapter extends RecyclerView.Adapter<SearchRecipeRVAdapter.ViewHolder>{


    private static final String TAG = "SearchRecipeRVAdapter";
    private ArrayList<Recipe> mRecipes;
    private Context mContext;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OnItemClickListener mListener;


    public SearchRecipeRVAdapter(Context context, ArrayList<Recipe> recipes) {
        this.mRecipes = new ArrayList<>(recipes);
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
       mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_rv_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recipeNameTextView.setText(mRecipes.get(position).getName());
        holder.recipeDescriptionTextView.setText(mRecipes.get(position).getDescription());

        // get user avatar + name
        db.collection("profile")
                .document(mRecipes.get(position).getUser_ID())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()) {
                            holder.userNameTextView.setText(document.getString("pname")); // set user's name

                            // we are using Picasso to load profile images from URLs into an ImageView
                            if(document.getString("profileImageURL") != null) {
                                Picasso.with(mContext)
                                        .load(document.getString("profileImageURL"))
                                        .into(holder.userAvatarImageView);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, e.toString()));

        // we are using Picasso to load recipe images from URLs into an ImageView
        if(mRecipes.get(position).getImage_URL() != null) {
            Picasso.with(mContext)
                    .load(mRecipes.get(position).getImage_URL())
                    .into(holder.recipeImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView recipeNameTextView;
        private final TextView recipeDescriptionTextView;
        private final TextView userNameTextView;
        private final ImageView userAvatarImageView;
        private final ImageView recipeImageView;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            // initializing the views of the RecyclerView
            recipeNameTextView = itemView.findViewById(R.id.feedRecipeNameTextView);
            recipeDescriptionTextView = itemView.findViewById(R.id.feedRecipeDescriptionTextView);
            userNameTextView = itemView.findViewById(R.id.feedUserNameTextView);
            userAvatarImageView = itemView.findViewById(R.id.feedUserAvatarImageView);
            recipeImageView = itemView.findViewById(R.id.feedCardRecipeImageView);

            itemView.setOnClickListener(view -> {
                int position = getAbsoluteAdapterPosition();
                if(position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(position);
                }
            });
        }
    }
}
