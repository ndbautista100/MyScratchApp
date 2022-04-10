package com.example.scratchappfeature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import classes.Profile;
import classes.Recipe;

public class ProfileRVAdapter extends RecyclerView.Adapter<ProfileRVAdapter.ProfileViewHolder>{
    private static final String TAG = "ProfileRVAdapter";
    private ArrayList<Profile> profileArrayList;
    private ArrayList<Profile> ProfileArrayListFull;
    private Context context;
    private Profile loggedUser;
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
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        //notifyItemRemoved(position);
        //notifyItemRangeChanged(position, profileArrayList.size());

        Profile profileToFollow = profileArrayList.get(position);
        String docID = profileToFollow.getUserID();
        String currentUSERID = currentUser;
        if(profileToFollow.getFollowers().containsKey(currentUser)){
            //remove the follower
            profileToFollow.removeFollowers(currentUser);
            // get ref to the user that is logged in
            DocumentReference docRef = db.collection("profile").document(currentUSERID);

            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        loggedUser = document.toObject(Profile.class);
                        loggedUser.removeFollowing(profileToFollow.getUserID());
                        ObjectMapper oMapper = new ObjectMapper();
                        Map<String, Object> profileMap = oMapper.convertValue(loggedUser, Map.class);
                        db.collection("profile").document(currentUSERID).set(profileMap)
                                .addOnSuccessListener(documentReference -> {
                                    // update the newly added document to set its document ID - on the Java object and Firebase document reference



                                });
                    } else {
                        Log.e(TAG, "No such document.");
                    }
                } else {
                    Log.e(TAG, "get failed with" + task.getException());
                }
            });
            // map profile to object
            ObjectMapper oMapper = new ObjectMapper();
            Map<String, Object> profileMap = oMapper.convertValue(profileToFollow, Map.class);
            db.collection("profile").document(docID).set(profileMap)
                    .addOnSuccessListener(documentReference -> {
                        // update the newly added document to set its document ID - on the Java object and Firebase document reference



                    });
        }else{
            //add the follower
            profileToFollow.addFollowers(currentUser);
            // get ref to the user that is logged in
            DocumentReference docRef = db.collection("profile").document(currentUSERID);

            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                       loggedUser = document.toObject(Profile.class);
                       loggedUser.addFollowing(profileToFollow.getUserID());
                        ObjectMapper oMapper = new ObjectMapper();
                        Map<String, Object> profileMap = oMapper.convertValue(loggedUser, Map.class);
                        db.collection("profile").document(currentUSERID).set(profileMap)
                                .addOnSuccessListener(documentReference -> {
                                    // update the newly added document to set its document ID - on the Java object and Firebase document reference



                                });
                    } else {
                        Log.e(TAG, "No such document.");
                    }
                } else {
                    Log.e(TAG, "get failed with" + task.getException());
                }
            });
            // map the profile class to an object
            ObjectMapper oMapper = new ObjectMapper();
            Map<String, Object> profileMap = oMapper.convertValue(profileToFollow, Map.class);
            db.collection("profile").document(docID).set(profileMap)
                    .addOnSuccessListener(documentReference -> {
                        // update the newly added document to set its document ID - on the Java object and Firebase document reference



                    });
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
                updateAt(position);
            }
            else{
                holder.followBTN.setText("Follow");
                holder.followed = false;
                updateAt(position);
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

    private void removeAt(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, profileArrayList.size());
        profileArrayList.remove(position);
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
