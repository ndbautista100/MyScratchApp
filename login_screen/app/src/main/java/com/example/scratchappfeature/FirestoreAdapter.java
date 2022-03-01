package com.example.scratchappfeature;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;
import android.content.Context;

import classes.Recipe;

public class FirestoreAdapter extends FirestorePagingAdapter<Recipe, FirestoreAdapter.RecipeViewHolder> {
    private static final String TAG = "FirestoreAdapter";
    private final Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /** Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}. */
    public FirestoreAdapter(@NonNull FirestorePagingOptions<Recipe> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecipeViewHolder holder, int position, @NonNull Recipe model) {
        holder.recipeNameTextView.setText(model.getName());
        holder.recipeDescriptionTextView.setText(model.getDescription());
        holder.userTextView.setText(model.getUser_ID()); // TEMPORARY - replace with username later

        // we are using Picasso to load images from URLs into an ImageView
        if(model.getImage_URL() != null) {
            Picasso.with(context)
                .load(model.getImage_URL())
                .into(holder.recipeImageView);
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Method onCreateViewHolder called.");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_rv_item, parent, false);
        return new RecipeViewHolder(view, mListener);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views of recycler items.
        private final TextView recipeNameTextView;
        private final TextView recipeDescriptionTextView;
        private final TextView userTextView;
        private final ImageView recipeImageView;

        public RecipeViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            // initializing the views of the RecyclerView
            recipeNameTextView = itemView.findViewById(R.id.feedRecipeNameTextView);
            recipeDescriptionTextView = itemView.findViewById(R.id.feedRecipeDescriptionTextView);
            userTextView = itemView.findViewById(R.id.feedUserTextView);
            recipeImageView = itemView.findViewById(R.id.feedCardRecipeImageView);

            itemView.setOnClickListener(view -> {
                int position = getAbsoluteAdapterPosition();
                if(position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getItem(getAbsoluteAdapterPosition()), position);
                }
            });
        }
    }
}
