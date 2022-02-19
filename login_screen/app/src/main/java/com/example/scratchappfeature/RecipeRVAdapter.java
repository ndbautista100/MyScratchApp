package com.example.scratchappfeature;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import classes.Recipe;

public class RecipeRVAdapter extends RecyclerView.Adapter<RecipeRVAdapter.RecipeViewHolder> {
    private ArrayList<Recipe> dataModalArrayList;
    private Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views of recycler items.
        private TextView recipeNameTextView;
        private TextView recipeDescriptionTextView;
        private ImageView recipeImageView;

        public RecipeViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            // initializing the views of the RecyclerView
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            recipeDescriptionTextView = itemView.findViewById(R.id.recipeDescriptionTextView);
            recipeImageView = itemView.findViewById(R.id.cardRecipeImageView);

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
    public RecipeRVAdapter(ArrayList<Recipe> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    public RecipeRVAdapter(View inflate) {

    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        View v = LayoutInflater.from(context).inflate(R.layout.image_rv_item, parent, false);
        RecipeViewHolder recipeViewHolder = new RecipeViewHolder(v, mListener);
        return recipeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeRVAdapter.RecipeViewHolder holder, int position) {
        // setting data to our views in RecyclerView items
        Recipe modal = dataModalArrayList.get(position);
        holder.recipeNameTextView.setText(modal.getName());
        holder.recipeDescriptionTextView.setText(modal.getDescription());

        // we are using Picasso to load images from URLs into an ImageView
        if(modal.getImage_URL() != null) {
            Picasso.with(context.getApplicationContext())
                .load(modal.getImage_URL())
                .into(holder.recipeImageView);
        }
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }
}