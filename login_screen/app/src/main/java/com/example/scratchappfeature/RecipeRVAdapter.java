package com.example.scratchappfeature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import classes.Recipe;

public class RecipeRVAdapter extends RecyclerView.Adapter<RecipeRVAdapter.RecipeViewHolder> implements Filterable {
    private static final String TAG = "RecipeRVAdapter";
    private ArrayList<Recipe> recipeArrayList;
    private ArrayList<Recipe> recipeArrayListFull;
    private Context context;
    private OnItemClickListener mListener;
    private String userID;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReference("images/");

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public void setProfileID(String profileID){ this.userID = profileID;}
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views of recycler items.
        private final TextView recipeNameTextView;
        private final TextView recipeDescriptionTextView;
        private final ImageView recipeImageView;
        private final ImageView userProfilePicture;
        private final TextView userProfileName;
        private final CardView cardItem;


        public RecipeViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            // initializing the views of the RecyclerView
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            recipeDescriptionTextView = itemView.findViewById(R.id.recipeDescriptionTextView);
            recipeImageView = itemView.findViewById(R.id.cardRecipeImageView);
            userProfilePicture = itemView.findViewById(R.id.scratchNotesAvatarImageView);
            userProfileName = itemView.findViewById(R.id.scratchNotesNameTextView);
            cardItem = itemView.findViewById(R.id.imageRVItemLayout);
            itemView.setOnClickListener(view -> {
                if(listener != null) {
                    int position = getAbsoluteAdapterPosition();
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

        Recipe recipeToDelete = recipeArrayList.get(position);
        String docID = recipeToDelete.getDocument_ID();
        String userIDToDelete = recipeToDelete.getUser_ID();

        //Determine if the recipe to be deleted is the user or another
        //The user can only delete their own, trying to delete another
        //will delete it from their saved recipes
        if (!userIDToDelete.equals(userID)){
            final Map<String, Object> savedRecipes = new HashMap<>();
            savedRecipes.put("savedRecipes", FieldValue.arrayRemove(docID));
            db.collection("profile")
                    .document(userID)
                    .update(savedRecipes);
        } else {
            db.collection("recipes").document(docID)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Log.i(TAG, "DocumentSnapshot successfully deleted!");
                        // Delete the image file as well after deleting the recipe
                        if (recipeToDelete.getImageName() != null) { // first check if the recipe has an image
                            StorageReference recipeImageRef = storageReference.child(recipeToDelete.getImageName());
                            Log.d(TAG, "Deleting image: " + recipeToDelete.getImageName());
                            recipeImageRef.delete()
                                    .addOnSuccessListener(unused1 -> Log.i(TAG, "Successfully deleted image: " + recipeToDelete.getImageName()))
                                    .addOnFailureListener(e -> Log.e(TAG, e.toString()));
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error deleting document", e));
        }
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
        setAvatar(holder, recipe.getUser_ID());
        holder.recipeNameTextView.setText(recipe.getName());
        holder.recipeDescriptionTextView.setText(recipe.getDescription());
        //Long click on a card item
        holder.cardItem.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick (View view){
                showMenu(view, position);
                return true;
            }
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

    private void setAvatar(@NonNull RecipeRVAdapter.RecipeViewHolder holder, String user_Id){

        db.collection("profile")
                .document(user_Id)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()) {
                            holder.userProfileName.setText(document.getString("pname")); // set user's name

                            // we are using Picasso to load profile images from URLs into an ImageView
                            if(document.getString("profileImageURL") != null) {
                                Picasso.with(context)
                                        .load(document.getString("profileImageURL"))
                                        .into(holder.userProfilePicture);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, e.toString()));
    }


    private void showMenu(View v, int position){
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_delete, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.deleteRecipe) {
                    removeAt(position);
                }
                return true;
            }
        });
        popupMenu.show();
    }
}