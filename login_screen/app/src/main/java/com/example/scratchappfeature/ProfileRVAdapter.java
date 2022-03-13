package com.example.scratchappfeature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import classes.Profile;
import classes.Recipe;

public class ProfileRVAdapter extends RecyclerView.Adapter<ProfileRVAdapter.ProfileViewHolder>{
    private static final String TAG = "ProfileRVAdapter";
    private ArrayList<Profile> profileArrayList;
    private ArrayList<Profile> ProfileArrayListFull;
    private Context context;
    private String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ProfileRVAdapter.OnItemClickListener mListener;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReference("images/");

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ProfileRVAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views of recycler items.
        private final TextView profileNameTextView;
        private Button followBTN;
        private final ImageView profileImageView;
        private boolean followed;
        //private final ImageButton removeBtn;

        public ProfileViewHolder(@NonNull View itemView, ProfileRVAdapter.OnItemClickListener listener) {
            super(itemView);
            // initializing the views of the RecyclerView
            profileNameTextView = itemView.findViewById(R.id.profileNameTextView);
            followBTN = itemView.findViewById(R.id.exploreFollowBTN);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            //removeBtn = itemView.findViewById(R.id.deleteRecipeImageButton);
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
    //leave this out for now
    private void updateAt(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, profileArrayList.size());

        Profile profileToFollow = profileArrayList.get(position);
        String docID = profileToFollow.getUserID();
        if(profileToFollow.getFollowers().containsKey(currentUser)){
            profileToFollow.removeFollowers(currentUser);
        }else{
            profileToFollow.addFollowers(currentUser);
            db.collection("profile").document(docID).update("followers", currentUser )
                    .addOnSuccessListener(unused -> Log.i(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating document", e));
        }
    }

    // constructor class for our Adapter
    public ProfileRVAdapter(ArrayList<Profile> profileArrayList, Context context) {
        this.profileArrayList = profileArrayList;
        ProfileArrayListFull = new ArrayList<>(profileArrayList); // create a copy that doesn't point to the same ArrayList
        this.context = context;
    }

    public ProfileRVAdapter(View inflate) {

    }

    @NonNull
    @Override
    public ProfileRVAdapter.ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        View v = LayoutInflater.from(context).inflate(R.layout.profile_source_item, parent, false);
        return new ProfileRVAdapter.ProfileViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileRVAdapter.ProfileViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // setting data to our views in RecyclerView items
        Profile profile = profileArrayList.get(position);
        holder.profileNameTextView.setText(profile.getpname());
        holder.followBTN.setOnClickListener(v -> {
            //removeAt(position);//i is your adapter position
            if(holder.followed == false){
                holder.followBTN.setText("Following");
                holder.followed = true;
                //updateAt(position);
            }
            else{
                holder.followBTN.setText("Follow");
                holder.followed = false;
                //updateAt(position);
            }

        });

        // we are using Picasso to load images from URLs into an ImageView
        if(profile.getProfileImageURL() != null) {
            Picasso.with(context.getApplicationContext())
                    .load(profile.getProfileImageURL())
                    .into(holder.profileImageView);
        }
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return profileArrayList.size();
    }

    /*
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }*/

    /*private final Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Recipe> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(profileArrayListFull);
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

    };*/
}
