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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import classes.Recipe;

public class RecipeRVAdapter extends RecyclerView.Adapter<RecipeRVAdapter.RecipeViewHolder> implements Filterable {
    private static final String TAG = "RecipeRVAdapter";
    private ArrayList<Recipe> recipeArrayList;
    private ArrayList<Recipe> recipeArrayListFull;
    private Context context;
    private OnItemClickListener mListener;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views of recycler items.
        private final TextView recipeNameTextView;
        private final TextView recipeDescriptionTextView;
        private final ImageView recipeImageView;
        private final ImageButton removeBtn;

        public RecipeViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            // initializing the views of the RecyclerView
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            recipeDescriptionTextView = itemView.findViewById(R.id.recipeDescriptionTextView);
            recipeImageView = itemView.findViewById(R.id.cardRecipeImageView);
            removeBtn = itemView.findViewById(R.id.deleteRecipeImageButton);
            itemView.setOnClickListener(view -> {
                if(listener != null) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    private void removeAt(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, recipeArrayList.size());
        String docID = recipeArrayList.get(position).getDocument_ID();
        db.collection("recipes").document(docID).delete().addOnSuccessListener(aVoid -> Log.i(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting document", e));
        recipeArrayList.remove(position);
    }

    // constructor class for our Adapter
    public RecipeRVAdapter(ArrayList<Recipe> recipeArrayList, Context context) {
        this.recipeArrayList = recipeArrayList;
        recipeArrayListFull = new ArrayList<>(recipeArrayList); // create a copy that doesn't point to the same ArrayList
        this.context = context;
    }

    public RecipeRVAdapter(View inflate) {

    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        View v = LayoutInflater.from(context).inflate(R.layout.image_rv_item, parent, false);
        return new RecipeViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeRVAdapter.RecipeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // setting data to our views in RecyclerView items
        Recipe recipe = recipeArrayList.get(position);
        holder.recipeNameTextView.setText(recipe.getName());
        holder.recipeDescriptionTextView.setText(recipe.getDescription());
        holder.removeBtn.setOnClickListener(v -> {
            removeAt(position);//i is your adapter position
        });

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

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private final Filter exampleFilter = new Filter() {
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