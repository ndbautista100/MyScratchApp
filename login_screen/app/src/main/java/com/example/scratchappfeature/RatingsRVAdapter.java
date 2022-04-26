package com.example.scratchappfeature;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
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

import classes.RateComment;
import classes.Recipe;
public class RatingsRVAdapter extends RecyclerView.Adapter<RatingsRVAdapter.RatingViewHolder>
{
    //Private Variables
    private static final String TAG = "RatingsRVAdapter";
    private Context context;
    private ArrayList<RateComment> rateCommentArrayList;
    private ArrayList<RateComment> rateCommentArrayListFull;
    private String userID;
    private String mProfileID;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private RatingsRVAdapter.OnItemClickListener mListener;

    //------
    public RatingsRVAdapter(ArrayList<RateComment> ratingArrayList, Context context) {
        this.rateCommentArrayList = rateCommentArrayList;
        // create a copy that doesn't point to the same ArrayList
        rateCommentArrayListFull = new ArrayList<>(ratingArrayList);
        this.context = context;
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setProfileID(String profileID){
        this.userID = profileID;
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public static class RatingViewHolder extends RecyclerView.ViewHolder{
        private final TextView ratingNumTextView;
        private final TextView ratingCommentTextView;
        private final TextView userProfileName;
        private final CardView cardItem;
        public RatingViewHolder(@NonNull View itemView, RatingsRVAdapter.OnItemClickListener listener)
        {
            super(itemView);
            ratingNumTextView = itemView.findViewById(R.id.ratingItem_Num);
            ratingCommentTextView = itemView.findViewById(R.id.ratingItem_Comment);
            userProfileName = itemView.findViewById(R.id.ratingItem_Person);
            cardItem = itemView.findViewById(R.id.ratingRVItem);
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
    //Grabbing layout information from rate_item.xml for displaying rating card item
    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rate_item, parent, false);
        return new RatingViewHolder(view, mListener);
    }
    //Setting info + additional
    public void onBindViewHolder(@NonNull RatingsRVAdapter.RatingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //Set info
        RateComment rateCommentThis = rateCommentArrayList.get(position);
        holder.ratingNumTextView.setText(rateCommentThis.getRatingNum());
        holder.ratingCommentTextView.setText(rateCommentThis.getRatingTextComment());
        holder.userProfileName.setText(rateCommentThis.getUserID());
        //Long Click for pop up
        holder.cardItem.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick (View view){
                showMenu(view, position);
                return true;
            }
        });
    }
    @Override
    public int getItemCount(){
        return 0;
    }
    //WORK ON
    private void removeAt(int position) {

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
