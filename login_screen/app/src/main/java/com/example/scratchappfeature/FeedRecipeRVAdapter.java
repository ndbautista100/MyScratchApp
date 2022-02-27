package com.example.scratchappfeature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import classes.Recipe;

public class FeedRecipeRVAdapter extends RecyclerView.Adapter<FeedRecipeRVAdapter.FeedRecipeViewHolder> implements Filterable {
    private ArrayList<Recipe> recipeArrayList;
    private ArrayList<Recipe> recipeArrayListFull;
    private Context context;
    private OnItemClickListener mListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class FeedRecipeViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views of recycler items.
        private TextView recipeNameTextView;
        private TextView recipeDescriptionTextView;
        private TextView userTextView;
        private ImageView recipeImageView;

        public FeedRecipeViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            // initializing the views of the RecyclerView
            recipeNameTextView = itemView.findViewById(R.id.feedRecipeNameTextView);
            recipeDescriptionTextView = itemView.findViewById(R.id.feedRecipeDescriptionTextView);
            userTextView = itemView.findViewById(R.id.feedUserTextView);
            recipeImageView = itemView.findViewById(R.id.feedCardRecipeImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    // constructor class for our Adapter
    public FeedRecipeRVAdapter(ArrayList<Recipe> recipeArrayList, Context context) {
        this.recipeArrayList = recipeArrayList;
        recipeArrayListFull = new ArrayList<>(recipeArrayList); // create a copy that doesn't point to the same ArrayList
        this.context = context;
    }

    public FeedRecipeRVAdapter(View inflate) {

    }

    @NonNull
    @Override
    public FeedRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        View view = LayoutInflater.from(context).inflate(R.layout.feed_rv_item, parent, false);
        FeedRecipeViewHolder recipeViewHolder = new FeedRecipeViewHolder(view, mListener);
        return recipeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedRecipeRVAdapter.FeedRecipeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // setting data to our views in RecyclerView items
        Recipe recipe = recipeArrayList.get(position);
        holder.recipeNameTextView.setText(recipe.getName());
        holder.recipeDescriptionTextView.setText(recipe.getDescription());
        holder.userTextView.setText(recipe.getUser_ID()); // TEMPORARY - replace with username later

        // we are using Picasso to load images from URLs into an ImageView
        if(recipe.getImage_URL() != null) {
            Picasso.with(context.getApplicationContext())
                .load(recipe.getImage_URL())
                .into(holder.recipeImageView);
        }
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return recipeArrayList.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        // if the user reaches the bottom of the feed by getting a null item, return the loading int
//        return recipeArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
//    }

    // Search for recipe
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Recipe> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(recipeArrayListFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase(Locale.ROOT).trim();

                for(Recipe recipe : recipeArrayListFull) {
                    if(recipe.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(recipe);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            recipeArrayList.clear();
            recipeArrayList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}