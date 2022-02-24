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
    private ArrayList<Recipe> dataModalArrayList;
    private ArrayList<Recipe> dataModalArrayListFull;
    private Context context;
    private OnItemClickListener mListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        private ImageView recipeImageView;

        public FeedRecipeViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            // initializing the views of the RecyclerView
            recipeNameTextView = itemView.findViewById(R.id.feedRecipeNameTextView);
            recipeDescriptionTextView = itemView.findViewById(R.id.feedRecipeDescriptionTextView);
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

    private void removeAt(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataModalArrayList.size());
        String docID = dataModalArrayList.get(position).getDocument_ID();
        db.collection("recipes").document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("DeleteTag", "DocumentSnapshot successfully deleted!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DeleteTag", "Error deleting document", e);
                    }
                });
        dataModalArrayList.remove(position);
    }

    // constructor class for our Adapter
    public FeedRecipeRVAdapter(ArrayList<Recipe> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        dataModalArrayListFull = new ArrayList<>(dataModalArrayList); // create a copy that doesn't point to the same ArrayList
        this.context = context;
    }

    public FeedRecipeRVAdapter(View inflate) {

    }

    @NonNull
    @Override
    public FeedRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        View v = LayoutInflater.from(context).inflate(R.layout.feed_rv_item, parent, false);
        FeedRecipeViewHolder recipeViewHolder = new FeedRecipeViewHolder(v, mListener);
        return recipeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedRecipeRVAdapter.FeedRecipeViewHolder holder, @SuppressLint("RecyclerView") int position) {
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

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Recipe> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(dataModalArrayListFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase(Locale.ROOT).trim();

                for(Recipe recipe : dataModalArrayListFull) {
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
            dataModalArrayList.clear();
            dataModalArrayList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}